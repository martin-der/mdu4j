package net.tetrakoopa.mdu4j.front.servlet.helper;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

public class ServletInitParameterHelper {

    public static int getIntegerParameter(HttpServlet servlet, String name) {
        final Integer value = getIntegerParameter(servlet, name, null);
        if (value == null) {
            throw new IllegalArgumentException("No such parameter '"+name+"'");
        }
        return value;
    }
    public static Integer getIntegerParameter(HttpServlet servlet, String name, Integer defaultz) {
        final String stringValue = getTrimmedParameter(servlet, name, null);
        if (stringValue == null) {
            return defaultz;
        }
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Parameter '"+name+"' is not a valid Integer");
        }
    }
    public static String getTrimmedParameter(HttpServlet servlet, String name) {
        return getTrimmedParameter(servlet, name, null);
    }
    public static String getTrimmedParameter(HttpServlet servlet, String name, String defaultz) {
        final ServletConfig config = servlet.getServletConfig();
        final String value = config.getInitParameter(name);
        return value == null ? defaultz : value.trim();
    }
    public static String[] getTrimmedParametersList(HttpServlet servlet, String name, String separator ) {
        return getTrimmedParametersList(servlet, name, separator, null);
    }
    public static String[] getTrimmedParametersList(HttpServlet servlet, String name, String separator, String defaultz[] ) {
        final ServletConfig config = servlet.getServletConfig();
        final String rawValue = config.getInitParameter(name);
        final String values[] = rawValue == null ? defaultz : rawValue.split(separator);
        if (values == null) {
            return null;
        }
        int i = 0;
        for (String value : values) {
            values[i++] = value.trim();
        }
        return values;
    }

}
