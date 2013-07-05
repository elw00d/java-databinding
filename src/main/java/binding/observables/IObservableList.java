package binding.observables;

import java.util.List;

/**
 * A {@link List} that notifies listeners of changes.
 *
 * @author igor.kostromin
 *         28.06.13 17:09
 */
public interface IObservableList<T> extends List<T> {
    /**
     * Adds a listener that is notified when the list changes.
     *
     * @param listener the listener to add
     */
    public void addObservableListListener(IObservableListListener listener);

    /**
     * Removes a listener.
     *
     * @param listener the listener to remove
     */
    public void removeObservableListListener(IObservableListListener listener);
}
