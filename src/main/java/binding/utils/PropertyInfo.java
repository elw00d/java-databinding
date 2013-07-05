package binding.utils;

import java.lang.reflect.Method;

/**
* User: igor.kostromin
* Date: 27.06.13
* Time: 0:42
*/
public class PropertyInfo {
    public Method getter;
    public Method setter;
    public Class<?> clazz;

    public PropertyInfo(Method getter, Method setter, Class<?> clazz) {
        this.getter = getter;
        this.setter = setter;
        this.clazz = clazz;
    }
}
