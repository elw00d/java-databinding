import binding.BindingBase;
import binding.BindingMode;
import binding.INotifyPropertyChanged;
import binding.IPropertyChangedListener;
import binding.observables.IObservableList;
import binding.observables.IObservableListListener;
import binding.observables.ObservableList;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author igor.kostromin
 *         27.06.13 18:33
 */
public class ObservablesTest {
    public static class Source implements INotifyPropertyChanged {
        private IObservableList<String > list = new ObservableList<String>( new ArrayList<String>(  ));

        public IObservableList<String> getList() {
            return list;
        }

        public Source() {
            ((IObservableList<String>) list).addObservableListListener( new IObservableListListener() {
                public void listElementsAdded( IObservableList list, int index, int length ) {
                    //raisePropertyChange( "list" );
                    System.out.println("added");
                }
                public void listElementsRemoved( IObservableList list, int index, List oldElements ) {
                    //raisePropertyChange( "list" );
                    System.out.println("removed");
                }
                public void listElementReplaced( IObservableList list, int index, Object oldElement ) {
                    //raisePropertyChange( "list" );
                    System.out.println("replaced");
                }
            } );
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
    public void testOneWayBinding() {
        Source source = new Source();
        Source target = new Source();
        BindingBase binding = new BindingBase( target, "list", source, "list", BindingMode.OneWay );
        binding.bind();
        Assert.assertTrue(target.getList().isEmpty());
        source.getList().add( "1" );
        Assert.assertTrue(target.getList().get( 0 ).equals( "1" ));
    }
}
