package binding;

import binding.adapters.IUiBindingAdapter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Provides data sync connection between two objects - source and target. Both source and target can be just objects,
 * but if you want to bind to object that does not implement {@link INotifyPropertyChanged} (for example, some UI control),
 * you should use it as target and use appropriate adapter ({@link IUiBindingAdapter} implementation). One Binding instance connects
 * one source property and one target property. Several Swing UI controls are supported by default.
 *
 * Typical usage scenario:
 * <p><blockquote><pre>
 * // Model object is binding Source, JTextField object is binding Target
 * public class Model implements INotifyPropertyChanged {
 *   private String name;
 *
 *   public String getName() { return name; }
 *
 *   public void setName(String name) {
 *       this.name = name;
 *       // raise property change event to let Binding know when to
 *       // update property from Source to Target
 *       raisePropertyChange("name");
 *   }
 *
 *   // INotifyPropertyChange implementation - you can extract it easily into superclass
 *   private void raisePropertyChange( String propName) {
 *       for ( IPropertyChangedListener listener : listeners ) {
 *           listener.propertyChanged( propName );
 *       }
 *   }
 *
 *   private List&lt;IPropertyChangedListener&gt; listeners = new ArrayList&lt;&gt;(  );
 *
 *   public void addPropertyChangedListener( IPropertyChangedListener listener ) {
 *      listeners.add( listener );
 *   }
 *
 *   public void removePropertyChangedListener( IPropertyChangedListener listener ) {
 *      listeners.remove( listener );
 *   }
 * }
 *
 * JTextField textField = ...
 * Model model = new Model();
 * model.setName("Igor");
 * Binding binding = new Binding(textField, "text", model, "name", BindingMode.TwoWay, UpdateSourceTrigger.PropertyChanged);
 * binding.bind();
 * </pre></blockquote></p>
 *
 * @author igor.kostromin
 *         10.07.13 15:29
 */
public class Binding extends BindingBase {
    private UpdateSourceTrigger updateSourceTrigger;

    // used instead targetListener if UpdateSourceTrigger set to LostFocus
    private FocusListener targetFocusListener;

    public Binding( JComponent target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode,
                        UpdateSourceTrigger updateSourceTrigger, BindingSettingsBase settings ) {
        super( target, targetProperty, source, sourceProperty, mode, settings );
        //
        this.updateSourceTrigger = updateSourceTrigger;
        this.targetIsUi = true;
    }

    public Binding( JComponent target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode,
                        UpdateSourceTrigger updateSourceTrigger ) {
        this(target, targetProperty, source, sourceProperty, mode, updateSourceTrigger, BindingSettings.SWING_DEFAULT_SETTINGS );
    }

    public Binding( JComponent target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode ) {
        this(target, targetProperty, source, sourceProperty, mode, UpdateSourceTrigger.Default );
    }

    public Binding( JComponent target, String targetProperty, INotifyPropertyChanged source, String sourceProperty ) {
        this(target, targetProperty, source, sourceProperty, BindingMode.Default, UpdateSourceTrigger.Default );
    }

    private class TargetFocusListener implements FocusListener {
        public void focusGained( FocusEvent e ) {
        }

        public void focusLost( FocusEvent e ) {
            if (!ignoreTargetListener)
                updateSource();
        }
    }

    private UpdateSourceTrigger getRealUpdateSourceTrigger() {
        assert targetIsUi;
        if (updateSourceTrigger != UpdateSourceTrigger.Default)
            return updateSourceTrigger;
        else {
            UpdateSourceTrigger real = ((IUiBindingAdapter ) adapter).getDefaultUpdateSourceTrigger();
            if (real == UpdateSourceTrigger.Default) throw new AssertionError("Adapter cannot return UpdateSourceTrigger.Default");
            return real;
        }
    }

    @Override
    protected void connectSourceAndTarget() {
        switch ( realMode ) {
            case OneTime:
                break;
            case OneWay:
                sourceListener = new SourceChangeListener();
                source.addPropertyChangedListener( sourceListener );
                break;
            case OneWayToSource:
                if (!targetIsUi || getRealUpdateSourceTrigger() == UpdateSourceTrigger.PropertyChanged) {
                    if (null == adapter) {
                        targetListener = new TargetChangeListener();
                        ((INotifyPropertyChanged) target).addPropertyChangedListener( targetListener );
                    } else {
                        targetListenerWrapper = adapter.addPropertyChangedListener( target, new TargetChangeListener() );
                    }
                } else {
                    if (getRealUpdateSourceTrigger() == UpdateSourceTrigger.LostFocus) {
                        targetFocusListener = new TargetFocusListener();
                        ((JComponent) target).addFocusListener( targetFocusListener );
                    }
                }
                break;
            case TwoWay:
                sourceListener = new SourceChangeListener();
                source.addPropertyChangedListener( sourceListener );
                //
                if (!targetIsUi || getRealUpdateSourceTrigger() == UpdateSourceTrigger.PropertyChanged) {
                    if (null == adapter) {
                        targetListener = new TargetChangeListener();
                        ((INotifyPropertyChanged) target).addPropertyChangedListener( targetListener );
                    } else {
                        targetListenerWrapper = adapter.addPropertyChangedListener( target, new TargetChangeListener() );
                    }
                } else {
                    if (getRealUpdateSourceTrigger() == UpdateSourceTrigger.LostFocus) {
                        targetFocusListener = new TargetFocusListener();
                        ((JComponent) target).addFocusListener( targetFocusListener );
                    }
                }
                break;
        }
    }

    @Override
    protected void disconnectSourceAndTarget() {
        if (realMode == BindingMode.OneWay || realMode == BindingMode.TwoWay) {
            // remove source listener
            source.removePropertyChangedListener( sourceListener );
            this.sourceListener = null;
        }
        if (realMode == BindingMode.OneWayToSource || realMode == BindingMode.TwoWay) {
            // remove target listener
            if (!targetIsUi || getRealUpdateSourceTrigger() == UpdateSourceTrigger.PropertyChanged) {
                if (adapter == null) {
                    ((INotifyPropertyChanged) target ).removePropertyChangedListener( targetListener );
                    targetListener = null;
                } else {
                    adapter.removePropertyChangedListener( target, targetListenerWrapper );
                    targetListenerWrapper = null;
                }
            } else {
                if (getRealUpdateSourceTrigger() == UpdateSourceTrigger.LostFocus) {
                    ((JComponent) target).removeFocusListener( targetFocusListener );
                }
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
}
