package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractActionAdminServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LogHtmlTemplate extends ActionAdminHtmlTemplate<LogAction> {

    private final static ActionAdminHtmlTemplate.ActionLink menuLinks[] = {
            new ActionLink(LogAction.LIST),
            new ActionLink(LogAction.VIEW),
            new ActionLink(LogAction.DOWNLOAD),
            new ActionLink(LogAction.SEARCH)
    };

    public LogHtmlTemplate(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
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
