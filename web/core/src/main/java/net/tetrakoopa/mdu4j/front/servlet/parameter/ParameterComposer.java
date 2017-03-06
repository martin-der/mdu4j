package net.tetrakoopa.mdu4j.front.servlet.parameter;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * ( Tout à fait mal placé ! )
 */
@Service
public class ParameterComposer extends CommonParameterHelper implements CommonRequestParameter {

    private static class InnerVariables {
        boolean first = true;
    }

    /**
     * Write parameters from <code>parameter</code> as <code>key=value</code> to <code>result</code> buffer,<br/>
     * <ul>
     * <li>seperated by '&'</li>
     * <li>prefixed by '?' if any paramter was found</li>
     * </ul>
     */
    public void compose(final CommonUserParameter parameter, final PrintWriter writer) {
        final InnerVariables variables = new InnerVariables();
        ReflectionUtils.doWithFields(parameter.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                final String value = getKeyValue(field, parameter);

                if (value == null) {
                    return;
                }

                if (variables.first) {
                    variables.first = false;
                    writer.append('?');
                } else {
                    writer.append('&');
                }

                writer.append(value);
            }
        });
    }

    /**
     * Add parameters from <code>parameter</code> as <code>key=value</code> to <code>result</code> buffer,<br/>
     * <ul>
     * <li>seperated by '&'</li>
     * <li>prefixed by '?' if any paramter was found</li>
     * </ul>
     */
    public void compose(final CommonUserParameter parameter, final StringBuffer result) {
        final InnerVariables variables = new InnerVariables();
        ReflectionUtils.doWithFields(parameter.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                final String value = getKeyValue(field, parameter);

                if (value == null) {
                    return;
                }

                if (variables.first) {
                    variables.first = false;
                    result.append('?');
                } else {
                    result.append('&');
                }

                result.append(value);
            }
        });
    }
    public String getComposition(final CommonUserParameter parameter) {
        final StringBuffer buffer = new StringBuffer();
        compose(parameter, buffer);
        return buffer.toString();
    }

    private final String getKeyValue(Field field, CommonUserParameter parameter) throws IllegalAccessException {
        ReflectionUtils.makeAccessible(field);

        final Object rawValue = field.get(parameter);

        if (rawValue == null) {
            return null;
        }

        final String value;
        if (rawValue instanceof HtmlParameterEnum) {
            value = ((HtmlParameterEnum)rawValue).getParameterName();
        } else {
            value = rawValue.toString();
        }

        return getKeys(field)[0]+"="+ value;
    }

}