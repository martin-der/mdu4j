package net.tetrakoopa.mdu4j.util;

import net.tetrakoopa.mdu4j.util.FormatterUtil;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.*;

public class FileUtil {

	public static String filename(String path) {
		final String separator = System.getProperty("path.separator");
		final int pos = path.indexOf(separator);
		return pos < 0 ? path : path.substring(pos+separator.length());
	}

	public static String getAsString(Reader reader) throws IOException {
		final char buffer[] = new char[200];
		int l;
		final StringBuffer stringBufferSource = new StringBuffer();
		while ((l=reader.read(buffer))>0) { stringBufferSource.append(buffer, 0, l); }
		return stringBufferSource.toString();
	}

	/**
	 * @deprecated use <code>net.tetrakoopa.mdu4j.util.IOUtil</code> instead
	 */
	@Deprecated
	public static void copy(InputStream input, OutputStream output) throws IOException {
		IOUtil.copy(input, output, new byte[4*1024]);
	}
	/**
	 * @deprecated use <code>net.tetrakoopa.mdu4j.util.IOUtil</code> instead
	 */
	@Deprecated
	public static void copy(InputStream input, OutputStream output, final byte buffer[]) throws IOException {
		IOUtil.copy(input, output, buffer);
	}

	/**
	 * @deprecated use <code>net.tetrakoopa.mdu4j.util.IOUtil</code> instead
	 */
	@Deprecated
	public static CharSequence readCharSequence(InputStream inputStream) throws IOException {
		return IOUtil.readString(inputStream, IOUtil.DEFAULT_ENCODING);
	}
	/**
	 * @deprecated use <code>net.tetrakoopa.mdu4j.util.IOUtil</code> instead
	 */
	@Deprecated
	public static CharSequence readCharSequence(InputStream input, String encoding) throws IOException {
		return IOUtil.readString(input, encoding);
	}

	/**
	 * @deprecated use <code>net.tetrakoopa.mdu4j.util.IOUtil</code> instead
	 */
	@Deprecated
	public static byte[] readContent(InputStream input) throws IOException {
		return IOUtil.read(input);
	}

}
