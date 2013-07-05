package binding.adapters;

import binding.BindingMode;
import binding.IPropertyChangedListener;
import binding.UpdateSourceTrigger;
import binding.observables.IObservableList;
import binding.observables.IObservableListListener;
import binding.observables.ObservableList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides next virtual properties:
 * ObservableList "items" (typically used in BindingMode.OneWay scenario)
 * ObservableList "selectedItems" (typically used in BindingMode.OneWayToSource scenario)
 *
 * User: igor.kostromin
 * Date: 01.07.13
 * Time: 1:31
 */
public class JListAdapter implements IUiBindingAdapter<JList> {
    @Override
    public UpdateSourceTrigger getDefaultUpdateSourceTrigger() {
        return UpdateSourceTrigger.PropertyChanged;
    }

    @Override
    public Class<JList> getTargetClazz() {
        return JList.class;
    }

    @Override
    public Class<?> getTargetPropertyClazz(String targetProperty) {
        if ("items".equals( targetProperty ))
            return List.class;
        if ("selectedItems".equals( targetProperty ))
            return IObservableList.class;
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> void setValue(JList jList, String targetProperty, TValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <TValue> TValue getValue(final JList jList, String targetProperty) {
        if (targetProperty.equals("items")) {
            Object binding_model = jList.getClientProperty("BINDING_MODEL_ITEMS");
            if (null != binding_model) return (TValue) binding_model;
            // observable list is used only for automatically call jList.updateUI();
            final ObservableList list = new ObservableList(new ArrayList());
            jList.putClientProperty("BINDING_MODEL_ITEMS", list);
            jList.setModel(new AbstractListModel() {
                @Override
                public int getSize() {
                    return list.size();
                }

                @Override
                public Object getElementAt(int index) {
                    return list.get(index);
                }
            });
            list.addObservableListListener(new IObservableListListener() {
                @Override
                public void listElementsAdded(IObservableList list, int index, int length) {
                    jList.updateUI();
                }

                @Override
                public void listElementsRemoved(IObservableList list, int index, List oldElements) {
                    jList.updateUI();
                }

                @Override
                public void listElementReplaced(IObservableList list, int index, Object oldElement) {
                    jList.updateUI();
                }
            });
            return (TValue) list;
        }
        if ("selectedItems".equals(targetProperty)) {
            Object bindingModel = jList.getClientProperty("BINDING_MODEL_SELECTEDITEMS");
            if (null != bindingModel ) return (TValue) bindingModel;
            final ObservableList list = new ObservableList(new ArrayList());
            // initialize selected items using jList current selection state
            for ( Object selected : jList.getSelectedValues() ) {
                list.add( selected );
            }
            //list.addAll(jList.getSelectedValuesList());
            jList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    //List current = jList.getSelectedValuesList();
                    List current = new ArrayList(  );
                    Collections.addAll( current, jList.getSelectedValues() );
                    List added = new ArrayList();
                    List removed = new ArrayList();
                    for (Object o : list) if (!current.contains(o)) removed.add(o);
                    for (Object o : current) if (!list.contains(o)) added.add(o);
                    for (Object o : removed) list.remove(o);
                    for (Object o : added) list.add(o);
                }
            });
            jList.putClientProperty("BINDING_MODEL_SELECTEDITEMS", list);
            return (TValue) list;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addPropertyChangedListener(JList jList, IPropertyChangedListener listener) {
        return null;
    }

    @Override
    public void removePropertyChangedListener(JList jList, Object listenerWrapper) {
    }

    @Override
    public BindingMode getDefaultMode() {
        return BindingMode.OneWay;
    }
}
