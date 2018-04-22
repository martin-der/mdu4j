package net.tetrakoopa.mdu4j.util.test;

import java.text.ParseException;

import net.tetrakoopa.mdu4j.util.NetUtil;

import org.testng.Assert;
import org.testng.annotations.Test;


public class NetUtilTest {

	@Test
	public void heuu() {
		// Byte.decode("0xff");
		// Byte.parseByte("ff", 16);
		byte b = (byte) (Integer.parseInt("ff", 16) & 0xFF);

	}

	@Test
	public void testConvert4To6() {

		byte ipv4[] = { 127, 0, 0, 1 };

		byte[] ipv6 = NetUtil.convertIPv4ToIPv6(ipv4);

		Assert.assertEquals("00:00:00:00:00:00:00:00:00:00:FF:FF:7F:00:00:01", NetUtil.ipv6ToString(ipv6));

	}

	//@Test
	public void testConvert6to4() throws ParseException {
		
		byte[] ipv6 = NetUtil.parseIPv6("00:00:00:00:00:00:00:00:00:00:FF:FF:7F:00:00:01");

		byte[] ipv4 = NetUtil.convertIPv6toIPv4(ipv6);

		Assert.assertEquals(ipv4, NetUtil.LOCALHOST_IPV4);

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIPv6Invalid() {

		byte[] ipv6 = new byte[4];

		NetUtil.convertIPv6toIPv4(ipv6);

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIPv6Invalid2() {

		byte[] ipv6 = new byte[11];

		NetUtil.ipv6ToString(ipv6);

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIPv4Invalid() {

		byte[] ipv4 = new byte[5];

		NetUtil.convertIPv4ToIPv6(ipv4);

	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIPv4Invalid2() {

		byte[] ipv4 = new byte[3];

		NetUtil.ipv4ToString(ipv4);

	}

}
