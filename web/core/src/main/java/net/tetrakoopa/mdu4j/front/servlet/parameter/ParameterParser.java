package net.tetrakoopa.mdu4j.front.servlet.parameter;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletRequest;
import java.lang.reflect.Field;
import java.util.Date;

@Service
public class ParameterParser extends AbstractParameterParser {

    public void parse(final CommonUserParameter parameter, final ServletRequest request) {
        ReflectionUtils.doWithFields(parameter.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                final String keys[] = getKeys(field);
                ReflectionUtils.makeAccessible(field);

                String rawValue = null;
                for (String key : keys) {
                    rawValue = request.getParameter(key);
                    if (rawValue != null)  {
                        break;
                    }
                }

                final CommonRequestParameter.Attribute.EmptyIsNull emptyIsNullAnnotation = field.getAnnotation(CommonRequestParameter.Attribute.EmptyIsNull.class);
                final boolean emptyIsNull = emptyIsNullAnnotation != null && emptyIsNullAnnotation.value();

                if ("".equals(rawValue) && emptyIsNull) {
                    rawValue = null;
                }

                if (rawValue == null) {
                    return;
                }

                final Class<?> type = field.getType();
                if (type.equals(Boolean.class)) {
                    field.set(parameter, parseBoolean(rawValue));
                } else if (type.equals(String.class)) {
                    field.set(parameter, rawValue);
                } else if (type.equals(Float.class) || type.equals(Double.class)) {
                    field.set(parameter, parseFloat(rawValue));
                } else if (type.equals(Integer.class) || type.equals(Long.class)) {
                    field.set(parameter, parseInteger(rawValue));
                } else if (Date.class.isAssignableFrom(type)) {
                    field.set(parameter, parseDate(rawValue));
                } else if (Enum.class.isAssignableFrom(type)) {
                    field.set(parameter, parseEnum((Class<? extends Enum>)type, rawValue));
                } else {
                    throw new IllegalArgumentException("Don't know how to handle field '"+field.getName()+"' in '"+parameter.getClass().getName()+"'");
                }
            }
        });

    }
}
