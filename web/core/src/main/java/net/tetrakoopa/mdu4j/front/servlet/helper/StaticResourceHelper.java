package net.tetrakoopa.mdu4j.front.servlet.helper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

public class StaticResourceHelper {

    public static String DEFAULT_BINARY_MIME_TYPE = "application/octet-stream";

    public static boolean isStaticResourcePath(String servletResourcePathPrefix, String path) {
        return path.startsWith(servletResourcePathPrefix+"/");
    }

    public static InputStream getResourceInputStream (ClassLoader loader, String servletResourcePathPrefix, String path, String classpathFolder ) {
        final String pathInFolder = path.substring(servletResourcePathPrefix.length()+1);
        return loader.getResourceAsStream(classpathFolder+"/"+pathInFolder);
    }

    public static String getMimeType ( HttpServlet servlet, String path ) {
        return getMimeType(servlet.getServletContext(), path);
    }
    public static String getMimeType ( ServletContext context, String path ) {
        final String mime = context.getMimeType(path);
        return mime != null ? mime : DEFAULT_BINARY_MIME_TYPE;
    }


}
