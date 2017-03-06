package net.tetrakoopa.mdu4j.util;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class IOUtilTest {

	@Test
	public void readAResourceFileWithVariousCR() throws IOException {
		final InputStream input = getClass().getClassLoader().getResourceAsStream("net/tetrakoopa/mdu4j/util/IOUtilTest.readAResourceFileWithDifferentCR.txt");
		final String text = IOUtil.readRawString(input);
		Assert.assertEquals("This\nis\nmultilines\ntext", text);
	}

	@Test
	public void readAResourceFile() throws IOException {
		final InputStream input = getClass().getClassLoader().getResourceAsStream("net/tetrakoopa/mdu4j/util/IOUtilTest.readAResourceFileWithDifferentCR.txt");
		final String text = IOUtil.readRawString(input);
		Assert.assertEquals("This\nis\nmultilines\ntext", text);
	}
}
