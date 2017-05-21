package net.tetrakoopa.mdu4j.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public abstract class AbstractUserAwareLogger<CONTEXT> implements Logger {

    private final Logger parent;

    private final static String UNKNOWN_USER_NAME = "<Unknown>";


	public AbstractUserAwareLogger(String parentName) {
		this(LoggerFactory.getLogger(parentName));
	}
	public AbstractUserAwareLogger(Class<?> parentClassName) {
		this(LoggerFactory.getLogger(parentClassName));
	}
    public AbstractUserAwareLogger(Logger parent) {
        this.parent = parent;
    }

    protected abstract String getUserName(CONTEXT context);

    protected String makeMessage(String message) {
        //final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //final String name = authentication == null ? UNKNOWN_USER_NAME : getUserNameOrDefault(authentication);
        return "name" +" : "+ message;
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return parent.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        parent.trace(makeMessage(msg));
    }

    @Override
    public void trace(String format, Object arg) {
        parent.trace(makeMessage(format), arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        parent.trace(makeMessage(format), arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        parent.trace(makeMessage(format), arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        parent.trace(makeMessage(msg), t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return parent.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        parent.trace(marker, makeMessage(msg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        parent.trace(marker, makeMessage(format), arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        parent.trace(marker, makeMessage(format), arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        parent.trace(marker, makeMessage(format), argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        parent.trace(marker, makeMessage(msg), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return parent.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        parent.debug(makeMessage(msg));
    }

    @Override
    public void debug(String format, Object arg) {
        parent.debug(makeMessage(format), arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        parent.debug(makeMessage(format), arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        parent.debug(makeMessage(format), arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        parent.debug(makeMessage(msg), t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return parent.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        parent.debug(marker, makeMessage(msg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        parent.debug(marker, makeMessage(format), arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        parent.debug(marker, makeMessage(format), arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        parent.debug(marker, makeMessage(format), argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        parent.debug(marker, makeMessage(msg), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return parent.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        parent.info(makeMessage(msg));
    }

    @Override
    public void info(String format, Object arg) {
        parent.info(makeMessage(format), arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        parent.info(makeMessage(format), arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        parent.info(makeMessage(format), arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        parent.info(makeMessage(msg), t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return parent.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        parent.info(marker, makeMessage(msg));
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        parent.info(marker, makeMessage(format), arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        parent.info(marker, makeMessage(format), arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        parent.info(marker, makeMessage(format), argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        parent.info(marker, makeMessage(msg), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return parent.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        parent.warn(makeMessage(msg));
    }

    @Override
    public void warn(String format, Object arg) {
        parent.warn(makeMessage(format), arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        parent.warn(makeMessage(format), arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        parent.warn(makeMessage(format), arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        parent.warn(makeMessage(msg), t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return parent.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        parent.warn(marker, makeMessage(msg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        parent.warn(marker, makeMessage(format), arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        parent.warn(marker, makeMessage(format), arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        parent.warn(marker, makeMessage(format), argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        parent.warn(marker, makeMessage(msg), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return parent.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        parent.error(makeMessage(msg));
    }

    @Override
    public void error(String format, Object arg) {
        parent.error(makeMessage(format), arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        parent.error(makeMessage(format), arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        parent.error(makeMessage(format), arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        parent.error(makeMessage(msg), t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return parent.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        parent.error(marker, makeMessage(msg));
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        parent.error(marker, makeMessage(format), arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        parent.error(marker, makeMessage(format), arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        parent.error(marker, makeMessage(format), argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        parent.error(marker, makeMessage(msg), t);
    }

}
