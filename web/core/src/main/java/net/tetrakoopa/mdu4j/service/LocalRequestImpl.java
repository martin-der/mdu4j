package net.tetrakoopa.mdu4j.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class LocalRequestImpl implements LocalRequest {

    @Autowired
    private HttpServletRequest request;

    private final String LANG_COOKIE_NAME = "lang";
    private final String LANG_PARAMETER_NAME = "lang";

    private String lang;

    @PostConstruct
    private void init() {
        initLang();
    }

    private void initLang() {
    	lang = "fr";
    	if (this != null) return;
        lang = request.getParameter(LANG_PARAMETER_NAME);
        if (lang != null) {
            return;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(LANG_COOKIE_NAME)) {
                lang = cookie.getValue();
                if (lang != null) {
                    if (lang.trim().equals("")) {
                        lang=null;
                    }
                }
                break;
            }
        }
        if (lang != null) {
            return;
        }
        lang = request.getLocale().getLanguage().toLowerCase();
        if (lang.equals("")) {
            lang = null;
        }
    }

    @Override
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        request.setAttribute(name, object);
    }

    @Override
    public String getLang() {
        return lang;
    }
}
