import binding.*;
import binding.converters.ConversionResult;
import binding.converters.IBindingConverter;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: igor.kostromin
 * Date: 01.07.13
 * Time: 23:19
 */
public class BindingSettingsTest {
    /**
     * Converters between collections are used only if collection object (real object at run time)
     * does not implement IObservableList.
     */
    public static class ListToArrayConverter implements IBindingConverter<String[], List> {

        @Override
        public Class<String[]> getFirstClazz() {
            return String[].class;
        }

        @Override
        public Class<List> getSecondClazz() {
            return List.class;
        }

        @Override
        public ConversionResult<List> convert(String[] strings) {
            if (strings == null) return new ConversionResult<List>(null);
            List res = new ArrayList(strings.length);
            for (String s : strings) res.add(s);
            return new ConversionResult<List>(res);
        }

        @Override
        public ConversionResult<String[]> convertBack(List list) {
            if (list == null ) return new ConversionResult<String[]>(null);
            return new ConversionResult<String[]>((String[]) list.toArray(new String[list.size()]));
        }
    }

    public static class Source implements INotifyPropertyChanged {
        private List<String> list ;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
            raisePropertyChange("list");
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

    public static class Target {
        private String[] array;

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }
    }

    @Test
    public void bindingSettingsTest() {
        BindingSettingsBase settings = new BindingSettingsBase();
        settings.initializeDefault();
        settings.addConverter(new ListToArrayConverter());

        Source source = new Source();
        Target target = new Target();
        BindingBase binding = new BindingBase(target, "array", source, "list", BindingMode.OneWay, settings);
        binding.bind();
        Assert.assertTrue(target.getArray() == null);
        source.setList(new ArrayList<String>() {{
            add("1");
            add("2");
        }});
        Assert.assertTrue(target.getArray()[0].equals("1") && target.getArray()[1].equals("2"));
    }
}
