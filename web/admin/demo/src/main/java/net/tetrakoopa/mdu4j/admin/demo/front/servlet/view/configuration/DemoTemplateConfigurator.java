package net.tetrakoopa.mdu4j.admin.demo.front.servlet.view.configuration;

import net.tetrakoopa.mdu4j.admin.front.servlet.AbstractAdminServlet;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.configuration.TemplateConfigurator;

import java.io.IOException;
import java.io.PrintWriter;

public class DemoTemplateConfigurator implements TemplateConfigurator<AbstractAdminServlet> {

	@Override
	public HTMLRenderHelper.ObjectRenderer<AbstractAdminServlet> getPreResourcesIncludeRenderer(AbstractAdminServlet servlet) {
		return null;
	}

	@Override
	public HTMLRenderHelper.ObjectRenderer<AbstractAdminServlet> getPostResourcesIncludeRenderer(AbstractAdminServlet servlet) {
		return new HTMLRenderHelper.ObjectRenderer<AbstractAdminServlet>() {
			@Override
			public void render(PrintWriter writer, AbstractAdminServlet servlet) throws IOException {
				servlet.includeCSSFromResources(writer,"demo/main.css");
				HTMLRenderHelper.renderIncludeJS(writer, "https://martin-der.github.io/-/js/js.cookie.js");
				HTMLRenderHelper.renderIncludeJS(writer, "https://martin-der.github.io/-/js/absurd.min.js");
				HTMLRenderHelper.renderIncludeJS(writer, "https://martin-der.github.io/-/js/mdu.js");
				HTMLRenderHelper.renderIncludeJS(writer, "https://martin-der.github.io/-/js/theme.js");
				servlet.includeJSFromResources(writer,"demo/main.js");
			}
		};
	}
}
