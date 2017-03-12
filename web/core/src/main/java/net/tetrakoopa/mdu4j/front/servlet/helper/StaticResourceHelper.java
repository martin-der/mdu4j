package net.tetrakoopa.mdu4j.front.servlet.helper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;

public class StaticResourceHelper {

    public static String DEFAULT_BINARY_MIME_TYPE = "application/octet-stream";


    public static boolean isStaticResourcePath(String servletResourcePathPrefix, String path) {
        return path.startsWith(servletResourcePathPrefix+"/");
    }

    public static boolean isStaticResourcePath(LinkedHashMap<String, String> servletResourcePaths, String path) {
        for (String servletResourcePathPrefix : servletResourcePaths.keySet()) {
            if (isStaticResourcePath(servletResourcePathPrefix, path))
                return true;
        }
        return false;
    }

     public static InputStream getResourceInputStream (ClassLoader loader, String servletResourcePathPrefix, String path, String classpathFolder ) {
        final String pathInFolder = path.substring(servletResourcePathPrefix.length()+1);
        return loader.getResourceAsStream(classpathFolder+"/"+pathInFolder);
    }

    public static InputStream getResourceInputStream (ClassLoader loader, LinkedHashMap<String, String> servletResourcePaths, String path) {
        for (Map.Entry<String, String> servletResourcePathPrefixesAndClasspath : servletResourcePaths.entrySet()) {
            final String servletResourcePathPrefix = servletResourcePathPrefixesAndClasspath.getKey();
            if (isStaticResourcePath(servletResourcePathPrefix, path))
                return getResourceInputStream(loader, servletResourcePathPrefix, path, servletResourcePathPrefixesAndClasspath.getValue());
        }
        throw new IllegalArgumentException("Path '"+path+"' did not match any resource path");
    }

    public static String getMimeType ( HttpServlet servlet, String path ) {
        return getMimeType(servlet.getServletContext(), path);
    }
    public static String getMimeType ( ServletContext context, String path ) {
        final String mime = context.getMimeType(path);
        return mime != null ? mime : DEFAULT_BINARY_MIME_TYPE;
    }


}
