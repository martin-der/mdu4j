package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.front.servlet.bean.ContentType;
import net.tetrakoopa.mdu4j.front.servlet.bean.ParameterDynamicForm;
import net.tetrakoopa.mdu4j.front.servlet.bean.common.ResourceOrigin;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesReference;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesRequestParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesUserParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.PropertiesActionResponse;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterCloner;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterParser;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.PropertiesHtmlTemplate;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.view.UIAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Servlet permettant d'afficher des fichiers properties
 */
@UIAttribute.Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"columns")
public class PropertiesServlet extends AbstractActionAdminServlet<PropertiesAction, PropertiesUserParameter> implements PropertiesRequestParameter {

    private final static Logger LOGGER = LoggerFactory.getLogger(PropertiesServlet.class);
    
    @Autowired
    private ParameterParser parser;

    @Override
    protected String getTechnicalName() {
        return "properties";
    }

    private final List<PropertiesReference> propertiesResources = new ArrayList<PropertiesReference>();
    private boolean propertiesResourcesPopulated = false;

    private String propertiesRootClasspath;

    @Override
    protected void adminInit(ServletConfig config) throws ServletException {
        propertiesRootClasspath = "net/tetrakoopa";
    }

    private synchronized void listProperties() throws IOException {
        synchronized (propertiesResources) {
            if (!propertiesResourcesPopulated) {
                propertiesResourcesPopulated = true;
                final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

                for (Resource resource : resolver.getResources("classpath*:"+ propertiesRootClasspath+"/**/*.properties")) {
                    final PropertiesReference propertiesReference = new PropertiesReference();
                    try {
                        propertiesReference.setClasspathResourceReference(resource.getURI().toString());
                    } catch (Exception ex) {
                        propertiesReference.setFileSystemReference(resource.getFile().getAbsolutePath());
                    }
                    propertiesResources.add(propertiesReference);
                }
            }
        }
    }

    @Override
    protected PropertiesUserParameter buildUserParamter(HttpServletRequest request) {
        final PropertiesUserParameter userParameter = new PropertiesUserParameter();
        parser.parse(userParameter, request);
        return userParameter;
    }

