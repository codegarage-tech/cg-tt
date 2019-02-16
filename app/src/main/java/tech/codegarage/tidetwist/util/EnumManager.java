package tech.codegarage.tidetwist.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Random;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class EnumManager {

    public static <T extends Enum<?>> T getRandomEnum(Class<T> clazz) {
        Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * Returns the <code>Enum</code> of type <code>enumType</code> whose a
     * public method return value of this Enum is
     * equal to <code>valor</code>.<br/>
     * Such method should be unique public, not final and static method
     * declared in Enum.
     * In case of more than one method in match those conditions
     * its first one will be chosen.
     *
     * @param enumType
     * @param value
     * @return
     */
    public static <E extends Enum<E>> E from(Class<E> enumType, Object value) {
        String methodName = getMethodIdentifier(enumType);
        return from(enumType, value, methodName);
    }

    /**
     * Returns the <code>Enum</code> of type <code>enumType</code> whose
     * public method <code>methodName</code> return is
     * equal to <code>value</code>.<br/>
     *
     * @param enumType
     * @param value
     * @param methodName
     * @return
     */
    public static <E extends Enum<E>> E from(Class<E> enumType, Object value, String methodName) {
        EnumSet<E> enumSet = EnumSet.allOf(enumType);
        for (E en : enumSet) {
            try {
                String invoke = enumType.getMethod(methodName).invoke(en).toString();
                if (invoke.equals(value.toString())) {
                    return en;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static String getMethodIdentifier(Class<?> enumType) {
        Method[] methods = enumType.getDeclaredMethods();
        String name = null;
        for (Method method : methods) {
            int mod = method.getModifiers();
            if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                name = method.getName();
                break;
            }
        }
        return name;
    }
}