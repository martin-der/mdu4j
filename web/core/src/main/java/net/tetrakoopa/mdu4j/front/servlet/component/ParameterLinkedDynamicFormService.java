package net.tetrakoopa.mdu4j.front.servlet.component;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.front.servlet.parameter.CommonParameterHelper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class ParameterLinkedDynamicFormService extends DynamicFormService<CommonUserParameter> {

    @Override
    protected String getValue(Object value) {
        if (value instanceof Enum) {
            return HtmlParameterEnum.Util.asHtmlParameterEnum((Enum)value).getParameterName();
        }
        return null;
    }

    @Override
    protected String getName(final Field field) {
        return CommonParameterHelper.getKeys(field)[0];
    }

}
