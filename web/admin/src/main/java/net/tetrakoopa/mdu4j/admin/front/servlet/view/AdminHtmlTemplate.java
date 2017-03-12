package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractAdminServlet;
import net.tetrakoopa.mdu4j.admin.service.AdminUtilService;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Les classe filles peuvent :
 *
 * <ul>
 *     <li>surcharger <code>renderMenu(PrintWriter writer, Renderer headRenderer)</code></li>
 * </ul>
 *
 */
public abstract class AdminHtmlTemplate extends AbstractHtmlTemplate {
    
    protected final AbstractAdminServlet servlet;

    public abstract AbstractHtmlTemplate.Renderer getMenuRenderer();

    public AdminHtmlTemplate(AbstractAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super(request, response, servlet, servlet.getTemplateConfiguration());
        this.servlet = servlet;
    }

    private static final HTMLRenderHelper.ExtraRenderer<AbstractAdminServlet> menuServletsExtraRenderer;
    static {
        menuServletsExtraRenderer = new HTMLRenderHelper.ExtraRenderer<AbstractAdminServlet>();
        menuServletsExtraRenderer.beforeIn = new HTMLRenderHelper.ObjectRenderer<AbstractAdminServlet>() {
            @Override
            public void render(PrintWriter writer, AbstractAdminServlet servlet) throws IOException {
                final String icon = servlet.getGlyph();
                if (icon != null && icon.startsWith(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX)) {
                    HTMLRenderHelper.Glyph.renderGlyph(writer, icon);
                }
            }
        };
    }

    public final void render(PrintWriter writer, Renderer headRenderer, Renderer bodyRenderer) throws IOException {
        render(writer, headRenderer, bodyRenderer, null);
    }
    public void render(PrintWriter writer, Renderer headRenderer, Renderer bodyRenderer, Renderer menuRenderer) throws IOException {
        
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
            menuRenderer = getMenuRenderer();
        }
        if (menuRenderer != null) {
            menuRenderer.render(writer, request, response);
        }

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

    protected void includeResources(PrintWriter writer) {

    	HTMLRenderHelper.renderIncludeJS(writer, servlet.getAdminPath()+"/static/admin-generated.js");

        servlet.includeJSFromResources(writer, "jquery-3.1.0.min.js");
        servlet.includeJSFromResources(writer, "any-admin.js");
        servlet.includeJSFromResources(writer, "jquery-util.js");
        servlet.includeJSFromResources(writer, "demo.js");
        servlet.includeJSFromResources(writer, "util.js");

        servlet.includeJSFromResources(writer, "jquery-ui/jquery-ui.min.js");

        servlet.includeCSSFromResources(writer, "any-admin.css");
        servlet.includeCSSFromResources(writer, "demo.css");

        servlet.includeCSSFromResources(writer, "jquery.growl.css");
        servlet.includeJSFromResources(writer, "jquery.growl.js");

        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster.bundle.min.css");
        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster-sideTip-borderless.min.css");
        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster-sideTip-light.min.css");
        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster-sideTip-noir.min.css");
        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster-sideTip-punk.min.css");
        servlet.includeCSSFromResources(writer, "tooltipster/tooltipster-sideTip-shadow.min.css");
        servlet.includeJSFromResources(writer, "tooltipster.bundle.min.js");
        servlet.includeCSSFromResources(writer, "font-awesome.min.css");


    }
    protected final void writeServletsList(PrintWriter writer) throws IOException {

        final String requestUri = request.getRequestURI();

        writer.println("<ul>");
        for (AbstractAdminServlet adminServlet : servlet.getAllAdminServlets()) {
            if (adminServlet.getAlwaysHidden() !=null) {
                final boolean alwaysHidden = adminServlet.getAlwaysHidden();
                if (alwaysHidden || !adminServlet.seen()) {
                    continue;
                }
            }
            final String servletPath = request.getContextPath()+adminServlet.getDefaultPath();
            String name = adminServlet.message("${admin.title}");
            if (name==null) {
                name = adminServlet.getServletName();
            }
            HTMLRenderHelper.Actionable.renderActionableLi(writer, servletPath, name,  adminServlet, null, menuServletsExtraRenderer, requestUri != null && requestUri.startsWith(servletPath), "action admin");
        }
        writer.println("</ul>");

    }


    protected final void writeAdminHeader(PrintWriter writer) {
        final String staticResources = servlet.getResourcesPath();

        writer.println("<header><table class=\"title header main-container\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<img src=\""+staticResources+"/image/structure-page/logo-50px.png"+"\" />");
        writer.println("</td>");
        writer.println("<td>");
        writer.println("<h1>"+escape(servlet.message("${header.title}", servlet.getApplicationName()))+"</h1>");

        writer.println("<div class='global-info'>");
        final String hostname = AdminUtilService.getHostname();
        if (hostname !=null ){
            final String ip = AdminUtilService.getIP();
            writer.println("<span title='"+escape(ip)+"'>"+escape(hostname)+"</span>");
        } else {
            writer.println("<span>");
            writer.println(escape(servlet.message("${error.host.unknown}")));
            writer.println("</span>");
        }
        writer.println("</div>");



        writer.println("</td>");
        writer.println("<td>");
        writer.println("<img src=\""+staticResources+"/image/structure-page/logo-50px.png"+"\" />");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table></header>");

    }

}
