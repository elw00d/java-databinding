/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package binding.adapters;

import binding.BindingMode;
import binding.IPropertyChangedListener;
import binding.UpdateSourceTrigger;

import javax.swing.*;

/**
 * Provides next virtual properties:
 * String "text" (only BindingMode.OneWay is supported)
 * Icon "icon" (only BindingMode.OneWay is supported)
 * String "tooltip" (only BindingMode.OneWay is supported)
 *
 * @author elwood
 */
public class JLabelAdapter implements IUiBindingAdapter<JLabel> {

    @Override
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.Explicit;
    }

    @Override
    public Class<JLabel> getTargetClazz() {
        return JLabel.class;
    }

    @Override
    public Class<?> getTargetPropertyClazz(String targetProperty) {
        if ("text".equals(targetProperty)) return String.class;
        if ("icon".equals(targetProperty)) return Icon.class;
        if ("tooltip".equals( targetProperty )) return String.class;
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> void setValue(JLabel target, String targetProperty, TValue value) {
        if ("text".equals(targetProperty)) {
            target.setText((String) value);
            return;
        }
        if ("icon".equals( targetProperty )) {
            target.setIcon( ( Icon ) value );
            return;
        }
        if ("tooltip".equals( targetProperty ) ) {
            target.setToolTipText( ( String ) value );
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> TValue getValue(JLabel target, String targetProperty) {
        throw new UnsupportedOperationException("Binding to JLabel as datasource is not supported. Use OneWay or OneTime mode.");
    }

    @Override
    public Object addPropertyChangedListener(JLabel target, IPropertyChangedListener listener) {
        throw new UnsupportedOperationException("Binding to JLabel as datasource is not supported. Use OneWay or OneTime mode.");
    }

    @Override
    public void removePropertyChangedListener(JLabel target, Object listenerWrapper) {
        throw new UnsupportedOperationException("Binding to JLabel as datasource is not supported. Use OneWay or OneTime mode.");
    }

    @Override
    public BindingMode getDefaultMode() {
        return BindingMode.OneWay;
    }
    
}
