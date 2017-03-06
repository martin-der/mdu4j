package net.tetrakoopa.mdu4j.front.servlet.parameter;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ParameterCloner extends CommonParameterHelper {

    private static class InnerVariables {
        Field fieldToChange = null;
    }

    private final static String NOT_CLONABLE_PREFIX_EXCEPTION = "Not clonable : ";

    private Map<Field, Object> allChanges = null;

    private final CommonUserParameter source;

    public ParameterCloner(CommonUserParameter source) {
        this.source = source;
    }

    private Map<Field, Object> changes() {
        if (allChanges == null) {
            allChanges = new HashMap<Field, Object>();
        }
        return allChanges;
    }

    public ParameterCloner change(final String name, String value) {
        final InnerVariables variables = new InnerVariables();
        ReflectionUtils.doWithFields(source.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (variables.fieldToChange != null) {
                    // TODO better yet : stop iteration
                    return;
                }
                final String keys[] = getKeys(field);
                for (String existingKey : keys) {
                    if (existingKey.equals(name)) {
                        variables.fieldToChange = field;
                        return;
                    }
                }
            }
        });
        if (variables.fieldToChange == null) {
            throw new IllegalArgumentException("'"+name+"' is not a attibute in "+source.getClass().getName());
        }
        changes().put(variables.fieldToChange, value);
        return this;
    }

    public CommonUserParameter cloneWithChanges() {
        final CommonUserParameter clone = createClone();

        for (Map.Entry<Field, Object> change : allChanges.entrySet()) {
            final Field field = change.getKey();
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, clone, change.getValue());
        }

        return clone;
    }
    public CommonUserParameter createClone() {
        try {
            return source.getClass().newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(NOT_CLONABLE_PREFIX_EXCEPTION+e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(NOT_CLONABLE_PREFIX_EXCEPTION+e.getMessage(), e);
        }
    }

}
