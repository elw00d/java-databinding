package binding;

import binding.adapters.IBindingAdapter;
import binding.converters.ConversionResult;
import binding.converters.IBindingConverter;
import binding.observables.IObservableList;
import binding.observables.IObservableListListener;
import binding.utils.PropertyInfo;
import binding.utils.PropertyUtils;
import binding.validators.IBindingValidator;
import binding.validators.ValidationResult;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

/**
 * Provides data sync connection between two objects - source and target. Both source and target can be just objects,
 * but if you want to bind to object that does not implement {@link INotifyPropertyChanged},
 * you should use it as target and use appropriate adapter ({@link IBindingAdapter} implementation). One Binding instance connects
 * one source property and one target property.
 *
 * @author igor.kostromin
 *         26.06.13 15:57
 */
public class BindingBase {

    protected Object target;
    private String targetProperty;
    protected INotifyPropertyChanged source;
    private String sourceProperty;
    private boolean bound;
    private BindingMode mode;
    protected BindingMode realMode;
    private BindingSettingsBase settings;
    protected boolean targetIsUi;

    protected IBindingAdapter adapter;
    private PropertyInfo targetPropertyInfo;
    private PropertyInfo sourcePropertyInfo;

    // converts target to source and back
    private IBindingConverter converter;

    protected IPropertyChangedListener sourceListener;
    protected IPropertyChangedListener targetListener;
    // used instead targetListener if target does not implement INotifyPropertyChanged
    protected Object targetListenerWrapper;

    // flags used to avoid infinite recursive loop
    private boolean ignoreSourceListener;
    protected boolean ignoreTargetListener;

    private IBindingResultListener resultListener;
    private IBindingValidator validator;

    // collections synchronization support
    private boolean sourceIsObservable;
    private boolean targetIsObservable;
    protected SourceListListener sourceListListener;
    protected IObservableList sourceList;
    protected IObservableList targetList;
    protected TargetListListener targetListListener;

    private boolean updateSourceIfBindingFails = true;

    /**
     * If target value conversion or validation fails, the source property will be set to null
     * if this flag is set to true. Otherwise the source property setter won't be called.
     * Default value is true
     */
    public boolean isUpdateSourceIfBindingFails() {
        return updateSourceIfBindingFails;
    }

    /**
     * Set the updateSourceIfBindingFails flag.
     * See {@link #isUpdateSourceIfBindingFails()} to view detailed description.
     */
    public void setUpdateSourceIfBindingFails( boolean updateSourceIfBindingFails ) {
        this.updateSourceIfBindingFails = updateSourceIfBindingFails;
    }

    /**
     * Returns binding result listener.
     */
    public IBindingResultListener getResultListener() {
        return resultListener;
    }

    /**
     * Sets binding result listener.
     */
    public void setResultListener( IBindingResultListener resultListener ) {
        this.resultListener = resultListener;
    }

    /**
     * Returns validator.
     */
    public IBindingValidator getValidator() {
        return validator;
    }

    /**
     * Sets the validator.
     */
    public void setValidator( IBindingValidator validator ) {
        this.validator = validator;
    }

    public BindingBase( Object target, String targetProperty, INotifyPropertyChanged source, String sourceProperty ) {
        this(target, targetProperty, source, sourceProperty, BindingMode.Default );
    }

    public BindingBase( Object target, String targetProperty, INotifyPropertyChanged source,
                        String sourceProperty, BindingMode mode ) {
        this(target, targetProperty, source, sourceProperty, mode, BindingSettingsBase.DEFAULT_SETTINGS);
    }

    public BindingBase( Object target, String targetProperty, INotifyPropertyChanged source,
                        String sourceProperty, BindingMode mode, BindingSettingsBase settings ) {
        if (null == target) throw new IllegalArgumentException( "target is null" );
        if (null == targetProperty || targetProperty.length() == 0) throw new IllegalArgumentException( "targetProperty is null or empty" );
        if (null == source) throw new IllegalArgumentException( "source is null" );
        if (null == sourceProperty || sourceProperty.length() == 0) throw new IllegalArgumentException( "sourceProperty is null or empty" );
        //
        this.target = target;
        this.targetProperty = targetProperty;
        this.source = source;
        this.sourceProperty = sourceProperty;
        this.mode = mode;
        this.bound = false;
        this.settings = settings;
    }

    public class SourceChangeListener implements IPropertyChangedListener {
        public void propertyChanged( String propertyName ) {
            if (!ignoreSourceListener && propertyName.equals( sourceProperty ))
                updateTarget();
        }
    }

