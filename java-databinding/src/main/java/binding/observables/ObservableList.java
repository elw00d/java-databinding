package binding.observables;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link IObservableList} implementation.
 *
 * @author igor.kostromin
 *         28.06.13 17:11
 */
public class ObservableList<E> extends AbstractList<E>
        implements IObservableList<E> {
    private List<E> list;
    private List<IObservableListListener> listeners;

    public ObservableList( List<E> list ) {
        this.list = list;
        listeners = new CopyOnWriteArrayList<IObservableListListener>();
    }

    public E get( int index ) {
        return list.get( index );
    }

    public int size() {
        return list.size();
    }

    public E set( int index, E element ) {
        E oldValue = list.set( index, element );

        for ( IObservableListListener listener : listeners ) {
            listener.listElementReplaced( this, index, oldValue );
        }

        return oldValue;
    }

    public void add( int index, E element ) {
        list.add( index, element );
        modCount++;

        for ( IObservableListListener listener : listeners ) {
            listener.listElementsAdded( this, index, 1 );
        }
    }

    public E remove( int index ) {
        E oldValue = list.remove( index );
        modCount++;

        for ( IObservableListListener listener : listeners ) {
            listener.listElementsRemoved( this, index,
                    java.util.Collections.singletonList( oldValue ) );
        }

        return oldValue;
    }

    public boolean addAll( Collection<? extends E> c ) {
        return addAll( size(), c );
    }

    public boolean addAll( int index, Collection<? extends E> c ) {
        if ( list.addAll( index, c ) ) {
            modCount++;

            for ( IObservableListListener listener : listeners ) {
                listener.listElementsAdded( this, index, c.size() );
            }
        }

        return false;
    }

    public void clear() {
        List<E> dup = new ArrayList<E>( list );
        list.clear();
        modCount++;

        if ( dup.size() != 0 ) {
            for ( IObservableListListener listener : listeners ) {
                listener.listElementsRemoved( this, 0, dup );
            }
        }
    }

    public boolean containsAll( Collection<?> c ) {
        return list.containsAll( c );
    }

    public <T> T[] toArray( T[] a ) {
        return list.toArray( a );
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public void addObservableListListener( IObservableListListener listener ) {
        listeners.add( listener );
    }

    public void removeObservableListListener(
            IObservableListListener listener ) {
        listeners.remove( listener );
    }
}
