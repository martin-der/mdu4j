package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractActionAdminServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CacheHtmlTemplate extends ActionAdminHtmlTemplate {

    private final ActionLink menuLinks[] = {
        new ActionLink(CacheAction.VIEW, servlet.message("View all")),
        new ActionLink(CacheAction.RESET, servlet.message("Reset all"))
    };

    public CacheHtmlTemplate(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super(servlet, request, response);
    }

    @Override
    public Renderer getActionMenuRenderer() {
        return new Renderer() {
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                renderMenusForActions(writer, menuLinks);
            }
        };
    }
}
