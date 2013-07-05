import binding.*;
import binding.validators.RequiredValidator;
import junit.framework.Assert;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author igor.kostromin
 *         27.06.13 13:24
 */
public class BindingTest {
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

    public static class Target implements INotifyPropertyChanged {
        private String text;

        public String getText() {
            System.out.println("getText()");
            return text;
        }

        public void setText( String text ) {
            System.out.println("setText()");
            this.text = text;
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

    @Test
    public void testSimpleBinding() {
        Source source = new Source();
        Target target = new Target();
        source.setI( 4 );
        Binding binding= new Binding( target, "text", source, "i", BindingMode.Default );
        binding.bind();
        source.setI( 5 );
        Assert.assertTrue(target.getText().equals( "5" ));
        target.setText( "1" );
        Assert.assertTrue( source.getI() == 1);
        target.setText( null );
        Assert.assertTrue( source.getI() == null);
        binding.unbind();
        target.setText( "5" );
        Assert.assertTrue( source.getI() == null );
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

    @Test
    public void testOneTimeBinding() {
        Source source = new Source();
        Target target = new Target();
        Binding binding = new Binding( target, "text", source, "i", BindingMode.OneTime );
        source.setI( 5 );
        binding.bind();
        Assert.assertTrue( target.getText().equals( "5" ) );
        source.setI( 6 );
        Assert.assertTrue( target.getText().equals( "5" ) );
        target.setText( "10" );
        Assert.assertTrue( source.getI() == 6 );
    }

    private static class Wrapper<T> {
        T object;
    }

    @Test
    public void testValidationAndOneWayToSourceBinding() {
        Source source = new Source();
        Target target = new Target();
        Binding binding = new Binding( target, "text", source, "i", BindingMode.OneWayToSource );
        binding.setValidator( new RequiredValidator() );
        final Wrapper<BindingResult> lastResult = new Wrapper<BindingResult>(  );
        binding.setResultListener( new IBindingResultListener() {
            public void onBinding( BindingResult result ) {
                lastResult.object = result;
            }
        } );
        binding.bind();
        Assert.assertTrue( lastResult.object.hasValidationError );
        target.setText( "incorrectnum" );
        Assert.assertTrue( lastResult.object.hasConversionError );
        target.setText( "0" );
        Assert.assertTrue( !lastResult.object.hasError );
        source.setI( 5 );
        Assert.assertTrue( target.getText().equals( "0" ) );
    }

    @Test
    public void testOneWayBinding() {
        Source source = new Source();
        Target target = new Target();
        target.setText( "" );
        Binding binding = new Binding( target, "text", source, "i", BindingMode.OneWay );
        binding.bind();
        Assert.assertTrue( target.getText() == null);
        source.setI( 1 );
        Assert.assertTrue( target.getText().equals( "1" ) );
        target.setText( "10" );
        Assert.assertTrue( source.getI() == 1 );
    }
}
