package net.tetrakoopa.mdu4j.service;

import javax.servlet.http.HttpServletRequest;

public interface LocalRequest {

    HttpServletRequest getRequest();

    Object getAttribute(String name);
    void setAttribute(String name, Object object);

    String getLang();

}
