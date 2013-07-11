package binding;

import android.widget.TextView;
import binding.BindingMode;
import binding.IPropertyChangedListener;
import binding.UpdateSourceTrigger;

/**
 * @author igor.kostromin
 *         10.07.13 16:33
 */
public class TextViewAdapter implements IUiBindingAdapter<TextView> {
    @Override
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.Explicit;
    }

    @Override
    public Class<TextView> getTargetClazz() {
        return TextView.class;
    }

    @Override
    public Class<?> getTargetPropertyClazz( String targetProperty ) {
        if ("text".equals( targetProperty ))
            return String.class;
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> void setValue( TextView textView, String targetProperty, TValue value ) {
        if ("text".equals( targetProperty )) {
            textView.setText( ( CharSequence ) value );
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> TValue getValue( TextView textView, String targetProperty ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addPropertyChangedListener( TextView textView, IPropertyChangedListener listener ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePropertyChangedListener( TextView textView, Object listenerWrapper ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BindingMode getDefaultMode() {
        return BindingMode.OneWay;
    }
}
