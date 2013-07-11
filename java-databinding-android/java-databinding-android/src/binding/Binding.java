package binding;

import android.view.View;
import binding.BindingBase;
import binding.BindingMode;
import binding.BindingSettingsBase;
import binding.INotifyPropertyChanged;
import binding.IUiBindingAdapter;

/**
 * @author igor.kostromin
 *         10.07.13 16:20
 */
public class Binding extends BindingBase {
    private UpdateSourceTrigger updateSourceTrigger;

    // used instead targetListener if UpdateSourceTrigger set to LostFocus
    private TargetFocusListener targetFocusListener;

    public Binding( View target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode,
                        UpdateSourceTrigger updateSourceTrigger, BindingSettingsBase settings ) {
        super( target, targetProperty, source, sourceProperty, mode, settings );
        //
        this.updateSourceTrigger = updateSourceTrigger;
        this.targetIsUi = true;
    }

    public Binding( View target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode,
                        UpdateSourceTrigger updateSourceTrigger ) {
        this(target, targetProperty, source, sourceProperty, mode, updateSourceTrigger, BindingSettings.ANDROID_DEFAULT_SETTINGS );
    }

    public Binding( View target, String targetProperty, INotifyPropertyChanged source, String sourceProperty, BindingMode mode ) {
        this(target, targetProperty, source, sourceProperty, mode, UpdateSourceTrigger.Default );
    }

    public Binding( View target, String targetProperty, INotifyPropertyChanged source, String sourceProperty ) {
        this(target, targetProperty, source, sourceProperty, BindingMode.Default, UpdateSourceTrigger.Default );
    }

    private class TargetFocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange( View view, boolean b ) {
            if (!b && !ignoreTargetListener)
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
                        ((View) target).setOnFocusChangeListener( targetFocusListener );
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
                        ((View) target).setOnFocusChangeListener( targetFocusListener );
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
                    ((View) target).setOnFocusChangeListener( null );
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
