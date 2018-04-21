package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.front.servlet.bean.ContentType;
import net.tetrakoopa.mdu4j.front.servlet.bean.ParameterDynamicForm;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataRequestParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataUserParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.DataActionResponse;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterCloner;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterParser;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.DataHtmlTemplate;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.view.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Servlet permettant d'afficher des données arbitraires
 */
@UI.Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"database")
public class DataServlet extends AbstractActionAdminServlet<DataAction, DataUserParameter> implements DataRequestParameter {

    private final static Logger LOGGER = LoggerFactory.getLogger(DataServlet.class);
    
    @Autowired
    private ParameterParser parser;

    private String HTML_IMG_STAR_ON;
    private String HTML_IMG_STAR_OFF;

    @Override
    protected String getTechnicalName() {
        return "data";
    }

    @Override
    protected void adminInit(ServletConfig config) throws ServletException {

        HTML_IMG_STAR_ON = "<img src='"+getResourcesPath()+"/image/icon/star.png' class='icon'/>";
        HTML_IMG_STAR_OFF = "<img src='"+getResourcesPath()+"/image/icon/star.png' class='icon off'/>";
    }

    /**
     * Interface des classe fournissant un rendu HTML pour un type de donnée
     */
    public interface DataHandler {

        interface HtmlRenderer {
            void renderHead(DataServlet servlet, DataUserParameter parameter, PrintWriter writer, Object object) throws IOException;
            void renderBody(DataServlet servlet, DataUserParameter parameter, PrintWriter writer, Object object) throws IOException;
        }

        enum HandlingAbility {
            BEST_EVER(4),
            GOOD(3),
            MEDIUM(2),
            BAD(1),
            CANNOT(0);

            private final int level;

            HandlingAbility(int level) {
                this.level = level;
            }

        }

        /**
         * Détermine la capacité du 'handler' à afficher les différentes type de données qu'il connait<br/>
         */
        Map<String, HandlingAbility> getHandlingAbilities();

        Object retrieveData(String type, String id);
        byte[] retrieveSerializedData(String type, String id);

        /**
         * @return <code>null</code> if object can't be viewed in HTML
         */
        HtmlRenderer getHtmlRenderer();

        /**
         * Provide a Regex to see if a given text qualifies as an ID
         * @return <code>null</code> if no regex for spotting the id is available
         */
        String getSpotRegex();

    }

    @Override
    protected DataUserParameter buildUserParamter(HttpServletRequest request) {
        final DataUserParameter userParameter = new DataUserParameter();
        parser.parse(userParameter, request);
        return userParameter;
    }

