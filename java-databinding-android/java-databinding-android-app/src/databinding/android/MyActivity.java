package databinding.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import binding.INotifyPropertyChanged;
import binding.IPropertyChangedListener;
import databinding.Binding;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private static class MyFactory implements LayoutInflater.Factory {

        private final LayoutInflater inflater;

        private MyFactory(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public View onCreateView( String s, Context context, AttributeSet attributeSet ) {
            View view = null;
            try {
                if (s.equals( "EditText" )) {
                view = inflater.createView("android.widget." + s, null , attributeSet );
                if (view.getId() == R.id.editText) {
                    String binding = attributeSet.getAttributeValue( "http://schemas.android.com/apk/res/databinding.android", "binding" );
                }
                return view;

                } else return null;
            } catch ( ClassNotFoundException e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getLayoutInflater().setFactory(new MyFactory(getLayoutInflater() ));
        setContentView( R.layout.main );

        TextView textTitle = (TextView) findViewById( R.id.textTitle );
        Model model = new Model();
        Binding binding = new Binding( textTitle, "text", model, "title" );
        binding.bind();
        Binding binding2 = new Binding( findViewById( R.id.editText ), "text", model, "text" );
        binding2.bind();

        model.setTitle( "Hello !" );

//        EditText editText = ( EditText ) findViewById( R.id.editText );
//        editText.
//        TypedArray typedArray = this.obtainStyledAttributes( R.id.editText, new int[] {R.styleable.EditText_binding} );
//        String string = typedArray.getString( R.styleable.EditText_binding );
    }

    public static class Model implements INotifyPropertyChanged {
        private String text;
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle( String title ) {
            this.title = title;
            raisePropertyChange( "title" );
        }

        public String getText() {
            return text;
        }

        public void setText( String text ) {
            this.text = text;
            setTitle( "You entered: "  + text );
            raisePropertyChange( "text" );
        }

        private List<IPropertyChangedListener> getListeners() {
            return listeners;
        }

        private void setListeners( List<IPropertyChangedListener> listeners ) {
            this.listeners = listeners;
        }

        private void raisePropertyChange( String propName) {
            for ( IPropertyChangedListener listener : listeners ) {
                listener.propertyChanged( propName );
            }
        }

        private List<IPropertyChangedListener> listeners = new ArrayList<IPropertyChangedListener>(  );

        public void addPropertyChangedListener( IPropertyChangedListener listener ) {
            listeners.add( listener );
        }

        public void removePropertyChangedListener( IPropertyChangedListener listener ) {
            listeners.remove( listener );
        }
    }
}
