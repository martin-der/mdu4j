package net.tetrakoopa.mdu4j.front.view.configuration;

import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public interface TemplateConfigurator<CONTEXT> {

	HTMLRenderHelper.ObjectRenderer<CONTEXT> getPreResourcesIncludeRenderer(CONTEXT context);

	HTMLRenderHelper.ObjectRenderer<CONTEXT> getPostResourcesIncludeRenderer(CONTEXT context);

}
