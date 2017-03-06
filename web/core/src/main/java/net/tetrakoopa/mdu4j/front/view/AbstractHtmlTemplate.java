package net.tetrakoopa.mdu4j.front.view;

import net.tetrakoopa.mdu4j.front.view.configuration.TemplateConfiguration;
import net.tetrakoopa.mdu4j.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractHtmlTemplate<CONTEXT> {
    
    protected final HttpServletResponse response;
    protected final HttpServletRequest request;

    protected final TemplateConfiguration<CONTEXT> configuration;
	protected final CONTEXT configurationParameter;


    public AbstractHtmlTemplate(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, null, null);
    }
    public AbstractHtmlTemplate(HttpServletRequest request, HttpServletResponse response, CONTEXT context, TemplateConfiguration templateConfiguration) {
        this.request= request;
        this.response = response;
        this.configuration = templateConfiguration;
        this.configurationParameter = context;
        if (response == null) {
            throw new NullPointerException("Parameter 'response' must not be null");
        }
    }
    
    public interface Renderer {
        void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException;
    }

	abstract protected void includeResources(PrintWriter writer);

	protected final void includeAllResources(PrintWriter writer) throws IOException {
		if (configuration != null && configuration.preResourcesIncludeRenderer != null)
			configuration.preResourcesIncludeRenderer.render(writer, configurationParameter);
		includeResources(writer);
		if (configuration != null && configuration.postResourcesIncludeRenderer != null)
			configuration.postResourcesIncludeRenderer.render(writer, configurationParameter);
	}



	protected final String escape(String string) {
        return escape(string, false);
    }
    // TODO à mettre ailleurs... mais où
    /**
     * @param alsoCr si <code>true</code> les retour chariot sont aussi convertis en leur equilvalent HTML
     * @return <ul><li>null si l'entrée est null</li><li>la chaine échappé en HTML ( à minima &amp;, &gt; et &lt; )</li></ul>
     */
    protected final String escape(String string, boolean alsoCr) {
        if (string==null) {
            return null;
        }
        final String escaped = StringUtil.escapeHtml(string);
        if (alsoCr) {
            return escaped.replace("\n", "<br/>");
        } else {
            return escaped;
        }
    }

}
