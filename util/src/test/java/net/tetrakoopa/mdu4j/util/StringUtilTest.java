package net.tetrakoopa.mdu4j.util;

import net.tetrakoopa.mdu4j.test.TestResourcesFetcher;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StringUtilTest {

	@Test
	public void makeByte2Hex() {
		byte bytes[] = {(byte)0xff, (byte)0x49, (byte)0xb1,  (byte)0x5e};
		String bytesString = StringUtil.byte2Hex(bytes);

		assertEquals("ff 49 b1 5e", bytesString);

	}

	@Test
	public void majuscule() {
		String string = "hello";
		String majusculeString = StringUtil.firstCharToUpperCase(string);

		assertEquals("Hello", majusculeString);
	}

	@Test
	public void majusculeEmpty() {
		String string = "";
		String majusculeString = StringUtil.firstCharToUpperCase(string);

		assertEquals("", majusculeString);
	}

	@Test
	public void majusculeSingleChar() {
		String string = "a";
		String majusculeString = StringUtil.firstCharToUpperCase(string);

		assertEquals("A", majusculeString);
	}

	@Test
	public void convertSomeCamel2_SOME_CAMEL() {
		String string = "MushroomCookingHelper";
		String UPPER_STRING = StringUtil.camelCase2UpperCaseUnderscoreSeparated(string);

		assertEquals("MUSHROOM_COOKING_HELPER", UPPER_STRING);
	}

	@Test
	public void escapeLiteralString() throws IOException {
		String text = new TestResourcesFetcher().getTextForThisMethod("txt");
		String result = StringUtil.escapeLiteralString(text);
		assertEquals("This\\n'is'\\nmultilines\\ntext with \\\"many types\\\" of carriage return", result);
	}

}