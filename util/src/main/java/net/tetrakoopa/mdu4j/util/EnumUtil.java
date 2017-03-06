package net.tetrakoopa.mdu4j.util;

import java.lang.reflect.Field;

public class EnumUtil {

    public static <E extends Enum<E>> Field getValue(Class<E> enumClass, E enumValue) {
        try {
            return enumClass.getField(enumValue.name());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}