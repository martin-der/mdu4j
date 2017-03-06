package net.tetrakoopa.mdu4j.service;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServlet;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class LocalSessionImpl implements LocalSession {

    private final Set<HttpServlet> servletSeens = new HashSet<HttpServlet>();

    @Override
    public boolean seen(HttpServlet servlet) {
        synchronized (LocalSessionImpl.this) {
            return servletSeens.contains(servlet);
        }
    }

    @Override
    public void see(HttpServlet servlet) {
        synchronized (LocalSessionImpl.this) {
            servletSeens.add(servlet);
        }
    }
}
