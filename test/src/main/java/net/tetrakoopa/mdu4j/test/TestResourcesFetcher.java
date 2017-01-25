package net.tetrakoopa.mdu4j.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public InputStream getResourceForThisMethod(ClassLoader classLoader) {
		return getResourceForThisMethod(null, classLoader);
	}
	public InputStream getResourceForThisMethod(String suffix) {
		return getResourceForThisMethod(suffix, null);
	}
	public InputStream getResourceForThisMethod(String suffix, ClassLoader classLoader) {
		String resourceName = resourceNameForMethod();
		if (suffix!=null) {
			resourceName = resourceName+"."+suffix;
		}
		if (classLoader == null)
			classLoader = TestResourcesFetcher.class.getClassLoader();
		final InputStream stream = classLoader.getResourceAsStream(resourceName);
		if (stream == null) {
			throw new IllegalArgumentException("Resource '"+resourceName+"'not found");
		}
		return stream;
	}

	public String getTextForThisMethod(String suffix, ClassLoader classLoader) throws IOException {
		return getTextForThisMethod(suffix, classLoader, null);
	}
	public String getTextForThisMethod(String suffix) {
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
	public String getTextForThisMethod(String suffix, ClassLoader classLoader, String encoding) {
		if (encoding == null)
			encoding = DEFAULT_ENCODING;
		return readRawString(getResourceForThisMethod(suffix, classLoader), encoding);
	}

	private String readRawString(final InputStream input, final String encoding) {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {

			int l;
			final byte buffer [] = new byte[1024];
				while((l=input.read(buffer))>0) {
					output.write(buffer,0,l);;
				}

			return new String(output.toByteArray(), encoding);
		} catch (IOException e) {
			throw new RuntimeException("Unable to fetch text : "+e.getMessage(), e);
		}
	}

}
