import binding.utils.PropertyInfo;
import binding.utils.PropertyUtils;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 22:21
 */

public class PropertyUtilsTest {

    private static class TestBean {
        private int x;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public boolean isReadOnly() {
            return true;
        }

        public void setAmbiguousProperty(boolean prop) {}
        public boolean isAmbiguousProperty(){return true;}
        public boolean getAmbiguousProperty(){return true;}

        public void setInvalidProp1(String x){}
        public int getInvalidProp1(){return 1;}

        public void setInvalidProp2(String x, int d){}
        public String getInvalidProp2(){return "";}
    }

    @Test
    public void testSimpleProperty() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "x");
        Assert.assertTrue(propertyInfo.clazz.equals(Integer.TYPE) &&
         propertyInfo.getter != null && propertyInfo.setter != null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArgs() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "X");
    }

    @Test(expected = RuntimeException.class)
    public void testNotFoundProperty() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "notfound");
    }

    @Test
    public void testReadOnlyProperty() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "readOnly");
        Assert.assertTrue(propertyInfo.clazz.equals(Boolean.TYPE) &&
        propertyInfo.getter != null && propertyInfo.setter == null);
    }

    @Test(expected = RuntimeException.class)
    public void testAmbiguousProperty() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "ambiguousProperty");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidProperty1() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "invalidProp1");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidProperty2() {
        PropertyInfo propertyInfo = PropertyUtils.getProperty(TestBean.class, "invalidProp2");
    }
}