    /**
     * Forces a data transfer from the binding source property to the binding target property.
     */
    public void updateTarget() {
        if (realMode != BindingMode.OneTime && realMode != BindingMode.OneWay && realMode != BindingMode.TwoWay)
            throw new RuntimeException( String.format( "Cannot update target in %s binding mode.", realMode ) );
        ignoreTargetListener = true;
        try {
            Object sourceValue = sourcePropertyInfo.getter.invoke( source );
            if ( sourceIsObservable ) { // work with observable list
                // we should take target list and initialize it using source items
                final List targetList;
                if (adapter == null) {
                    targetList = (List) targetPropertyInfo.getter.invoke(target);
                } else {
                    targetList = (List) adapter.getValue(target, targetProperty);
                }
                if ( sourceValue == null ) {
                    if (null != targetList ) targetList.clear();
                } else {
                    if (null != targetList) {
                        targetList.clear();
                        targetList.addAll((List) sourceValue);

                        // subscribe to source list
                        if (sourceList != null ) {
                            sourceListListener.ban = true;
                            sourceList.removeObservableListListener(sourceListListener);
                            sourceList = null;
                        }
                        sourceList = (IObservableList) sourceValue;
                        sourceListListener = new SourceListListener(targetList);
                        sourceList.addObservableListListener(sourceListListener);
                    } else {
                        // todo : debug : target list is null, ignoring sync operation
                    }
                }
            } else { // work with usual property
                Object converted = sourceValue;
                // convert back if need
                if (null != converter) {
                    ConversionResult result = converter.convertBack( sourceValue );
                    if (!result.success) {
                        return;
                    }
                    converted = result.value;
                }
                //
                if (adapter == null)
                    targetPropertyInfo.setter.invoke( target, converted);
                else
                    adapter.setValue( target, targetProperty, converted );
                //
                if ( null != resultListener ) {
                    resultListener.onBinding( new BindingResult( false ) );
                }
            }
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch (InvocationTargetException e) {
            throw new RuntimeException( e );
        } finally {
            ignoreTargetListener = false;
        }
    }

    private class TargetListListener implements IObservableListListener {
        // to avoid side effects from old listeners
        // (can be reproduced if call raisePropertyChanged inside ObservableList handler)
        boolean ban = false;
        List sourceList;

        private TargetListListener(List sourceList) {
            this.sourceList = sourceList;
        }

        @Override
        public void listElementsAdded(IObservableList list, int index, int length) {
            if (ban) return;
            ignoreSourceListener = true;
            try {
                for (int i = index; i < list.size(); i++) sourceList.add(list.get(i));
            } finally {
                ignoreSourceListener = false;
            }
        }

        @Override
        public void listElementsRemoved(IObservableList list, int index, List oldElements) {
            if (ban) return;
            ignoreSourceListener = true;
            try {
                for (Object item : oldElements)
                    sourceList.remove(item);
            } finally {
                ignoreSourceListener = false;
            }
        }

        @Override
        public void listElementReplaced(IObservableList list, int index, Object oldElement) {
            if (ban) return;
            ignoreSourceListener = true;
            try {
                sourceList.set(index, list.get(index));
            } finally {
                ignoreSourceListener = false;
            }
        }
    }

    private class SourceListListener implements IObservableListListener {
        // to avoid side effects from old listeners
        // (can be reproduced if call raisePropertyChanged inside ObservableList handler)
        boolean ban = false;
        List targetList;

        private SourceListListener(List targetList) {
            this.targetList = targetList;
        }

        @Override
        public void listElementsAdded(IObservableList list, int index, int length) {
            if (ban) return;
            ignoreTargetListener = true;
            try {
                for (int i = index; i < list.size(); i++) targetList.add(list.get(i));
            } finally {
                ignoreTargetListener = false;
            }
        }

        @Override
        public void listElementsRemoved(IObservableList list, int index, List oldElements) {
            if (ban) return;
            ignoreTargetListener = true;
            try {
                for (Object item : oldElements)
                    targetList.remove(item);
            } finally {
                ignoreTargetListener = false;
            }
        }

        @Override
        public void listElementReplaced(IObservableList list, int index, Object oldElement) {
            if (ban) return;
            ignoreTargetListener = true;
            try {
                targetList.set(index, list.get(index));
            } finally {
                ignoreTargetListener = false;
            }
        }
    }

    /**
     * Sends the current binding target value to the binding source property in TwoWay or OneWayToSource bindings.
     */
    public void updateSource() {
        if (realMode != BindingMode.OneWayToSource && realMode != BindingMode.TwoWay)
            throw new RuntimeException( String.format( "Cannot update source in %s binding mode.", realMode ) );
        ignoreSourceListener = true;
        try {
            Object targetValue;
            if (null == adapter)
                targetValue = targetPropertyInfo.getter.invoke( target );
            else
                targetValue = adapter.getValue( target, targetProperty );
            //
            if ( targetIsObservable ) { // work with collection
                final List sourceList = (List) sourcePropertyInfo.getter.invoke(source);
                if (targetValue == null) {
                    if (null != sourceList) sourceList.clear();
                } else {
                    if (null != sourceList) {
                        sourceList.clear();
                        sourceList.addAll((Collection) targetValue);

                        // subscribe to source list
                        if (targetList != null ) {
                            sourceListListener.ban = true;
                            targetList.removeObservableListListener(sourceListListener);
                            targetList = null;
                        }
                        targetList = (IObservableList) targetValue;
                        targetListListener = new TargetListListener(sourceList);
                        targetList.addObservableListListener(targetListListener);
                    } else {
                        // todo : debug : source list is null, ignoring sync operation
                    }
                }
            } else { // work with usual property
                Object convertedValue = targetValue;
                // convert if need
                if (null != converter) {
                    ConversionResult result = converter.convert( targetValue );
                    if (!result.success) {
                        if (null != resultListener)
                            resultListener.onBinding( new BindingResult( true, false, result.failReason ) );
                        if ( updateSourceIfBindingFails ) {
                            sourcePropertyInfo.setter.invoke( source, (Object) null);
                        }
                        return;
                    }
                    convertedValue = result.value;
                }
                // validate if need
                if (null != validator) {
                    ValidationResult validationResult = validator.validate( convertedValue );
                    if (!validationResult.valid) {
                        if (null != resultListener)
                            resultListener.onBinding( new BindingResult( false, true, validationResult.message ) );
                        if ( updateSourceIfBindingFails ) {
                            sourcePropertyInfo.setter.invoke( source, (Object) null);
                        }
                        return;
                    }
                }
                sourcePropertyInfo.setter.invoke( source, convertedValue );
                if (null != resultListener)
                    resultListener.onBinding( new BindingResult( false ) );
                //
            }
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch (InvocationTargetException e) {
            throw new RuntimeException( e );
        } finally {
            ignoreSourceListener =false;
        }
    }

    public class TargetChangeListener implements IPropertyChangedListener {
        public void propertyChanged( String propertyName ) {
            if (!ignoreTargetListener && propertyName.equals( targetProperty ))
                updateSource();
        }
    }

//    private UpdateSourceTrigger getRealUpdateSourceTrigger() {
//        assert targetIsUi;
//        if (updateSourceTrigger != UpdateSourceTrigger.Default)
//            return updateSourceTrigger;
//        else {
//            UpdateSourceTrigger real = ((IUiBindingAdapter) adapter).getDefaultUpdateSourceTrigger();
//            if (real == UpdateSourceTrigger.Default) throw new AssertionError("Adapter cannot return UpdateSourceTrigger.Default");
//            return real;
//        }
//    }

    /**
     * Connects Source and Target objects.
     */
    public void bind() {
        // resolve binding mode and search converter if need
        if (targetIsUi) {
            adapter = settings.getAdapterFor(target.getClass());
            if ( mode == BindingMode.Default) {
                realMode = adapter.getDefaultMode();
            } else
                realMode = mode;
        } else {
            if (mode == BindingMode.Default)
                realMode = BindingMode.TwoWay;
            else
                realMode = mode;

            if (realMode == BindingMode.TwoWay || realMode == BindingMode.OneWayToSource) {
                if (! (target instanceof INotifyPropertyChanged))
                    adapter = settings.getAdapterFor( target.getClass() );
            }
        }

        // get properties info and check if they are collections
        sourcePropertyInfo = PropertyUtils.getProperty( source.getClass(), sourceProperty );
        if (null == adapter)
            targetPropertyInfo = PropertyUtils.getProperty( target.getClass(), targetProperty );

        Class<?> targetPropertyClass = (null == adapter) ? targetPropertyInfo.clazz : adapter.getTargetPropertyClazz(targetProperty);

        sourceIsObservable = IObservableList.class.isAssignableFrom( sourcePropertyInfo.clazz );
        targetIsObservable = IObservableList.class.isAssignableFrom( targetPropertyClass );

        // we need converter if data will flow from non-observable property to property of another class
        if (!targetPropertyClass.equals( sourcePropertyInfo.clazz )) {
            boolean needConverter = false;
            if (realMode == BindingMode.OneTime || realMode == BindingMode.OneWay || realMode == BindingMode.TwoWay)
                needConverter |= !sourceIsObservable;
            if (realMode == BindingMode.OneWayToSource || realMode == BindingMode.TwoWay)
                needConverter |= !targetIsObservable;
            //
            if (needConverter) {
                converter = settings.getConverterFor( targetPropertyClass, sourcePropertyInfo.clazz );
                if (converter == null )
                    throw new RuntimeException( String.format("Converter for %s -> %s classes not found.",
                            targetPropertyClass.getName(), sourcePropertyInfo.clazz.getName()) );
            }
        }

        // verify properties getters and setters for specified binding mode
        if (realMode == BindingMode.OneTime || realMode == BindingMode.OneWay || realMode == BindingMode.TwoWay) {
            if (sourcePropertyInfo.getter == null) throw new RuntimeException( "Source property getter not found" );
            if (sourceIsObservable) {
                if (null == adapter && targetPropertyInfo.getter == null) throw new RuntimeException( "Target property getter not found" );
                if (!List.class.isAssignableFrom( targetPropertyClass ))
                    throw new RuntimeException( "Target property class have to implement List" );
            } else {
                if (null == adapter && targetPropertyInfo.setter == null) throw new RuntimeException( "Target property setter not found" );
            }
        }
        if (realMode == BindingMode.OneWayToSource || realMode == BindingMode.TwoWay) {
            if ( null == adapter && targetPropertyInfo.getter == null) throw new RuntimeException( "Target property getter not found" );
            if ( targetIsObservable) {
                if (sourcePropertyInfo.getter == null) throw new RuntimeException( "Source property getter not found" );
                if (!List.class.isAssignableFrom( sourcePropertyInfo.clazz ))
                    throw new RuntimeException( "Source property class have to implement List" );
            } else {
                if (sourcePropertyInfo.setter == null ) throw new RuntimeException( "Source property setter not found" );
            }
        }

        // subscribe to listeners
        connectSourceAndTarget();

        // initial flush values
        if ( realMode == BindingMode.OneTime || realMode == BindingMode.OneWay || realMode == BindingMode.TwoWay)
            updateTarget();
        if (realMode == BindingMode.OneWayToSource || realMode == BindingMode.TwoWay)
            updateSource();

        this.bound = true;
    }

    protected void connectSourceAndTarget() {
        switch ( realMode ) {
            case OneTime:
                break;
            case OneWay:
                sourceListener = new SourceChangeListener();
                source.addPropertyChangedListener( sourceListener );
                break;
            case OneWayToSource:
                if (null == adapter) {
                    targetListener = new TargetChangeListener();
                    ((INotifyPropertyChanged) target).addPropertyChangedListener( targetListener );
                } else {
                    targetListenerWrapper = adapter.addPropertyChangedListener( target, new TargetChangeListener() );
                }
                break;
            case TwoWay:
                sourceListener = new SourceChangeListener();
                source.addPropertyChangedListener( sourceListener );
                //
                if (null == adapter) {
                    targetListener = new TargetChangeListener();
                    ((INotifyPropertyChanged) target).addPropertyChangedListener( targetListener );
                } else {
                    targetListenerWrapper = adapter.addPropertyChangedListener( target, new TargetChangeListener() );
                }
                break;
        }
    }

    /**
     * Disconnects Source and Target objects.
     */
    public void unbind() {
        if (!this.bound) return;

        disconnectSourceAndTarget();

        this.sourcePropertyInfo = null;
        this.targetPropertyInfo = null;

        this.adapter = null;
        this.converter = null;

        this.bound = false;
    }

    protected void disconnectSourceAndTarget() {
        if (realMode == BindingMode.OneWay || realMode == BindingMode.TwoWay) {
            // remove source listener
            source.removePropertyChangedListener( sourceListener );
            this.sourceListener = null;
        }
        if (realMode == BindingMode.OneWayToSource || realMode == BindingMode.TwoWay) {
            // remove target listener
            if (adapter == null) {
                ((INotifyPropertyChanged) target ).removePropertyChangedListener( targetListener );
                targetListener = null;
            } else {
                adapter.removePropertyChangedListener( target, targetListenerWrapper );
                targetListenerWrapper = null;
            }
        }

        if (sourceList != null) {
            sourceList.removeObservableListListener(sourceListListener);
            sourceList = null;
        }
        if (targetList != null) {
            targetList.removeObservableListListener(targetListListener);
            targetList = null;
        }
    }

    /**
     * Changes the binding Source object. If current binding state is bound,
     * the {@link #unbind()} and {@link #bind()} methods will be called automatically.
     * @param source New Source object
     */
    public void setSource(INotifyPropertyChanged source) {
        if (null == source) throw new IllegalArgumentException( "source is null" );
        if (bound) {
            unbind();
            this.source = source;
            bind();
        } else {
            this.source = source;
        }
    }

    /**
     * Changes the binding Target object. If current binding state is bound,
     * the {@link #unbind()} and {@link #bind()} methods will be called automatically.
     * @param target New Target object
     */
    public void setTarget(Object target) {
        if (null == target) throw new IllegalArgumentException( "target is null" );
        if (bound) {
            unbind();
            this.target = target;
            bind();
        } else {
            this.target = target;
        }
    }
}
