package net.tetrakoopa.mdu4j.util;

import java.text.ParseException;

public class NetUtil {

	public final static byte LOCALHOST_IPV4[] = { 127, 0, 0, 1 };

	private final static byte IPV6_TO_IPV4_REQUIRED_PREFIX[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF };

	public static String convertIPv4ToIPv6(String ipv4) throws ParseException {
		
		byte ipv4octets[] = parseIPv4(ipv4);

		byte ipv6[] = convertIPv4ToIPv6(ipv4octets);

		return ipv6ToString(ipv6);
	}

	public static byte[] convertIPv4ToIPv6(byte ipv4[]) {

		checkIPv4ValidArgument(ipv4);

		byte ipv6[] = new byte[16];

		ipv6[10] = (byte) 0xff;
		ipv6[11] = (byte) 0xff;
		ipv6[12] = ipv4[0];
		ipv6[13] = ipv4[1];
		ipv6[14] = ipv4[2];
		ipv6[15] = ipv4[3];
	
		return ipv6;
	}

	public static boolean isIPv6ConvertibleToIPv4(byte[] ipv6) {

		checkIPv6ValidArgument(ipv6);

		int i;
		for (i = 0; i < 6; i++) {
			if (ipv6[i] != IPV6_TO_IPV4_REQUIRED_PREFIX[i]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] convertIPv6toIPv4(byte[] ipv6) {
		
		// don't need to 'checkIPv6ValidArgument' 
		// since 'isIPv6ConvertibleToIPv4' is a public 
		// method therefore will do the test for us
		
		if (!isIPv6ConvertibleToIPv4(ipv6)) {
			throw new IllegalArgumentException(ipv6ToString(ipv6) + " is not convertible into IPv4 Address");
		}
		
		byte ipv4[] = new byte[4];
		
		int i;
		for (i = 0; i < 4; i++) {
			ipv4[i] = ipv6[12 + i];
		}
		
		return ipv4;
	}

	public static String ipv4ToString(byte ipv4[]) {

		checkIPv4ValidArgument(ipv4);

		return ipv4[0] + "." + ipv4[1] + "." + ipv4[2] + "." + ipv4[3];
	}

	public static byte[] parseIPv4(String ip) throws ParseException {

		if (ip == null)
			return null;

		String octetsStr[] = ip.split(".");
		if (octetsStr.length != 4) {
			throw new ParseException(ip, 0);
		}

		byte octets[] = new byte[4];
		int i;
		for (i = 0; i < 4; i++) {
			try {
				octets[i] = Byte.valueOf(octetsStr[i]);
			} catch (NumberFormatException nfex) {
				throw new ParseException(ip, 0);
			}
		}

		return octets;
	}

	public static String ipv6ToString(byte ipv6[]) {

		checkIPv6ValidArgument(ipv6);

		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (byte n : ipv6) {
			if (first)
				first = false;
			else
				buffer.append(':');
			;
			buffer.append(String.format("%02X", n));
		}
		return buffer.toString();
	}

	public static byte[] parseIPv6(String ip) throws ParseException {

		if (ip == null)
			return null;

		String octetsStr[] = ip.split(":");
		if (octetsStr.length != 16) {
			throw new ParseException(ip, 0);
		}

		byte doctets[] = new byte[16];
		int i;
		for (i = 0; i < 16; i++) {
			if ("".equals(octetsStr[i])) {
				doctets[i] = 0;
			} else {
				try {
					//doctets[i] = Byte.decode("0x" + octetsStr[i]);
					doctets[i] = (byte) (Integer.parseInt("ff", 16) & 0xFF);
				} catch (NumberFormatException nfex) {
					throw new ParseException(ip, 0);
				}
			}
		}

		return doctets;
	}

	private static void checkIPv4ValidArgument(byte[] ipv4) {
		if (ipv4 != null && ipv4.length != 4)
			throw new IllegalArgumentException("IPv4 argument expected 4 octets ( byte[4] )");
	}

	private static void checkIPv6ValidArgument(byte[] ipv6) {
		if (ipv6 != null && ipv6.length != 16)
			throw new IllegalArgumentException("IPv6 argument expected 16 octets ( byte[16] )");
	}

}
