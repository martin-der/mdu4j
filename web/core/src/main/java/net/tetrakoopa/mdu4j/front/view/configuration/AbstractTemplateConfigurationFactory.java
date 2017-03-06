package net.tetrakoopa.mdu4j.front.view.configuration;

import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

public abstract class AbstractTemplateConfigurationFactory<CONTEXT> implements ServletContextAware {

	public static final String WEB_CONFIGURATION_TEMPLATE_CONFIGURATOR_CLASS = "mdu4j.HTML-template-configurator-class";

	private TemplateConfigurator configurator;

	private final static String ERROR_CONFIGURATION_PREFIX = "Failed to configure template : ";

	@Override
	public void setServletContext(ServletContext servletContext) {
		final String configuratorClass = servletContext.getInitParameter(WEB_CONFIGURATION_TEMPLATE_CONFIGURATOR_CLASS);
		if (configuratorClass!=null) {
			try {
				configurator = (TemplateConfigurator) Class.forName(configuratorClass).newInstance();
			} catch (RuntimeException e) {
				throw new IllegalArgumentException(ERROR_CONFIGURATION_PREFIX + e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(ERROR_CONFIGURATION_PREFIX + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(ERROR_CONFIGURATION_PREFIX + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(ERROR_CONFIGURATION_PREFIX + e.getMessage(), e);
			}
		}
	}

	public TemplateConfiguration getConfiguration(CONTEXT context) {

		final HTMLRenderHelper.ObjectRenderer preResourcesIncludeRenderer = configurator.getPreResourcesIncludeRenderer(context);
		final HTMLRenderHelper.ObjectRenderer postResourcesIncludeRenderer = configurator.getPostResourcesIncludeRenderer(context);

		return new TemplateConfiguration(preResourcesIncludeRenderer, postResourcesIncludeRenderer);
	}

}
