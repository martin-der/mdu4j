package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractActionAdminServlet;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class ActionAdminHtmlTemplate<ACTION extends Enum<ACTION>> extends AdminHtmlTemplate {

    protected final AbstractActionAdminServlet servlet;

    public abstract AbstractHtmlTemplate.Renderer getActionMenuRenderer();

    public ActionAdminHtmlTemplate(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super(servlet, request, response);
        this.servlet = servlet;
    }

    protected static class ActionLink<ACTION extends Enum<ACTION>> {
        final ACTION action;
        final String text;

        public ActionLink(ACTION action) {
            this(action, null);
        }
        public ActionLink(ACTION action, String text) {
            this.action = action;
            this.text = text;
        }
    }

    public AbstractHtmlTemplate.Renderer getMenuRenderer() {
        return getActionMenuRenderer();
    };

    protected void renderMenusForActions(PrintWriter writer, ActionLink links[]) throws IOException {
        final String prefix = request.getContextPath()+request.getServletPath();

        writer.println("<ul>");
        for (ActionLink link : links) {
            writer.print("<li class='action verb'>");
            servlet.renderActionAsHTMLAnchor(writer, link.action, link.text, null, true, true);
            writer.print("</li>");
       }
        writer.println("</ul>");

    }

    public void render(PrintWriter writer, AbstractHtmlTemplate.Renderer headRenderer, AbstractHtmlTemplate.Renderer bodyRenderer, AbstractHtmlTemplate.Renderer menuRenderer) throws IOException {

        final String requestUri = request.getRequestURI();

        writer.println("<html>");
        writer.println("<head>");

        includeAllResources(writer);

        if (headRenderer != null) {
            headRenderer.render(writer, request, response);
        }

        writer.println("</head>");
        writer.println("<body class='theme body'>");
        writer.println("<table class=\"main\">");

        writer.println("<tr>");
        writer.println("<td colspan=\"2\">");

        writeAdminHeader(writer);

        writer.println("</td>");
        writer.println("</tr>");

        writer.println("<tr>");
        writer.println("<td class=\"menu\"><div class=\"main-container\">");

        writeServletsList(writer);

        writer.println("<hr/>");

        if (menuRenderer == null) {
            menuRenderer = servlet.allMenusRenderer;
        }
        menuRenderer.render(writer, request, response);

        writer.println("</div></td>");
        writer.println("<td class=\"content\"><div class=\"main-container\">");
        if (bodyRenderer != null) {
            bodyRenderer.render(writer, request, response);
        }
        writer.println("</div></td>");
        writer.println("</tr>");

        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }

}
