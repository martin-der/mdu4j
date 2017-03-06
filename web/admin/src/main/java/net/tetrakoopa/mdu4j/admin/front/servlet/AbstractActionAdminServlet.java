package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.front.servlet.bean.*;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheActionResponse;
import net.tetrakoopa.mdu4j.front.servlet.component.ParameterLinkedDynamicFormService;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.ActionAdminHtmlTemplate;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLActionRenderHelper;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.AdminHtmlTemplate;
import net.tetrakoopa.mdu4j.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Classe parente des servlets d'admin dont le point d'entré principal est une action.<br/>
 */
public abstract class AbstractActionAdminServlet<ACTION extends Enum<ACTION>, PARAMETER extends CommonUserParameter> extends AbstractAdminServlet {

    private final Class<ACTION> actionClass;
    private final Class<PARAMETER> parameterClass;

    protected AbstractActionAdminServlet() {
        final Class<?> types [] = GenericTypeResolver.resolveTypeArguments(this.getClass(), AbstractActionAdminServlet.class);
        actionClass = (Class<ACTION>)types[0];
        parameterClass = (Class<PARAMETER>)types[1];
    }

    @Autowired
    protected ParameterLinkedDynamicFormService dynamicFormService;

    private final static String ATTRIBUTE_KEY_ACTION_NAME="AbstractActionAdminServlet."+"actionName";
    private final static String ATTRIBUTE_KEY_ACTION="AbstractActionAdminServlet."+"action";
    private final static String ATTRIBUTE_KEY_USER_PARAMETER="AbstractActionAdminServlet."+"userParameter";

    public static class NoSuchAction extends Exception {
        public NoSuchAction(String actionName) { super("No such action '"+actionName+"'"); }
    }


