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
 *         26.06.13 19:06
 */
public class JTextFieldAdapter implements IUiBindingAdapter<JTextField> {
    public Class<JTextField> getTargetClazz() {
        return JTextField.class;
    }

    public Class<?> getTargetPropertyClazz( String targetProperty ) {
        if ("text".equals( targetProperty ))
            return String.class;
        return null;
    }

    public <TValue> void setValue( JTextField target, String targetProperty, TValue value ) {
        if (!"text".equals( targetProperty )) throw new UnsupportedOperationException( );
        target.setText( ( String ) value );
    }

    public <TValue> TValue getValue( JTextField target, String targetProperty ) {
        if (!"text".equals( targetProperty )) throw new UnsupportedOperationException( );
        return ( TValue ) target.getText();
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

    public Object addPropertyChangedListener( JTextField target, IPropertyChangedListener listener ) {
        MyDocumentListener documentListener = new MyDocumentListener( listener );
        target.getDocument().addDocumentListener( documentListener );
        return documentListener;
    }

    public void removePropertyChangedListener( JTextField target, Object listenerWrapper ) {
        target.getDocument().removeDocumentListener( ( DocumentListener ) listenerWrapper );
    }

    public BindingMode getDefaultMode() {
        return BindingMode.TwoWay;
    }

    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.LostFocus;
    }
}
