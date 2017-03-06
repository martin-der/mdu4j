package net.tetrakoopa.mdu4j.admin.front.servlet.view;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractActionAdminServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DataHtmlTemplate extends ActionAdminHtmlTemplate<DataAction> {

    private final static ActionLink menuLinks[] = {
        new ActionLink(DataAction.VIEW),
        new ActionLink(DataAction.LIST_HANDLERS)
    };

    public DataHtmlTemplate(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
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