    @Override
    protected AbstractHtmlTemplate.Renderer getMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return new DataHtmlTemplate(servlet, request, response).getMenuRenderer();
    }

    @Override
    protected AbstractHtmlTemplate.Renderer getSpecificMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public final void actionDoGet(DataUserParameter userParameter, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        parser.parse(userParameter, request);
        if (userParameter.getReponseContentType() == null) {
            userParameter.setReponseContentType(ContentType.HTML);
        }

        if (!actionProvidedOrRespondNoActionProvided(request, response, userParameter)) {
            return;
        }

        final DataAction action = getActionOrRespondNoSuchAction(request,response,userParameter);
        if (action == null) {
            return;
        }

        if (action == DataAction.VIEW) {
            renderViewAction(userParameter, request, response);
            return;
        }

        if (action == DataAction.LIST_HANDLERS) {
            renderListHandlersAction(userParameter, request, response);
            return;
        }

        renderNotImplementedYet(request, response, action);
    }

    private void renderListHandlersAction(final DataUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        LOGGER_ACTIVITY.info("List data handlers");

        final DataActionResponse responseBean = new DataActionResponse();

        responseBean.setAction(DataAction.LIST_HANDLERS);

        responseBean.getHandlers().addAll(enumerateHandlers());

        if (userParameter.getReponseContentType() != ContentType.HTML) {
            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
            return;
        }

        makeItHtmlResponse(response);

        //FIXME sortir le *new* template
        new DataHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
            }
        }, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                writer.append("<h1>");
                writer.append(escape(message("${handlers-list.title}")));
                writer.append("</h1>");
                if (responseBean.getHandlers().size()>0) {
                    renderHandlersTable(writer, responseBean.getHandlers(), request, userParameter);
                } else {
                    writer.append("<h2>");
                    writer.append(escape(message("${handlers-list.info.no-handler}")));
                    writer.append("</h2>");
                }
            }

        });
    }

    private void renderViewAction(final DataUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        checkArgumentForDataAccess(userParameter);

        LOGGER_ACTIVITY.info("View data '"+escape(userParameter.getDataId())+"' ( type : '"+escape(userParameter.getDataType())+"' )");

        final DataActionResponse responseBean = new DataActionResponse();

        responseBean.setAction(DataAction.VIEW);

        final DataActionResponse.Retrieval retrieval = new DataActionResponse.Retrieval();
        responseBean.setRetrieval(retrieval);

        final DataHandler handler = findBestHandler(userParameter.getDataType());

        if (handler==null) {

            retrieval.setStatus(DataActionResponse.Status.NO_AVAILABLE_HANDLER);

            final List<DataActionResponse.AvailableDataHandler> handlers = enumerateHandlers();
            if (handlers.size() > 0) {
                renderFailure(request, response, null, message("${data-view.error-no-handler}"), new AbstractHtmlTemplate.Renderer() {
                    @Override
                    public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                        renderHandlersTable(writer, handlers, request, userParameter);
                    }
                }, null);
            } else {
                renderFailure(request, response, null, message("${handlers-list.info.no-handler}"), null);
            }
            return;
        }

        final Object data;
        try {
            data = handler.retrieveData(userParameter.getDataType(), userParameter.getDataId());
        }catch (Exception ex) {
            renderFailure(request, response, null, "${error.data.failed-to-fetch}", ex, null);
            return;
        }

        if (data == null) {
            retrieval.setStatus(DataActionResponse.Status.NO_AVAILABLE_HANDLER);
            final String spotRegex = handler.getSpotRegex();
            final String errorMessage = message("${error.data.not-found.by-id}", userParameter.getDataId());
            if (spotRegex  == null) {
                renderFailure(request, response, null, errorMessage, HttpServletResponse.SC_NOT_FOUND);
            } else {
                renderFailure(request, response, null, errorMessage, new AbstractHtmlTemplate.Renderer() {
                    @Override
                    public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                        renderSpotRegexHint(writer, userParameter.getDataId(), spotRegex);
                    }
                }, HttpServletResponse.SC_NOT_FOUND);
            }
            return;
        }

        //retrieval.setObject(handler.retrieveData());
        retrieval.setStatus(DataActionResponse.Status.OK);

        if (userParameter.getReponseContentType() != ContentType.HTML) {
            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
            return;
        }

        makeItHtmlResponse(response);

        new DataHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {
            
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                handler.getHtmlRenderer().renderHead(DataServlet.this, userParameter, writer, data);
            }
        }, new AbstractHtmlTemplate.Renderer() {
            
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                handler.getHtmlRenderer().renderBody(DataServlet.this, userParameter, writer, data);
            }
            
        });
        
    }

    private void checkArgumentForDataAccess(DataUserParameter parameter) {
        if (parameter.getDataId() == null) {
            throw buildIllegalActionArgumentException("The id of the document (Parameter 'id') is expected", parameter);
        }
        if (parameter.getDataType() == null) {
            throw buildIllegalActionArgumentException("The type of the document (Parameter 'type') is expected", parameter);
        }
    }

    private void renderHandlersTable(PrintWriter writer, List<DataActionResponse.AvailableDataHandler> handlers, HttpServletRequest request, DataUserParameter parameters) throws IOException {
        boolean oddRow = true;
        writer.append("<table class='main table'>");
        writer.append("<tr><th>Type</th><th>Bean</th><th>Class</th><th>Ability</th></tr>");
        for (DataActionResponse.AvailableDataHandler handler : handlers) {
            final String rowCssClass = oddRow?"odd":"even";
            writer.append("<tr class='"+rowCssClass+"'>");
            writer.append("<td>");
            final ParameterCloner cloner = new ParameterCloner(parameters).change(DataRequestParameter.KEY_DATA_TYPE, handler.type);
            final String uri = getActionPath(DataAction.VIEW)+escapeXmlAttribute(parameterComposer.getComposition(cloner.cloneWithChanges()));
            HTMLRenderHelper.Actionable.renderActionableDiv(writer, uri, handler.type, null, null, null, false, "action humble");
            writer.append("</td>");
            writer.append("<td>");
            writer.write(escape(handler.springName));
            writer.append("</td>");
            writer.append("<td>");
            writer.write(escape(handler.handler.getClass().getName()));
            writer.append("</td>");
            writer.append("<td>");
            final int count = DataHandler.HandlingAbility.values().length-1;
            for (int i=0; i<count ; i++) {
                writer.append(handler.ability.ordinal()>i ? HTML_IMG_STAR_ON : HTML_IMG_STAR_OFF);
            }
            writer.append("</td>");
            writer.append("</tr>");
            oddRow = !oddRow;
        }
        writer.append("</table>");
    }

    private void renderSpotRegexHint(PrintWriter writer, String dataId, String regex) {
        final Pattern pattern = Pattern.compile(regex);
        final String regulaExpresionEplanationURL = "https://fr.wikipedia.org/wiki/Expression_rationnelle";
        boolean regexValid = true;
        boolean idMatchsRegex = false;
        try {
            idMatchsRegex = pattern.matcher(dataId).matches();
        } catch (PatternSyntaxException psex) {
            regexValid = false;
        }
        if (idMatchsRegex) {
            return;
        }
        writer.append("Hint : IDs for this kind of data <b>may</b> conform to this <a href='"+regulaExpresionEplanationURL+"'>regular expression</a> :<br/>" );

        writer.append("<span class='code'>");
        writer.append(escape(regex));
        writer.append("</span>");
        if (!regexValid) {
            writer.append("<br/><span class='text-error'>Regex is invalid</span>");
        }

    }

    private List<DataActionResponse.AvailableDataHandler> enumerateHandlers() {
        final List<DataActionResponse.AvailableDataHandler> availableHandlers = new ArrayList<DataActionResponse.AvailableDataHandler>();
        final Map<String, DataHandler> handlers = getAllhandlers();

        for (Map.Entry<String, DataHandler> handler : handlers.entrySet()) {
            final Map<String,DataHandler.HandlingAbility> abilities = handler.getValue().getHandlingAbilities();
            for (Map.Entry<String, DataHandler.HandlingAbility> ability : abilities.entrySet()) {
                availableHandlers.add(new DataActionResponse.AvailableDataHandler(ability.getKey(), handler.getKey(), handler.getValue(),  ability.getValue()));
            }
        }

        return availableHandlers;
    }
    private DataHandler findBestHandler(String dataType) {
        final Map<String, DataHandler> handlers = getAllhandlers();
        DataHandler handler = null;
        DataHandler.HandlingAbility ability = null;
        for (Map.Entry<String, DataHandler> entry : handlers.entrySet()) {
            final Map<String,DataHandler.HandlingAbility> abilities = entry.getValue().getHandlingAbilities();
            DataHandler.HandlingAbility thisHandlerAbility = abilities.containsKey(dataType) ? abilities.get(dataType) : null;
            if (thisHandlerAbility == null) {
                continue;
            }
            if (handler == null || thisHandlerAbility.level>ability.level) {
                handler = entry.getValue();
                ability = thisHandlerAbility;
            }
        }
        return handler;
    }

    private IllegalActionArgumentException buildIllegalActionArgumentException(String message, DataUserParameter parameter) {
        final ParameterDynamicForm form = createPopulatedForm(parameter);
        addExtraAttibute(parameter, form);
        return new IllegalActionArgumentException(message, form);
    }

    private void addExtraAttibute(DataUserParameter parameter, ParameterDynamicForm form) {
        if (parameter.getDataType() != null) {
            final DataHandler handler = findBestHandler(parameter.getDataType());
            if (handler != null ) {
                final String regex = handler.getSpotRegex();
                if (regex != null) {
                    getFormEntry(form, DataRequestParameter.KEY_DATA_ID).addAttribute(ParameterDynamicForm.UI_ATTRIBUTE_PREFIX+":regex", regex);
                }
            }
        }
    }

    private Map<String, DataHandler> getAllhandlers() {
        return getApplicationContext().getBeansOfType(DataHandler.class);
    }

}
