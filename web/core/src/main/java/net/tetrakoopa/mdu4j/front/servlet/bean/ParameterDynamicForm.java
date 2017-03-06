package net.tetrakoopa.mdu4j.front.servlet.bean;

import net.tetrakoopa.mdu4j.front.servlet.parameter.CommonParameterHelper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ParameterDynamicForm extends DynamicForm {

    public final static String UI_ATTRIBUTE_PREFIX = "df";

    public DynamicForm.FormEntry getParameterEntry(Class<? extends CommonUserParameter> paramterClass, String name) {
        final Field field;
            field = CommonParameterHelper.getFieldForName(paramterClass, name);
        try {
            return getEntry(field.getName());
        } catch(RuntimeException rex) {
            throw new IllegalArgumentException("No such field with key name '"+name+"' for "+paramterClass.getName(), rex);
        }
    }

}
