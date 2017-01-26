package net.tetrakoopa.mdu4j.util;


import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class FormatterUtilTest {

	static {
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void formattedSizeOfTheSizeOfCatPicture() {
		String formattedSize;
		long size;

		size = 2000000;
		formattedSize = FormatterUtil.fileSizeAsString(size);
		assertEquals("1.907 mb.", formattedSize);
	}

	@Test
	public void customFormattedSizeOfTheSizeOfASmallMovie() {
		String formattedSize;
		long size;

		size = 600000000;
		formattedSize = FormatterUtil.fileSizeAsString(size, "%1$.5f (%2$sb)");
		assertEquals("572.20459 (mb)", formattedSize);

	}

}
