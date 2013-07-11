package binding.adapters;

import binding.BindingMode;
import binding.IPropertyChangedListener;
import binding.UpdateSourceTrigger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Provides next virtual properties:
 * String "text" (can be used in any binding mode)
 *
 * @author igor.kostromin
 *         28.06.13 12:04
 */
public class JPasswordFieldAdapter implements IUiBindingAdapter<JPasswordField> {
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.LostFocus;
    }

    public Class<JPasswordField> getTargetClazz() {
        return JPasswordField.class;
    }

    public Class<?> getTargetPropertyClazz( String targetProperty ) {
        return String.class;
    }

    public <TValue> void setValue( JPasswordField jPasswordField, String targetProperty, TValue value ) {
        if (!targetProperty.equals( "text" )) throw new UnsupportedOperationException(  );
        jPasswordField.setText( ( String ) value );
    }

    public <TValue> TValue getValue( JPasswordField jPasswordField, String targetProperty ) {
        if (!targetProperty.equals( "text" )) throw new UnsupportedOperationException(  );
        return ( TValue ) new String(jPasswordField.getPassword());
    }

    private static class MyDocumentListener implements DocumentListener {
        IPropertyChangedListener listener;

        private MyDocumentListener( IPropertyChangedListener listener ) {
            this.listener = listener;
        }

        public void insertUpdate( DocumentEvent e ) {
            listener.propertyChanged( "text" );
        }

        public void removeUpdate( DocumentEvent e ) {
            listener.propertyChanged( "text" );
        }

        public void changedUpdate( DocumentEvent e ) {
            listener.propertyChanged( "text" );
        }
    }

    public Object addPropertyChangedListener( JPasswordField target, IPropertyChangedListener listener ) {
        MyDocumentListener documentListener = new MyDocumentListener( listener );
        target.getDocument().addDocumentListener( documentListener );
        return documentListener;
    }

    public void removePropertyChangedListener( JPasswordField target, Object listenerWrapper ) {
        target.getDocument().removeDocumentListener( ( DocumentListener ) listenerWrapper );
    }

    public BindingMode getDefaultMode() {
        return BindingMode.TwoWay;
    }
}
