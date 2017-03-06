package net.tetrakoopa.mdu4j.front.servlet.parameter;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;

import java.util.Arrays;
import java.util.Date;

public abstract class AbstractParameterParser extends CommonParameterHelper implements CommonRequestParameter {
    protected Boolean parseBoolean(String value) {
        if (value== null) {
            return null;
        }

        for (String trueString : VALUES_TRUE) {
            if (value.equals(trueString)) {
                return true;
            }
        }
        for (String trueString : VALUES_FALSE) {
            if (value.equals(trueString)) {
                return false;
            }
        }


        throw new IllegalArgumentException("Parameter value '"+value+"' is a not a legal boolean. Use one of "+ Arrays.toString(VALUES_TRUE)+" or "+ Arrays.toString(VALUES_FALSE));
    }

    protected Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfex) {
            throw new IllegalArgumentException("Parameter value '"+value+"' is a not a legal integer");
        }
    }

    protected Float parseFloat(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException nfex) {
            throw new IllegalArgumentException("Parameter value '"+value+"' is a not a legal integer");
        }
    }

    protected <E extends Enum<E>> E parseEnum(Class<E> clazz, String value) {
        if (value == null) {
            return null;
        }

        try {
            return HtmlParameterEnum.Util.fromParameterName(clazz, value);
        } catch (HtmlParameterEnum.NoSuchEnumException nseex) {
            throw new IllegalArgumentException("Parameter value '"+value+"' is a not a legal "+clazz.getName());
        }
    }

    protected Date parseDate(String value) {
        if (value== null) {
            return null;
        }

        throw new IllegalStateException("Not Implemented Yet");
    }
}
