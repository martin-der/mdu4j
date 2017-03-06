package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractActionAdminServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesAction;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PropertiesHtmlTemplate extends ActionAdminHtmlTemplate<PropertiesAction> {

    private final static ActionLink menuLinks[] = {
            new ActionLink(PropertiesAction.LIST),
            new ActionLink(PropertiesAction.VIEW)
    };

    public PropertiesHtmlTemplate(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super(servlet, request, response);
    }

    @Override
    public AbstractHtmlTemplate.Renderer getActionMenuRenderer() {
        return new AbstractHtmlTemplate.Renderer() {
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                renderMenusForActions(writer, menuLinks);
            }
        };
    }
}
