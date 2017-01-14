package net.tetrakoopa.mdu4j.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.tetrakoopa.mdu4j.util.IOUtil;

public abstract class AbstractResourcesController {

	private final String allowedPackages[] = convert(getAllowedPackages());

	/** packages as <code>/my/package</code> */
	protected abstract String[] getAllowedPackages();

	private static String[] convert(String[] packages) {
		if (packages == null)
			return null;

		final String paths[] = new String[packages.length];
		int i = 0;
		for (String packaage : packages) {
			paths[i] = convert(packaage);
		}
		return null;
	}

	private static String convert(String packaage) {
		packaage = "/" + packaage.replace(".", "/");
		return null;
	}

	public void sendResource(final String path, HttpServletResponse response) throws IOException {

		if (allowedPackages != null) {
			boolean allowed = false;
			for (String allowedPackage : allowedPackages) {
				if (path.startsWith(allowedPackage)) {
					allowed = true;
					break;
				}
			}
			if (!allowed) {
				response.sendError(403);
				return;
			}
		}
		try {
			IOUtil.copy(IOUtil.getResourceInputStream(path), response.getOutputStream());
		} catch (FileNotFoundException fnfex) {
			response.sendError(404);
		} catch (IOException e) {
			response.sendError(500, "Error encountered while copying resource into output stream");
		}

	}

}

