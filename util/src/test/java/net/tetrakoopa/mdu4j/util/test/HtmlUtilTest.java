package net.tetrakoopa.mdu4j.util.test;

import net.tetrakoopa.mdu4j.util.HtmlUtil;

import org.junit.Test;

public class HtmlUtilTest {

	@Test
	public void makeALogLineLookingHTML() {
		String result = HtmlUtil.readLogFile("DEBUG qsdqs dqs \nINFO qsd qsd qsd qsd qsd\nOUPS azeaozeiuoiausqd qsldkjqlskjd");
		System.out.println(result);
	}
}
