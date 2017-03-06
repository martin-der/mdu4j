package net.tetrakoopa.mdu4j.front.view.configuration;

import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public class TemplateConfiguration<CONTEXT> {

	public final HTMLRenderHelper.ObjectRenderer<CONTEXT> preResourcesIncludeRenderer;
	public final HTMLRenderHelper.ObjectRenderer<CONTEXT> postResourcesIncludeRenderer;

	public TemplateConfiguration(HTMLRenderHelper.ObjectRenderer<CONTEXT> preResourcesIncludeRenderer, HTMLRenderHelper.ObjectRenderer<CONTEXT> postResourcesIncludeRenderer) {

		this.preResourcesIncludeRenderer = preResourcesIncludeRenderer;
		this.postResourcesIncludeRenderer = postResourcesIncludeRenderer;
	}

}