    private String getActionNameInURL(HttpServletRequest request) {
        if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
            return null;
        }
        final String path = request.getPathInfo();
        final String prefix = "/";
        return path.substring(prefix.length());
    }

    public static class IllegalActionArgumentException extends IllegalArgumentException {

        final DynamicForm form;

        public IllegalActionArgumentException(String message, DynamicForm form) {
            super(message);
            this.form = form;
        }
    }

    protected abstract PARAMETER buildUserParamter(HttpServletRequest request);

    protected abstract AbstractHtmlTemplate.Renderer getMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response);

    protected abstract AbstractHtmlTemplate.Renderer getSpecificMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response);

    public final AbstractHtmlTemplate.Renderer allMenusRenderer = new AbstractHtmlTemplate.Renderer() {

        @Override
        public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
            getMenuRenderer(AbstractActionAdminServlet.this, request, response).render(writer, request, response);
            final AbstractHtmlTemplate.Renderer specificMenuRenderer = getSpecificMenuRenderer(AbstractActionAdminServlet.this, request, response);
            if (specificMenuRenderer!=null) {
                writer.append("<hr/>");
                specificMenuRenderer.render(writer, request, response);
            }
        }
    };

    protected String getActionName() {
        return (String)getRequestAttribute(ATTRIBUTE_KEY_ACTION_NAME);
    }
    protected ACTION getAction() throws NoSuchAction {
        final String actionName = getActionName();
        if (getActionName() == null) {
            return null;
        }
        final ACTION action = (ACTION)getRequestAttribute(ATTRIBUTE_KEY_ACTION);
        if (action == null) {
            throw new NoSuchAction(actionName);
        }
        return action;
    }
    protected ACTION getActionOrNull() {
        try {
            return getAction();
        } catch (NoSuchAction noSuchAction) {
            return null;
        }
    }
    protected PARAMETER getUserParameter() {
        return (PARAMETER)getRequestAttribute(ATTRIBUTE_KEY_USER_PARAMETER);
    }

    protected abstract void actionDoGet(PARAMETER parameter, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected final void adminDoGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            final PARAMETER userParameter = buildUserParamter(request);
            if (userParameter != null) {
                if (userParameter.getReponseContentType() == null) {
                    userParameter.setReponseContentType(ContentType.HTML);
                }
            }
            setRequestAttribute(ATTRIBUTE_KEY_USER_PARAMETER, userParameter);

            final String actionName = getActionNameInURL(request);
            setRequestAttribute(ATTRIBUTE_KEY_ACTION_NAME, actionName);
            if (actionName !=null) {
                try {
                    setRequestAttribute(ATTRIBUTE_KEY_ACTION, getAvailableActionFromName(actionClass, actionName));
                } catch (HtmlParameterEnum.NoSuchEnumException e) {
                    // On ne fait rien ( getAction jettera l'errue à l'appel
                }
            }
            actionDoGet(userParameter, request, response);
        } catch (final IllegalActionArgumentException actionEx) {
            renderFailure(request, response, ActionResponse.Status.Code.FAILURE_ACTION_BAD_PARAMETER, actionEx.getMessage(), new AbstractHtmlTemplate.Renderer() {

                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                    if (actionEx.form != null) {
                        printTechnicalInfo(writer, request, response, new AbstractHtmlTemplate.Renderer() {
                            @Override
                            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                                final String actionName = getActionNameInURL(request);
                                dynamicFormService.renderDynamicForm(writer, request.getRequestURI(), actionEx.form, actionName);
                            }
                        });
                    }
                }
            },HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected String getActionPath(ACTION action) {
        return getAdminPath()+"/"+HtmlParameterEnum.Util.asHtmlParameterEnum(action).getParameterName();
    }

    /**
     * Vérifie qu'une action est fournie<br/>
     * Si aucune action n'était explicitée dans l'URL, alors écrit une réponse d'erreur via la servlet
     *
     * @return <code>true</code> si il avait une action, <code>false</code> dans le cas contraire
     */
    protected boolean actionProvidedOrRespondNoActionProvided(HttpServletRequest request, HttpServletResponse response, CommonUserParameter userParameter) throws IOException {

        if (getActionName()!=null) {
            return true;
        }

        final String helpPreMessage = message("${error.no-action.prompt-avalaible-actions}");
        final String availablesActions[] = getAvailableActionNames(actionClass);

        if (userParameter.getReponseContentType() != ContentType.HTML) {

            final CacheActionResponse responseBean = new CacheActionResponse();
            responseBean.getStatus().setCodeAndMessage(CacheActionResponse.Status.Code.FAILURE_UNKNOWN_ACTION, helpPreMessage+ Arrays.toString(availablesActions));

            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);

            return false;
        }

        makeItHtmlResponse(response);

        new AdminHtmlTemplate(this, request, response) {
            @Override
            public Renderer getMenuRenderer() {
                return AbstractActionAdminServlet.this.getMenuRenderer(AbstractActionAdminServlet.this, request, response);
            }
        }.render(response.getWriter(), null, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                writer.println("<h1>");
                writer.println(escape(message("${error.no-action.title}")));
                writer.println("</h1>");
                writer.println(helpPreMessage);
                writer.println("<br/>");
                renderActionsList(writer);

            }
        });

        return false;
    }

    /**
     * Récupère l'action explicitée via l'URL<br/>
     * Si aucune action ne correspond, ecrit une réponse d'erreur via la servlet
     *
     * @return null si aucune action correspondait
     */
    protected ACTION getActionOrRespondNoSuchAction(HttpServletRequest request, HttpServletResponse response, CommonUserParameter userParameter) throws IOException {

        final String actionName = getActionNameInURL(request);

        try {
            return getAction();
        } catch (NoSuchAction noSuchAction) {
            final String helpMessage = message("${error.no-action.prompt-avalaible-actions}")+" : "+ Arrays.toString(getAvailableActionNames(actionClass));

            if (userParameter.getReponseContentType() != ContentType.HTML) {

                final CacheActionResponse responseBean = new CacheActionResponse();
                responseBean.getStatus().setCodeAndMessage(CacheActionResponse.Status.Code.FAILURE_NO_ACTION, helpMessage);

                writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);

                return null;
            }

            makeItHtmlResponse(response);

            new ActionAdminHtmlTemplate(this, request, response) {
                @Override
                public Renderer getActionMenuRenderer() {
                    return null;
                }
            }.render(response.getWriter(), null, new AbstractHtmlTemplate.Renderer() {

                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                    writer.println("<h1>Unknown action '"+ StringUtil.escapeHtml(actionName)+"'</h1>");
                    writer.println(helpMessage);
                    renderActionsList(writer);
                }
            });

            return null;
        }
    }

    protected void renderNotImplementedYet(HttpServletRequest request, HttpServletResponse response, ACTION action) {
        renderFailure(request, response, null, "Action '"+action.name()+"' Not Implemented Yet", HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    protected DynamicForm.FormEntry getFormEntry(ParameterDynamicForm form, String name) {
        return form.getParameterEntry(parameterClass, name);
    }

    protected final ParameterDynamicForm createPopulatedForm(PARAMETER parameter) {
        final ParameterDynamicForm form = new ParameterDynamicForm();
        dynamicFormService.populateForm(form, parameter);
        return form;
    }

    private ACTION[] getActions(Class<ACTION> actionParameterClass) {
        return HtmlParameterEnum.Util.getActions(actionParameterClass);
    }
    private String getParameterName(ACTION action) {
        return HtmlParameterEnum.Util.asHtmlParameterEnum(action).getParameterName();
    }
    private ACTION getAvailableActionFromName(Class<ACTION> actionParameterClass, String actionName) throws HtmlParameterEnum.NoSuchEnumException {
        return HtmlParameterEnum.Util.fromParameterName(actionParameterClass, actionName);
    }
    private String[] getAvailableActionNames(Class<ACTION> actionParameterClass) {
        return HtmlParameterEnum.Util.buildActionsParameterName(actionParameterClass);
    }

    private void renderActionsList(PrintWriter writer) throws IOException {
        boolean oddRow = true;
        writer.append("<table class='main table'>");
        writer.append("<tr><th>");
        writer.print(message("${domain.REST.verb}"));
        writer.append("</th><th>");
        writer.print(message("${domain.name}"));
        writer.append("</th><th>");
        writer.print(message("${domain.information}"));
        writer.append("</th></tr>");
        for(ACTION action : getActions(actionClass)) {
            final String rowCssClass = oddRow?"odd":"even";
            writer.println("<tr class='"+rowCssClass+"'>");

            writer.println("<td>");
            writer.println("<div class='action verb'>");
            renderActionAsHTMLAnchor(writer, action, HtmlParameterEnum.Util.asHtmlParameterEnum(action).getParameterName(), null, true, false);
            writer.println("</div>");
            writer.println("</td>");

            final Field actionField = getField(action);

            final ActionAttribute.Label label = actionField.getAnnotation(ActionAttribute.Label.class);
            writer.println("<td>");
            if (label != null) {
                writer.print(escape(message(label.value())));
            } else {
                writer.print(escape(action.name()));
            }
            writer.println("</td>");

            final ActionAttribute.Info info = actionField.getAnnotation(ActionAttribute.Info.class);
            writer.println("<td>");
            if (info != null ) {
                writer.print(escape(message(info.quickInfo())));
            }
            writer.println("</td>");

            writer.println("</tr>");

            oddRow = ! oddRow;
        }
        writer.println("</table>");
    }
    public final void renderActionAsHTMLAnchor(PrintWriter writer, ACTION action, String overridenLabel, String escapedParameters, boolean showInfos, boolean showIcon) throws IOException {
        final String parameterName = getParameterName(action);
        final Field actionField = getField(action);
        final ActionAttribute.Label label = actionField.getAnnotation(ActionAttribute.Label.class);

        String href = getAdminPath()+"/"+parameterName;
        if (escapedParameters != null) {
            href = href+"?"+escapedParameters;
        }

        final String finalLabel;
        if (overridenLabel!=null) {
            finalLabel = overridenLabel;
        } else {
            finalLabel = label!=null ? message(label.value()) : parameterName;
        }

        HTMLRenderHelper.Actionable.renderAnchor(writer, href, finalLabel, actionField, showInfos ?  extraActionInfoAndCriticalityRenderer : extraActionCriticalityRenderer, showIcon ? HTMLActionRenderHelper.fieldActionAttributeExtraRenderer : null, false);

    }

    private final ExtraActionCriticalityRenderer extraActionCriticalityRenderer = new ExtraActionCriticalityRenderer();
    private final ExtraActionInfoAndCriticalityRenderer extraActionInfoAndCriticalityRenderer = new ExtraActionInfoAndCriticalityRenderer();

    public class ExtraActionCriticalityRenderer implements HTMLRenderHelper.ObjectRenderer<Field> {

        @Override
        public void render(PrintWriter writer, Field object) throws IOException {
            final Field actionField = (Field)object;

            final ActionAttribute.Criticality criticality = actionField.getAnnotation(ActionAttribute.Criticality.class);

            if (criticality !=null) {
                writer.append(" aa:criticality='"+criticality.value().name()+"'");
            }

        }
    }
    public class ExtraActionInfoAndCriticalityRenderer extends ExtraActionCriticalityRenderer implements HTMLRenderHelper.ObjectRenderer<Field> {

        @Override
        public void render(PrintWriter writer, Field object) throws IOException {
            final Field actionField = (Field)object;

            final ActionAttribute.Info info = actionField.getAnnotation(ActionAttribute.Info.class);

            if (info !=null) {
                if (!info.quickInfo().trim().isEmpty()) {
                    writer.append(" title='" + HTMLRenderHelper.escapeXmlAttribute(message(info.quickInfo())) + "'");
                }
                if (!info.explanation().trim().isEmpty()) {
                    writer.append(" aa:explanation='" + HTMLRenderHelper.escapeXmlAttribute(message(info.explanation())) + "'");
                }
            }

            super.render(writer, object);
        }
    }

    private Field getField(ACTION action) {
        try {
            return actionClass.getField(action.name());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
