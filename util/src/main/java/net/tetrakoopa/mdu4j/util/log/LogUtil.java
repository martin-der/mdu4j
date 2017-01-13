package net.tetrakoopa.mdu4j.util.log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.tetrakoopa.mdu4j.util.IOUtil;

public class LogUtil {

	private final static String LOG4J_PROPERTIES = "/log4j.properties";

	public static String getFileNameForLog4JAppender(String appenderName) throws IOException {
		Properties log4jProperties = new Properties();

		try {
			log4jProperties.load(IOUtil.getInputStream(LOG4J_PROPERTIES));
		} catch (FileNotFoundException e) {
			return null;
		}

		final String key = "log4j.appender." + appenderName + ".File";
		// if (!log4jProperties.contains(key)) {
		// return null;
		// }
		final String filename = (String) log4jProperties.get(key);

		return filename;
	}

}