    @Override
    protected AbstractHtmlTemplate.Renderer getMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return new PropertiesHtmlTemplate(servlet, request, response).getMenuRenderer();
    }

    @Override
    protected AbstractHtmlTemplate.Renderer getSpecificMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public final void actionDoGet(PropertiesUserParameter userParameter, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        parser.parse(userParameter, request);
        if (userParameter.getReponseContentType() == null) {
            userParameter.setReponseContentType(ContentType.HTML);
        }

        if (!actionProvidedOrRespondNoActionProvided(request, response, userParameter)) {
            return;
        }

        final PropertiesAction action = getActionOrRespondNoSuchAction(request,response,userParameter);
        if (action == null) {
            return;
        }

        if (action == PropertiesAction.VIEW) {
            renderDisplayAction(userParameter, request, response);
            return;
        }

        if (action == PropertiesAction.LIST) {
            renderListAction(userParameter, request, response);
            return;
        }

        renderNotImplementedYet(request, response, action);
    }

    private void renderListAction(final PropertiesUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        LOGGER_ACTIVITY.info("List properties");

        listProperties();

        final PropertiesActionResponse responseBean = new PropertiesActionResponse();

        responseBean.setAction(PropertiesAction.LIST);

        for (PropertiesReference reference : propertiesResources) {
            responseBean.getProperties().add(reference);
        }

        if (userParameter.getReponseContentType() != ContentType.HTML) {
            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
            return;
        }

        makeItHtmlResponse(response);

        new PropertiesHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
            }
        }, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                writer.append("<h1>");
                writer.append(escape(message("${propertiesFiles-list.title}")));
                writer.append("</h1>");
                if (responseBean.getProperties().size()>0) {
                    renderResourcesTable(writer, responseBean.getProperties(), request, userParameter);
                } else {
                    writer.append("<h2>");
                    writer.append(escape(message("${propertiesFiles-list.info.no-propertiesFiles}")));
                    writer.append("</h2>");
                }
            }

        });
    }

    private void renderDisplayAction(final PropertiesUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        checkArgumentForPropertiesDisplay(userParameter);

        if (userParameter.getResourcePath()!=null) {
            LOGGER_ACTIVITY.info("View resource properties '"+userParameter.getResourcePath()+"'");
        } else {
            LOGGER_ACTIVITY.info("View file properties '"+userParameter.getFilePath()+"'");
        }

        final Properties properties = new Properties();
        final String title;
        try {
            final InputStream inputStream;
            if (userParameter.getResourcePath() != null) {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(userParameter.getResourcePath());
                title = userParameter.getResourcePath();
                if (inputStream==null) throw new IOException("Resource '"+userParameter.getResourcePath()+"' not found");
            } else {
                inputStream = new FileInputStream(userParameter.getFilePath());
                title = userParameter.getFilePath();
            }
            properties.load(inputStream);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load resource : "+ex.getMessage(), ex);
        }

        makeItHtmlResponse(response);

        new PropertiesHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
            }
        }, new AbstractHtmlTemplate.Renderer() {
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                writer.append("<h2>");
                writer.append(escape(title));
                writer.append("</h2>");

                renderPropertiesTable(writer, properties, request);
            }
        });
    }

    private void checkArgumentForPropertiesDisplay(PropertiesUserParameter parameter) {
        if (parameter.getResourcePath() == null && parameter.getFilePath() == null) {
            throw buildIllegalActionArgumentException("The name of resource (Parameter '"+PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_CLASSPATH+"') or path of the file (Parameter '"+PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_FILE_SYSTEM+"') is expected", parameter);
        }
        if (parameter.getResourcePath() != null && parameter.getFilePath() != null) {
            throw buildIllegalActionArgumentException("Both name of resource (Parameter 'resource') or path of the file (Parameter 'file') cannot be given", parameter);
        }
    }

    private void renderResourcesTable(PrintWriter writer, List<PropertiesReference> properties, HttpServletRequest request, PropertiesUserParameter parameters) throws IOException {
        boolean oddRow = true;
        writer.append("<table class='main table'>");
        writer.append("<tr><th>Origin</th><th>Properties</th></tr>");
        for (PropertiesReference reference : properties) {
            final String rowCssClass = oddRow?"odd":"even";
            writer.append("<tr class='"+rowCssClass+"'>");
            writer.append("<td>");
            writer.append(htmlElementRenderService.getEnumLabel(reference.getOrigin()));
            writer.append("</td>");
            writer.append("<td>");
            final ParameterCloner cloner = new ParameterCloner(parameters);
            final String path;
            if (reference.getOrigin() == ResourceOrigin.CLASSPATH) {
                path = reference.getClasspathResourceReference();
                cloner.change(PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_CLASSPATH, path);
            } else {
                path = reference.getFileSystemReference();
                cloner.change(PropertiesRequestParameter.KEY_PROPERTIES_RESOURCE_FILE_SYSTEM, path);
            }
            final String uri = getActionPath(PropertiesAction.VIEW)+escapeXmlAttribute(parameterComposer.getComposition(cloner.cloneWithChanges()));
            HTMLRenderHelper.Actionable.renderActionableDiv(writer, uri, path, null, null, null, false, "action humble");
            writer.append("</td>");
            writer.append("</tr>");
            oddRow = !oddRow;
        }
        writer.append("</table>");
    }

    private void renderPropertiesTable(PrintWriter writer, Properties properties, HttpServletRequest request) throws IOException {
        boolean oddRow = true;
        writer.append("<table class='main table'>");
        writer.append("<tr><th>");
        writer.append(escape(message("${domain.key}")));
        writer.append("</th><th>");
        writer.append(escape(message("${domain.value}")));
        writer.append("</th></tr>");
        for (String key:  properties.stringPropertyNames()) {
            final String rowCssClass = oddRow?"odd":"even";
            writer.append("<tr class='"+rowCssClass+"'>");
            writer.append("<td>");
            writer.write(escape(key));
            writer.append("</td>");
            writer.append("<td>");
            writer.write(escape(properties.getProperty(key)));
            writer.append("</td>");
            writer.append("</tr>");
            oddRow = !oddRow;
        }
        writer.append("</table>");
    }

    private IllegalActionArgumentException buildIllegalActionArgumentException(String message, PropertiesUserParameter parameter) {
        final ParameterDynamicForm form = createPopulatedForm(parameter);
        addExtraAttibute(parameter, form);
        return new IllegalActionArgumentException(message, form);
    }

    private void addExtraAttibute(PropertiesUserParameter parameter, ParameterDynamicForm form) {
        if (parameter.getResourcePath() != null) {
        }
    }

}
