package net.tetrakoopa.mdu4j.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

public class StringUtil {

	public interface ToStringable<E> {
		String asString(E object);
	}

	public interface QuickWriter<T> {
		void write(T object, StringBuffer buffer);

	}

	public static <T> void concatCollection(StringBuffer buffer, Collection<T> collection, String separator) {
		concatCollection(buffer, collection, separator, null);
	}

	public static <T> void concatCollection(StringBuffer buffer, Collection<T> collection, String separator, QuickWriter<T> writer) {

		boolean first = false;

		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			final T next = iterator.next();
			if (writer != null) {
				writer.write(next, buffer);
			} else {
				buffer.append(next);
			}
		}

	}

	public static String suroundBy(String left, String string, String right, int beginIndex, int endIndex) {
		final int length = string.length();
		String surounded = string.substring(beginIndex, endIndex);

		if (left != null)
			surounded = left + surounded;
		if (right != null)
			surounded = surounded + right;

		if (beginIndex > 0)
			surounded = string.substring(0, beginIndex) + surounded;
		if (endIndex < length)
			surounded = surounded + string.substring(endIndex);

		return surounded;
	}

	public static String firstCharToUpperCase(String string) {
		if (string == null) {
			return null;
		}
		if (string.length()>0) {
			final StringBuffer majusculeString = new StringBuffer();
			majusculeString.append(Character.toUpperCase(string.charAt(0)));
			majusculeString.append(string.substring(1));
			return majusculeString.toString();
		} else {
			return string;
		}
	}
	public static char[] escapeRegex(char string[]) {
		final StringBuffer escaped = new StringBuffer();
		final int len = string.length;
		for (int i =0; i<len; i++) {
			final char c = string[i];
			if (".*?()[]{}^S|+".contains(String.valueOf(c))) {
				escaped.append('\\');
			}
			if (c=='\\' && i<len-1) {
				i++;
				continue;
			}
			escaped.append(c);
		}
		return escaped.toString().toCharArray();
	}
	public static String escapeXml(String string) {
		return string.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
	}
	public static String escapeXmlAttribute(String string) {
		return string.replace("\'", "&apos;'").replace("\"","&quot;");
	}

	public static String escapeHtml(String string) {
		final StringBuffer buffer = new StringBuffer(string.length());
		boolean lastWasBlankChar = false;
		final int len = string.length();
		char c;

		for (int i = 0; i < len; i++)
		{
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					buffer.append("&nbsp;");
				}
				else {
					lastWasBlankChar = true;
					buffer.append(' ');
				}
			}
			else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					buffer.append("&quot;");
				else if (c == '&')
					buffer.append("&amp;");
				else if (c == '<')
					buffer.append("&lt;");
				else if (c == '>')
					buffer.append("&gt;");
				else if (c == '\n')
					// Handle Newline
					buffer.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160 )
						// nothing special only 7 Bit
						buffer.append(c);
					else {
						// Not 7 Bit use the unicode system
						buffer.append("&#");
						buffer.append(Integer.toString(ci));
						buffer.append(';');
					}
				}
			}
		}
		return buffer.toString();
	}
	public static String escapeLiteralString(String string) {
		return string.replace("\"", "\\\"").replace("\r\n","\\n").replace("\n","\\n").replace("\r","\\n");
	}

	public static String byte2Hex(byte[] bytes) {
		return byte2Hex(bytes, " ");
	}
	public static String byte2Hex(byte[] bytes, String twoBytesSeparator) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < bytes.length; i++) {
			if (first) {
				first = false;
			} else if (twoBytesSeparator != null) {
				sb.append(twoBytesSeparator);
			}
			int b = bytes[i] & 0xff;
			if (b < 0x10)
				sb.append('0');
			sb.append(Integer.toHexString(b));
		}
		return sb.toString();
	}

	public static String camelCase2UpperCaseUnderscoreSeparated(String string) {
		return camelCase2UnderscoreSeparated(string, true);
	}
	public static String camelCase2UnderscoreSeparated(String string, boolean upper) {
		final String regex = "([a-z])([A-Z]+)";
		final String replacement = "$1_$2";
		final String separated =  string.replaceAll(regex, replacement);
		return upper ? separated.toUpperCase() : separated.toLowerCase();
	}

	public static String concatToString(Object objects[], ToStringable<Object> convertor, String separator) {
		final StringBuffer buffer = new StringBuffer();
		concatIntoBuffer(buffer, objects, convertor, separator);
		return buffer.toString();
	}
	public static void concatIntoBuffer(StringBuffer buffer, Object objects[], ToStringable<Object> convertor, String separator) {
		boolean first = true;
		for (Object object : objects) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(object == null ? object : (convertor == null ? object.toString() : convertor.asString(object)));
		}
	}

	public static String concatToString(String strings[], String separator) {
		final StringBuffer buffer = new StringBuffer();
		concatIntoBuffer(buffer, strings, separator);
		return buffer.toString();
	}
	public static void concatIntoBuffer(StringBuffer buffer, String strings[], String separator) {
		boolean first = true;
		for (String string : strings) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(string);
		}
	}

	// ----------

	public static String stringifiedCollectionValues(Collection<?> values, int maxDepth) {
		final StringBuffer stringBuffer = new StringBuffer("[ ");
		boolean isFirst = true;
		for (Object value : values){
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuffer.append(", ");
			}
			stringBuffer.append(stringifiedValue(value, maxDepth));
		}
		stringBuffer.append(isFirst ? "]" : " ]");
		return stringBuffer.toString();
	}

	public static String stringifiedArrayValues(Object[] values, int maxDepth) {
		final StringBuffer stringBuffer = new StringBuffer("[ ");
		boolean isFirst = true;
		for (Object value : values){
			if (isFirst) {
				isFirst = false;
			} else {
				stringBuffer.append(", ");
			}
			stringBuffer.append(stringifiedValue(value, maxDepth));
		}
		stringBuffer.append(isFirst ? "]" : " ]");
		return stringBuffer.toString();
	}

	public static String stringifiedValue(Object value) {
		return stringifiedValue(value, 3);
	}
	public static String stringifiedValue(Object value, int maxDepth) {

		if (maxDepth <= 0) {
			return "...";
		}

		maxDepth --;

		if (value instanceof String) {
			return (String)value;
		}
		if (value instanceof Array) {
			return stringifiedArrayValues((Object[])value, maxDepth);
		}
		if (value instanceof Collection) {
			return stringifiedCollectionValues((Collection<?>) value, maxDepth);
		}
		/*if (value instanceof org.springframework.cache.support.SimpleValueWrapper) {
			final Object unwrappedValue = ((org.springframework.cache.support.SimpleValueWrapper)value).get();
			if (! (unwrappedValue instanceof org.springframework.cache.support.SimpleValueWrapper)) {
				return stringifiedValue(unwrappedValue);
			}
		}*/

		return value == null ? null : value.toString();
	}


}
