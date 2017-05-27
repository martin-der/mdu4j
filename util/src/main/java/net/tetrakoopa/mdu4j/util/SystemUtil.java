package net.tetrakoopa.mdu4j.util;

import java.io.File;
import java.util.regex.Pattern;

public class SystemUtil {

	public static File findInPath(String executable) {
		final String[] paths = System.getenv("PATH").split(Pattern.quote(File.pathSeparator));
		boolean exists = false;
		for (String path : paths) {
			File file = new File(path,executable);
			if (file.exists()) {
				return file;
			}
		}
		return null;
	}

}
