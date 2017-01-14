package net.tetrakoopa.mdu4j.util;


public class FormatterUtil {

	public static final String FILE_SIZE_FORMAT_EN = "%1$.3f %2$sB.";
	public static final String FILE_SIZE_FORMAT_FR = "%1$.3f %2$so.";

	/**
	 * Equivalent to <code>fileSizeAsString(size, FILE_SIZE_FORMAT_EN)</code>
	 */
	public static String fileSizeAsString(long size) {
		return fileSizeAsString(size, "%1$.3f %2$sb.");
	}

	/**
	 * @param format format used to display result, accept two place holders
	 *            <table>
	 *                <tr><th>Place Holder</th><th>Position</th></th><th>Type</th><th>example</th></tr>
	 *                <tr><td>size</td><td>1</td></td><td>Float or Double</td><td><code>%1$.1</code></td></tr>
	 *                <tr><td>dimension</td><td>2</td><td>String</td><td><code>%2$s</code></td></tr>
	 *            </table>
	 *
	 * example <code>fileSizeAsString(3000000, "%1$.3f %2$sb.")</code>
	 */
	public static String fileSizeAsString(long size, String format) {
		final double bytes = size;
		final double kilobytes = (bytes / 1024);
		if (kilobytes <= 1)
			return String.format(format, bytes, "");
		final double megabytes = (kilobytes / 1024);
		if (megabytes <= 1)
			return String.format(format, kilobytes, "k");
		final double gigabytes = (megabytes / 1024);
		if (gigabytes <= 1)
			return String.format(format, megabytes, "m");
		final double terabytes = (gigabytes / 1024);
		if (terabytes <= 1)
			return String.format(format, gigabytes, "g");
		final double petabytes = (terabytes / 1024);
		if (petabytes <= 1)
			return String.format(format, terabytes, "t");
		final double exabytes = (petabytes / 1024);
		if (exabytes <= 1)
			return String.format(format, petabytes, "p");
		final double zettabytes = (exabytes / 1024);
		if (zettabytes <= 1)
			return String.format(format, exabytes, "e");
		final double yottabytes = (zettabytes / 1024);
		if (yottabytes <= 1)
			return String.format(format, zettabytes, "z");
		return String.format(format, yottabytes, "y");
	}

}
