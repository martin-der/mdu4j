package net.tetrakoopa.mdu4j.util.parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

public class EnvVarsLikeParser {

	public interface ValueResolver {
		String resolve(String key);
	}

	public static String expandVar(String text, ValueResolver resolver) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		expandVar(text, resolver, new PrintWriter(outputStream));

		return outputStream.toString();
	}

	public static void expandVar(String text, ValueResolver resolver, PrintWriter writer) {

		boolean inVerbatimText = true;

		String key;
		int actual;
		int nextStartMarker;
		int nextEndMarker;
		final int length = text.length();

		actual = 0;

		while (true) {

			if (inVerbatimText) {
				nextStartMarker = text.indexOf("${", actual);
				if (nextStartMarker >= actual) {
					writer.append(text, actual, nextStartMarker);
					actual = nextStartMarker + "${".length();
					inVerbatimText = false;
				} else {
					writer.append(text, actual, length);
					return;
				}

			} else {
				nextEndMarker = text.indexOf("}", actual);
				if (nextEndMarker >= actual) {
					key = text.substring(actual, nextEndMarker);
					final String value;
					try {
						value = resolver.resolve(key);
					} catch (Exception ex) {
						// TODO Type this exception
						throw new RuntimeException("Exception while lookuping key '" + key + "' : " + ex.getMessage(), ex);
					}
					writer.append(value);
					actual = nextEndMarker + "}".length();
					inVerbatimText = true;
				} else {
					key = text.substring(actual, length);
					// TODO Type this exception
					throw new RuntimeException("Unterminated key '" + key + "'");
				}
			}

			key = text.substring(0, 10);
			writer.append(resolver.resolve(key));
		}
	}

}
