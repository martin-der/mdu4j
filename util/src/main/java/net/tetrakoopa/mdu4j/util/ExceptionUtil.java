package net.tetrakoopa.mdu4j.util;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ExceptionUtil {

	/**
	 * @param throwable : not null
	 * @return throwable messages separated by ' : '
	 * @deprecated : use <code>getMessages(Throwable throwable, String separator : null, String exceptionNameSeparator : null)</code> instead
	 */
	public static String getMessages(Throwable throwable) {
		return getMessages(throwable, false, null);
	}

	/**
	 * @param throwable : not null
	 * @param withExceptionName : if <code>true</code> prints exception class name, otherwise only prints exception message
	 * @return throwable messages separated by ' : '
	 * @deprecated : use <code>getMessages(Throwable throwable, String separator : null, String exceptionNameSeparator)</code> instead
	 */
	public static String getMessages(Throwable throwable, boolean withExceptionName) {
		return getMessages(throwable, withExceptionName, null);
	}

	/**
	 * @param throwable : not null
	 * @param withExceptionName : if <code>true</code> prints exception class name, otherwise only prints exception message
	 * @param separator : if null, defaults to ' : '
	 * @return throwable messages separated by <code>separator</code>
	 * @deprecated : use <code>getMessages(Throwable throwable, String separator, String exceptionNameSeparator)</code> instead
	 */
	public static String getMessages(Throwable throwable, boolean withExceptionName, String separator) {
		return getMessages(throwable, separator, withExceptionName ? ":": null);
	}
	/**
	 * @param throwable : not null
	 * @param separator : if null, defaults to ' : '
	 * @param exceptionNameSeparator : if not null, exception class name in added before the message
	 * @return throwable messages separated by <code>separator</code>
	 */
	public static String getMessages(Throwable throwable, String separator, String exceptionNameSeparator) {
		if (separator==null)
			separator = " : ";

		boolean first = true;
		final StringBuffer messages = new StringBuffer();

		do {
			if (first)
				first = false;
			else
				messages.append(separator);

			if (exceptionNameSeparator != null) {
				messages.append(throwable.getClass().getName());
				messages.append(exceptionNameSeparator);
			}
			messages.append(throwable.getMessage());

		} while ((throwable = throwable.getCause())!=null);

		return messages.toString();
	}

	public static String getStacktrace(Throwable exception) {
		try {
			return getStacktrace(exception, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static String getStacktrace(Throwable exception, String encoding) throws UnsupportedEncodingException {
		final ByteArrayOutputStream ouput = new ByteArrayOutputStream();
		exception.printStackTrace(new PrintStream(ouput));
		return new String(ouput.toByteArray(), encoding);
	}
}
