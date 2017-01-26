package net.tetrakoopa.mdu4j.util;


import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class FormatterUtilTest {

	@Test
	public void formattedSizeOfTheSizeOfCatPicture() {
		Locale.setDefault(Locale.ENGLISH);

		String formattedSize;
		long size;

		size = 2000000;
		formattedSize = FormatterUtil.fileSizeAsString(size);
		assertEquals("1.907 mB.", formattedSize);
	}

	@Test
	public void customFormattedSizeOfTheSizeOfASmallMovie() {
		Locale.setDefault(Locale.FRENCH);

		String formattedSize;
		long size;

		size = 600000000;
		formattedSize = FormatterUtil.fileSizeAsString(size, "%1$.5f (%2$so)");
		assertEquals("572,20459 (mo)", formattedSize);

	}

}
