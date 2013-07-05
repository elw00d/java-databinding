package binding.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * User: igor.kostromin
 * Date: 26.06.13
 * Time: 22:24
 */
public class PropertyUtils {

    public static PropertyInfo getProperty(Class<?> cls, String propertyName) {
        if (null == cls) throw new IllegalArgumentException("cls is null");
        if (null == propertyName || propertyName.isEmpty())
            throw new IllegalArgumentException("propertyName is null or empty");
        if (!Character.isLowerCase(propertyName.charAt(0)))
            throw new IllegalArgumentException("propertyName starts with uppercase character");
        //
        String suffix = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        String getterName = "get" + suffix;
        String setterName = "set" + suffix;
        String isGetterName = "is" + suffix;
        Method[] methods = cls.getMethods();
        //
        Method getter = null;
        Method setter = null;
        Class<?> clazz = null;
        //
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()))
                continue;
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();
            if (method.getName().equals(getterName)) {
                if (parameterTypes.length > 0)
                    throw new RuntimeException("Property getter should not have arguments.");
                if (clazz != null) {
                    if (!clazz.equals(returnType))
                        throw new RuntimeException("Property getter has return type different to setter");
                } else {
                    clazz = returnType;
                }
                if (getter != null)
                    throw new RuntimeException("Ambiguous property getter: get-method and is-method are available both");
                getter = method;
            } else if (method.getName().equals(setterName)) {
                if (parameterTypes.length != 1)
                    throw new RuntimeException("Property setter should have one argument.");
                if (clazz != null) {
                    if (!clazz.equals(parameterTypes[0]))
                        throw new RuntimeException("Property setter has type different to getter.");
                } else {
                    clazz = parameterTypes[0];
                }
                setter = method;
            } else if (method.getName().equals(isGetterName)) {
                if (parameterTypes.length > 0)
                    throw new RuntimeException("Property getter should not have arguments.");
                if (!returnType.equals(Boolean.class) && !returnType.equals(Boolean.TYPE))
                    throw new RuntimeException("Property is-getter should have boolean return type");
                if (clazz != null) {
                    if (!clazz.equals(returnType))
                        throw new RuntimeException("Property getter has return type different to setter");
                } else {
                    clazz = returnType;
                }
                if (getter != null)
                    throw new RuntimeException("Ambiguous property getter: get-method and is-method are available both");
                getter = method;
            }
        }
        if (null == clazz || null == getter && null == setter)
            throw new RuntimeException(String.format("Property %s not found", propertyName));
        return new PropertyInfo(getter, setter, clazz);
    }
}
