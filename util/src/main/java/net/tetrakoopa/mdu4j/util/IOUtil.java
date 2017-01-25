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

	public static final String DEFAULT_ENCODING = "utf-8";

	private final static int DEFAULT_BUFFER_SIZE = 2000;
	private static int buffer_size = DEFAULT_BUFFER_SIZE;

	public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		
		byte buffer[] = new byte[buffer_size];

		copy(inputStream, outputStream, buffer);
	}

	public static void copy(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
		int l;
		while ((l=inputStream.read(buffer))>0) {
			outputStream.write(buffer, 0, l);
		}
	}

	public static void copy(String source, OutputStream outputStream) throws IOException {
		copy(source, outputStream, new byte[buffer_size]);
	}
	public static void copy(String source, OutputStream outputStream, byte[] buffer) throws IOException {
		IOUtil.copy(new ByteArrayInputStream(source.getBytes()), outputStream, buffer);
	}

	public static void copy(byte[] source, OutputStream outputStream) throws IOException {
		copy(source, outputStream, new byte[buffer_size]);
	}

	public static void copy(byte[] source, OutputStream outputStream, byte[] buffer) throws IOException {
		IOUtil.copy(new ByteArrayInputStream(source), outputStream, buffer);
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

	/* -- Get resource -- */

	public static InputStream getResourceInputStream(final String resource) throws FileNotFoundException {
		return getResourceInputStream(resource, null);
	}

	public static InputStream getResourceInputStream(final String resource, ClassLoader classLoader) throws FileNotFoundException {
		if (classLoader == null)
			classLoader = IOUtil.class.getClassLoader();
		final InputStream stream = classLoader.getResourceAsStream(resource);
		if (stream == null) {
			throw new FileNotFoundException("Could not find resource '" + resource + "' in classpath");
		}
		return stream;
	}

	public static String readString(final String resource, final String encoding) throws IOException {
		return readString(resource, IOUtil.class.getClassLoader(), encoding);
	}

	public static String readString(final String resource, final ClassLoader classLoader, final String encoding)
	throws IOException {
		return readString(getResourceInputStream(resource, classLoader), encoding);
	}
	
	public static String readString(final String resource) throws IOException {
		return readString(resource, IOUtil.class.getClassLoader());
	}

	public static String readString(final String resource, final ClassLoader classLoader)
	throws IOException {
		return readString(getResourceInputStream(resource, classLoader), DEFAULT_ENCODING);
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

	public static String readRawString(final String resource, final String encoding) throws IOException {
		return readRawString(resource, IOUtil.class.getClassLoader(), encoding);
	}

	public static String readRawString(final String resource, final ClassLoader classLoader, final String encoding)
			throws IOException {
		return readRawString(getResourceInputStream(resource, classLoader), encoding);
	}

	public static String readRawString(final String resource) throws IOException {
		return readRawString(resource, IOUtil.class.getClassLoader());
	}

	public static String readRawString(final String resource, final ClassLoader classLoader)
			throws IOException {
		return readRawString(getResourceInputStream(resource, classLoader), DEFAULT_ENCODING);
	}

	public static String readRawString(final InputStream input)
			throws IOException {
		return readRawString(input, DEFAULT_ENCODING);
	}

	public static String readRawString(final InputStream input, final String encoding)
			throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		int l;
		final byte buffer [] = new byte[1024];
		while((l=input.read(buffer))>0) {
			output.write(buffer,0,l);;
		}

		return new String(output.toByteArray(), encoding);
	}

}
