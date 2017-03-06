package net.tetrakoopa.mdu4j.front.servlet.bean;

import net.tetrakoopa.mdu4j.view.UIAttribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

public class DynamicForm {

    public interface Attribute extends UIAttribute {
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Excluded {
            boolean hidden() default true;
        }
    }

    public static class FormEntry {

        public enum Type {
            STRING, BOOLEAN, INTEGER, FLOAT, ENUM;

            public static Type fromClass(Class<?> clazz) {
                if (clazz == null) {
                    return null;
                }
                if (clazz.equals(String.class))
                    return STRING;
                if (clazz.equals(Boolean.class))
                    return BOOLEAN;
                if (clazz.equals(Integer.class) || clazz.equals(Long.class))
                    return INTEGER;
                if (clazz.equals(Float.class) || clazz.equals(Double.class))
                    return FLOAT;
                if (Enum.class.isAssignableFrom(clazz))
                    return ENUM;

                throw new IllegalArgumentException("No such " + Type.class.getName() + " matching " + clazz.getName());
            }
        }

        public final String name;
        public final String label;
        public final String value;
        public final Type type;
        public final boolean mandatory;
        public final boolean hidden;
        public final int order;

        private Map<String, String> extraAttributes;

        public Map<String, String> attributes() {
            if (extraAttributes == null) {
                extraAttributes = new HashMap<String, String>();
            }
            return extraAttributes;
        }

        public FormEntry(String name, String label, String value, Type type, boolean mandatory, boolean hidden, int order) {
            this.name = name;
            this.label = label;
            this.value = value;
            this.type = type;
            this.mandatory = mandatory;
            this.hidden = hidden;
            this.order = order;
        }

        public void addAttributes(Map<String, String> attributes) {
            extraAttributes.putAll(attributes);
        }
        public void addAttribute(String name, String value) {
            attributes().put(name, value);
        }
    }

    public Map<String, FormEntry> entries = new HashMap<String, FormEntry>();

    public FormEntry getEntry(String id) {
        if (!entries.containsKey(id))
            throw new IllegalArgumentException("No such field with name '"+id+"' for "+this.getClass().getName());
        return entries.get(id);
    }

}
