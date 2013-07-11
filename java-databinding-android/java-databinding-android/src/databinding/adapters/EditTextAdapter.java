package databinding.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import databinding.UpdateSourceTrigger;
import android.widget.EditText;
import binding.BindingMode;
import binding.IPropertyChangedListener;

/**
 * @author igor.kostromin
 *         10.07.13 17:23
 */
public class EditTextAdapter implements IUiBindingAdapter<EditText> {
    @Override
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.LostFocus;
    }

    @Override
    public Class<EditText> getTargetClazz() {
        return EditText.class;
    }

    @Override
    public Class<?> getTargetPropertyClazz( String targetProperty ) {
        if ("text".equals( targetProperty ))
            return String.class;
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> void setValue( EditText editText, String targetProperty, TValue value ) {
        if ("text".equals( targetProperty )) {
            editText.setText( ( CharSequence ) value );
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> TValue getValue( EditText editText, String targetProperty ) {
        if ("text".equals( targetProperty )) {
            return ( TValue ) editText.getText().toString();
        }
        throw new UnsupportedOperationException();
    }

    private class TextChangedListener implements TextWatcher {
        IPropertyChangedListener listener;
        private TextChangedListener(IPropertyChangedListener listener) {
            this.listener = listener;
        }

        @Override
        public void beforeTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {
        }

        @Override
        public void onTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {
        }

        @Override
        public void afterTextChanged( Editable editable ) {
            listener.propertyChanged( "text" );
        }
    }

    @Override
    public Object addPropertyChangedListener( EditText editText, IPropertyChangedListener listener ) {
        TextChangedListener watcher = new TextChangedListener( listener );
        editText.addTextChangedListener( watcher );
        return watcher;
    }

    @Override
    public void removePropertyChangedListener( EditText editText, Object listenerWrapper ) {
        editText.removeTextChangedListener( ( TextWatcher ) listenerWrapper );
    }

    @Override
    public BindingMode getDefaultMode() {
        return BindingMode.TwoWay;
    }
}
