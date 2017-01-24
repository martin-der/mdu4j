package net.tetrakoopa.mdu4j.test;

import junit.framework.Assert;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TestResourcesFetcherTest {

	@Test
	public void getResource() throws IOException {
		final String text = new TestResourcesFetcher().getTextForThisMethod("txt");
		Assert.assertEquals("That's exactly what I needed.", text);
	}

	@Test(expected = FileNotFoundException.class)
	public void cannotGetResource() throws IOException {
		new TestResourcesFetcher().getTextForThisMethod("txt");
	}
}
