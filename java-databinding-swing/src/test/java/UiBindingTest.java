import binding.*;
import junit.framework.Assert;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author igor.kostromin
 *         27.06.13 18:32
 */
public class UiBindingTest {

    public static class Source implements INotifyPropertyChanged {

        private Integer i;

        public Integer getI() {
            System.out.println("getI()");
            return i;
        }

        public void setI( Integer i ) {
            System.out.println("setI()");
            this.i = i;
            raisePropertyChange( "i" );
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

    @Test
    public void testJTextField() {
        Source source = new Source();
        JTextField textField = new JTextField(  );
        source.setI( 1 );
        Binding binding = new Binding( textField, "text", source, "i", BindingMode.Default, UpdateSourceTrigger.PropertyChanged );
        binding.bind();
        Assert.assertTrue( textField.getText().equals( "1" ) );
        textField.setText( "2" );
        Assert.assertTrue( source.getI() == 2 );
    }
}
