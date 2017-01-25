package net.tetrakoopa.mdu4j.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestResourcesFetcher {

	public static final String DEFAULT_ENCODING = "utf-8";

	public String resourceNameForMethod() {
		final String thisClassName = TestResourcesFetcher.class.getName();
		final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		boolean enteredThisClassOnce = false;
		for (StackTraceElement element : elements) {
			final String className = element.getClassName();
			if (!className.equals(thisClassName)) {
				if (enteredThisClassOnce) {
					return className.replace(".","/")+"."+element.getMethodName();
				}
			} else {
				enteredThisClassOnce = true;
			}
		}
		throw new RuntimeException("Could not find out calling method");
	}

	public InputStream getResourceForThisMethod(ClassLoader classLoader) throws FileNotFoundException {
		return getResourceForThisMethod(null, classLoader);
	}
	public InputStream getResourceForThisMethod(String suffix) throws FileNotFoundException {
		return getResourceForThisMethod(suffix, null);
	}
	public InputStream getResourceForThisMethod(String suffix, ClassLoader classLoader) throws FileNotFoundException {
		String resourceName = resourceNameForMethod();
		if (suffix!=null) {
			resourceName = resourceName+"."+suffix;
		}
		if (classLoader == null)
			classLoader = TestResourcesFetcher.class.getClassLoader();
		final InputStream stream = classLoader.getResourceAsStream(resourceName);
		if (stream == null) {
			throw new FileNotFoundException();
		}
		return stream;
	}

	public String getTextForThisMethod(String suffix, ClassLoader classLoader) throws IOException {
		return getTextForThisMethod(suffix, classLoader, null);
	}
	public String getTextForThisMethod(String suffix) throws IOException {
		return getTextForThisMethod(suffix, null, null);
	}
	public String getTextForThisMethod(String suffix, String encoding) throws IOException {
		return getTextForThisMethod(suffix, null, encoding);
	}
	public String getTextForThisMethod(ClassLoader classLoader) throws IOException {
		return getTextForThisMethod(null, classLoader, null);
	}
	public String getTextForThisMethod() throws IOException {
		return getTextForThisMethod(null, null, null);
	}
	public String getTextForThisMethod(String suffix, ClassLoader classLoader, String encoding) throws IOException {
		if (encoding == null)
			encoding = DEFAULT_ENCODING;
		return readRawString(getResourceForThisMethod(suffix, classLoader), encoding);
	}

	private String readString(final InputStream input, final String encoding)
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

	private String readRawString(final InputStream input, final String encoding) throws IOException {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		int l;
		final byte buffer [] = new byte[1024];
		while((l=input.read(buffer))>0) {
			output.write(buffer,0,l);;
		}

		return new String(output.toByteArray(), encoding);
	}

}
