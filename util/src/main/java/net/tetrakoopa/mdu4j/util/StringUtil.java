package net.tetrakoopa.mdu4j.util;

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
}
