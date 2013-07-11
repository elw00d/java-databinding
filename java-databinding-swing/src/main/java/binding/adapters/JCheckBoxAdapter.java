/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package binding.adapters;

import binding.BindingMode;
import binding.IPropertyChangedListener;
import binding.UpdateSourceTrigger;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Provides next virtual properties:
 * Boolean "checked" (can be used in any binding mode)
 *
 * @author elwood
 */
public class JCheckBoxAdapter implements IUiBindingAdapter<JCheckBox> {

    @Override
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.PropertyChanged;
    }

    @Override
    public Class<JCheckBox> getTargetClazz() {
        return JCheckBox.class;
    }

    @Override
    public Class<?> getTargetPropertyClazz(String targetProperty) {
        return Boolean.class;
    }

    @Override
    public <TValue> void setValue(JCheckBox target, String targetProperty, TValue value) {
        if (!targetProperty.equals("checked")) throw new UnsupportedOperationException();
        target.setSelected(value != null ? (Boolean) value : false);
    }

    @Override
    public <TValue> TValue getValue(JCheckBox target, String targetProperty) {
        if (!"checked".equals(targetProperty)) throw new UnsupportedOperationException();
        return (TValue) (Boolean) target.isSelected();
    }

    @Override
    public Object addPropertyChangedListener(JCheckBox target, final IPropertyChangedListener listener) {
        ChangeListener _listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                listener.propertyChanged("checked");
            }
        };
        target.addChangeListener(_listener);
        return _listener;
    }

    @Override
    public void removePropertyChangedListener(JCheckBox target, Object listenerWrapper) {
        target.removeChangeListener((ChangeListener) listenerWrapper);
    }

    @Override
    public BindingMode getDefaultMode() {
        return BindingMode.TwoWay;
    }
    
}
