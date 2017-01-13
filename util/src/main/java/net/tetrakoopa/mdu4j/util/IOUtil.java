package net.tetrakoopa.mdu4j.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class IOUtil {
	
	public final static String DEFAULT_ENCODING = "utf8";
	
	private static int default_buffer_size = 2000;

	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		
		byte buffer[] = new byte[default_buffer_size];

		copy(inputStream, outputStream, buffer);
	}

	public static void copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
		int l;
		while ((l=inputStream.read(buffer))>0) {
			outputStream.write(buffer, 0, l);
		}
	}

	public static void copy(String source, OutputStream outputStream) throws IOException {

		byte buffer[] = new byte[default_buffer_size];

		copy(source, outputStream, buffer);
	}
	public static void copy(String source, OutputStream outputStream, byte[] buffer) throws IOException {
		IOUtil.copy(new ByteArrayInputStream(source.getBytes()), outputStream);
	}

	public static void copy(byte[] source, OutputStream outputStream) throws IOException {

		byte buffer[] = new byte[default_buffer_size];

		copy(source, outputStream, buffer);
	}

	public static void copy(byte[] source, OutputStream outputStream, byte[] buffer) throws IOException {
		IOUtil.copy(new ByteArrayInputStream(source), outputStream);
	}

	/* -- Open Input Stream -- */

	public static InputStream getInputStream(final String resource) throws FileNotFoundException {
		return getInputStream(resource, IOUtil.class.getClassLoader());
	}

	public static InputStream getInputStream(final String resource, final ClassLoader classLoader) throws FileNotFoundException {
		final InputStream stream = classLoader.getResourceAsStream(resource);
		if (stream == null) {
			throw new FileNotFoundException("Could not find resource '" + resource + "' in classpath");
		}
		return stream;
	}

	/* -- Read Byte Array -- */

	public static byte[] read(final String resource) throws IOException {
		return read(resource, IOUtil.class.getClassLoader());
	}

	public static byte[] read(InputStream intputStream) throws IOException {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		copy(intputStream, buffer);
		return buffer.toByteArray();
	}

	public static byte[] read(final String resource, final ClassLoader classLoader) throws IOException {
		return read(resource, classLoader);
	}

	/* -- Read as String -- */

	public static String readString(final String resource, final String encoding) throws IOException {
		return readString(resource, IOUtil.class.getClassLoader(), encoding);
	}

	public static String readString(final String resource, final ClassLoader classLoader, final String encoding)
	throws IOException {
		return readString(getInputStream(resource, classLoader), encoding);
	}
	
	public static String readString(final String resource) throws IOException {
		return readString(resource, IOUtil.class.getClassLoader());
	}

	public static String readString(final String resource, final ClassLoader classLoader)
	throws IOException {
		return readString(getInputStream(resource, classLoader), DEFAULT_ENCODING);
	}
	
	public static String readString(final InputStream input)
	throws IOException {
		return readString(input, DEFAULT_ENCODING);
	}
	
	public static String readString(final InputStream input, final String encoding)
	throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input, encoding));
		
		final StringBuffer result = new StringBuffer();
		
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
			result.append('\n');
		}
		
		return result.toString();
	}

}
