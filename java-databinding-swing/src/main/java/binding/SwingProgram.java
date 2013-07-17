package binding;

import binding.observables.IObservableList;
import binding.observables.IObservableListListener;
import binding.observables.ObservableList;
import binding.validators.RequiredValidator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author igor.kostromin
 *         27.06.13 19:56
 */
public class SwingProgram {
    
    public static class ListItem {

        @Override
        public String toString() {
            return "Item!";
        }
        
    }
    
    public static class Model implements INotifyPropertyChanged {

        private Integer integerValue;
        private String email;
        private String password;
        private ObservableList<ListItem> items = new ObservableList<ListItem>(new ArrayList<ListItem>());
        private ObservableList<ListItem> selectedItems = new ObservableList<ListItem>(new ArrayList<ListItem>());
        private Boolean remember;
        private Boolean variant1;
        private Boolean variant2;
        private List<String> availableItems;
        private String selectedItem;
        
        public Model() {
            selectedItems.addObservableListListener(new IObservableListListener() {
                public void listElementsAdded(IObservableList list, int index, int length) {
                    raisePropertyChange("selectedItems");
                }
                public void listElementsRemoved(IObservableList list, int index, List oldElements) {
                    raisePropertyChange("selectedItems");
                }
                public void listElementReplaced(IObservableList list, int index, Object oldElement) {
                    raisePropertyChange("selectedItems");
                }
            });
        }

        public Integer getIntegerValue() {
            return integerValue;
        }

        public void setIntegerValue(Integer integerValue) {
            if (this.integerValue != integerValue) {
                this.integerValue = integerValue;
                raisePropertyChange("integerValue");
            }
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
            raisePropertyChange("email");
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
            raisePropertyChange("password");
        }

        public ObservableList<ListItem> getItems() {
            return items;
        }

        public List<ListItem> getSelectedItems() {
            return selectedItems;
        }

        public Boolean getRemember() {
            return remember;
        }

        public void setRemember(Boolean remember) {
            if (this.remember != remember) {
                this.remember = remember;
                raisePropertyChange("remember");
            }
        }

        public Boolean getVariant1() {
            return variant1;
        }

        public void setVariant1(Boolean variant1) {
            this.variant1 = variant1;
        }

        public Boolean getVariant2() {
            return variant2;
        }

        public void setVariant2(Boolean variant2) {
            this.variant2 = variant2;
        }

        public List<String> getAvailableItems() {
            return availableItems;
        }

        public void setAvailableItems(List<String> availableItems) {
            this.availableItems = availableItems;
        }

        public String getSelectedItem() {
            return selectedItem;
        }

        public void setSelectedItem(String selectedItem) {
            this.selectedItem = selectedItem;
        }

        public List<IPropertyChangedListener> getListeners() {
            return listeners;
        }

        public void setListeners(List<IPropertyChangedListener> listeners) {
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
        
        public String dump() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("integerValue: %d\n" , integerValue));
            sb.append(String.format("email: %s\n", email));
            sb.append(String.format("password: %s\n", password));
            sb.append(String.format("items: %s\n", items != null ? join(items, ", ") : "null"));
            sb.append(String.format("selectedItems: %s\n", selectedItems != null ? join(selectedItems, ", ") : "null"));
            sb.append(String.format("remember: %b\n", remember));
            sb.append(String.format("variant1: %b\n", variant1));
            sb.append(String.format("variant2: %b\n", variant2));
            sb.append(String.format("availableItems: %s\n", availableItems != null ? join(availableItems, ", ") : "null"));
            sb.append(String.format("selectedItem: %s", selectedItem));
            return sb.toString();
        }
        
        private static String join(List strings, String separator) {
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for(Object s: strings) {
                sb.append(sep).append(s.toString());
                sep = separator;
            }
            return sb.toString();                           
        }
        
    }
    
    public static void main(String[] args) {
        final SampleForm jframe = new SampleForm();
        final Model model = new Model();
        model.addPropertyChangedListener(new IPropertyChangedListener() {
            @Override
            public void propertyChanged(String propertyName) {
                jframe.textareaModelState.setText(model.dump());
            }
        });
        model.getItems().addObservableListListener(new IObservableListListener() {
            public void listElementsAdded(IObservableList list, int index, int length) {
                jframe.textareaModelState.setText(model.dump());
            }
            public void listElementsRemoved(IObservableList list, int index, List oldElements) {
                jframe.textareaModelState.setText(model.dump());
            }
            public void listElementReplaced(IObservableList list, int index, Object oldElement) {
                jframe.textareaModelState.setText(model.dump());
            }
        });
        BindingGroup group = new BindingGroup(model);
        group.add(jframe.textInteger, "text", "integerValue", BindingMode.Default, UpdateSourceTrigger.PropertyChanged);
        group.add(jframe.textEmail, "text", "email", BindingMode.Default, UpdateSourceTrigger.PropertyChanged, new RequiredValidator() );
        group.add(jframe.password, "text", "password");
        group.add(jframe.checkboxRemember, "checked", "remember");
        group.add(jframe.listItems, "items", "items", BindingMode.OneWay);
        group.add(jframe.listItems, "selectedItems", "selectedItems", BindingMode.OneWayToSource);
        group.setBindingResultsListener( new IBindingResultsListener() {
            @Override
            public void onBinding( String sourceProperty, BindingResult result ) {
                if ("email".equals( sourceProperty )) {
                    if (result.hasError) {
                        jframe.textEmail.setBackground( Color.RED );
                    } else {
                        jframe.textEmail.setBackground( Color.WHITE );
                    }
                }
            }
        } );
        group.bind();

        jframe.buttonAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getItems().add(new ListItem());
            }
        });
        
        jframe.setVisible(true);
    }
}
