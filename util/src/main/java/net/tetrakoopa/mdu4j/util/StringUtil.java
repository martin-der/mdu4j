package net.tetrakoopa.mdu4j.util;

import java.util.Collection;
import java.util.Iterator;

public class StringUtil {

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

	public static String leftTrim(final String s) {
		int i = 0;
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		return s.substring(i);
	}

	public static String rightTrim(final String s) {
		int i = s.length() - 1;
		while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}
}
