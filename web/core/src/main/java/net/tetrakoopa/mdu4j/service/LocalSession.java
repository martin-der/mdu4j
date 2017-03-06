package net.tetrakoopa.mdu4j.service;

import javax.servlet.http.HttpServlet;

public interface LocalSession {

    boolean seen(HttpServlet servlet);
    void see(HttpServlet servlet);

}
