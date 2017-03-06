package net.tetrakoopa.mdu4j.front.servlet.parameter;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public abstract class CommonParameterHelper {

    private static class FoundField {
        public Field field;
    }


    /**
     * Find the keys that can be used for this field, defaults to the name of the field if no key is defined
     * @return a non null array, with a least one element
     */
    public static String[] getKeys(Field field) {
        final CommonRequestParameter.Attribute.Key keyAnnotation = field.getAnnotation(CommonRequestParameter.Attribute.Key.class);
        return (keyAnnotation != null && keyAnnotation.value().length>0) ? keyAnnotation.value() : new String[] {field.getName()};
    }

    public static Field getFieldForName(Class<? extends CommonUserParameter> parameterClass, final String name) {
        final FoundField foundField = new FoundField();
        ReflectionUtils.doWithFields(parameterClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                for (String key : getKeys(field)) {
                    if (key.equals(name))  {
                        foundField.field = field;
                        return;
                    }
                }
            }
        });
        if (foundField.field == null) {
            throw new IllegalArgumentException("Class '"+parameterClass.getName()+"' has no field named '"+name+"'");
        }
        return foundField.field;

    }

}
