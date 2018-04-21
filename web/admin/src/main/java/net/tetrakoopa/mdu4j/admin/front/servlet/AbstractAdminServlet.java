package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.admin.front.servlet.view.configuration.AdminServletTemplateConfigurationFactory;
import net.tetrakoopa.mdu4j.admin.service.AdminUtilService;
import net.tetrakoopa.mdu4j.front.servlet.AbstractServlet;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.configuration.TemplateConfiguration;
import net.tetrakoopa.mdu4j.message.MessageService;
import net.tetrakoopa.mdu4j.service.LocalRequest;
import net.tetrakoopa.mdu4j.service.LocalSession;
import net.tetrakoopa.mdu4j.message.CommonMessageService;
import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.ContentType;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.GenericFailureActionResponse;
import net.tetrakoopa.mdu4j.front.servlet.component.ComponentMessageService;
import net.tetrakoopa.mdu4j.front.servlet.helper.ServletInitParameterHelper;
import net.tetrakoopa.mdu4j.front.servlet.helper.StaticResourceHelper;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterComposer;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterParser;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.AdminHtmlTemplate;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLElementRenderService;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.service.SerialisationService;
import net.tetrakoopa.mdu4j.util.ExceptionUtil;
import net.tetrakoopa.mdu4j.util.StringUtil;
import net.tetrakoopa.mdu4j.view.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.annotation.*;
import java.util.List;

/**
 * Classe parente des servlets d'admin.<br/><br/>
 * Les méthodes habituellement offertes par la classe HttpServlet sont cachées ( i.e. mise en 'final' ) et exposé aux enfant sous la forme <code>adminMethode</code>.<br/>
 * ex :<ul>
 * <li><code>adminInit(...)</code> à la place de <code>init(...)</code></li>  
 * <li><code>adminDoGet(...)</code> à la place de <code>doGet(...)</code></li>  
 * <li>...</li>
 * </ul>
 * The following servlet parameters are available :
 * <ul>
 * <li>static-resources-path : permet de surcherger l'URL ( ou le contexte ) dans lequel se trouvent les ressources statiques</li>
 * <li>static-resources-classpath-folder : répertoire dans lequel chercher les ressources</li>
 * <li>extra-applicationContext : permet d'ajouter un ou plusieurs configuration Spring dans lesquels sont déclarés les 'Handlers'</li>
 * <li>menu.order : position dans le menu</li>
 * </ul>
 * 
 * <hr/>
 * This class can do Spring injection using annotation. After the call of <code>adminInit</code> all service are created and injected.<br/>
 * <br/>
 */
public abstract class AbstractAdminServlet extends AbstractServlet {

    private static final long serialVersionUID = -3069389309195935223L;

    public static final String INIT_PARAMETER_MENU_ORDER = "menu.order";

    public static String TECHNICAL_ACCESSOR_CSS_CLASS = "technical-accessor";

    public static String ERROR_INFORMATIONAL_CSS_CLASS = "error-informational";

    protected final static Logger LOGGER = LoggerFactory.getLogger(AbstractAdminServlet.class) ;

    protected final Logger LOGGER_ACTIVITY;
    protected final static String LOGGER_CONSEQUENCE_PREFIX = "\\->";

    protected final static String UNKNOWN_PATH = "/???";

    private String applicationName;
    private String defaultPath;

    private String glyph;
    /** null if never hidden, false if hidden at start only, true if always hidden */
    private Boolean alwaysHidden;

    private final static String EXTRA_PATH_RESOURCES_PREFIX = "/static";
    private final static String CLASSPATH_RESOURCES_PREFIX = "net/tetrakoopa/mdu4j/admin/front/resource";

    private String staticResourcesBase;
    private String staticResourcesFolder;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    @interface Hidden {
        boolean always() default false;
    }

    protected Logger getActivityLogger() {
        return null;
    }

    @Autowired
    private AdminUtilService adminUtilService;

    @Autowired
    private SerialisationService serialisationService;

	@Autowired
    @Qualifier("defaultMessageService")
    private MessageService messageService;

    @Autowired
    protected HTMLElementRenderService htmlElementRenderService;

    @Autowired
    protected ParameterComposer parameterComposer;

    protected abstract String getTechnicalName();

    private final static String REDIRECT_URL = "/login.xhtml";

    private ApplicationContext webApplicationContext;
    private ApplicationContext applicationContext;

    @Autowired
    private LocalRequest localRequest;

    @Autowired
    private LocalSession localSession;

    @Autowired
    private ParameterParser parser;

	private TemplateConfiguration templateConfiguration;

	protected AbstractAdminServlet() {
        final Logger logger = getActivityLogger();
        this.LOGGER_ACTIVITY = logger == null ? LOGGER : logger;
    }

    @Override
    public final void init(ServletConfig config) throws ServletException {
        super.init(config);

		final AdminServletTemplateConfigurationFactory adminServletTemplateConfigurationFactory = new AdminServletTemplateConfigurationFactory();
		adminServletTemplateConfigurationFactory.setServletContext(config.getServletContext());
		this.templateConfiguration = adminServletTemplateConfigurationFactory.getConfiguration(this);


		webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());

		final String applicationContexts[] = getTrimmedInitParametersList("extra-applicationContext", ",");
		applicationContext = applicationContexts == null ? webApplicationContext : new ClassPathXmlApplicationContext(applicationContexts, webApplicationContext);

		final AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
		autowiredAnnotationBeanPostProcessor.processInjection(this);

		applicationName = config.getServletContext().getServletContextName();
		defaultPath = getDefaultPath(config);

		staticResourcesBase = getTrimmedInitParameter("static-resources-path", EXTRA_PATH_RESOURCES_PREFIX);
		staticResourcesFolder = getTrimmedInitParameter("static-resources-classpath-folder", CLASSPATH_RESOURCES_PREFIX);

		adminUtilService.register(this);

		final String technicalName = getTechnicalName();
		if (technicalName != null) {
			try {
				final MessageService messageService = new ComponentMessageService((CommonMessageService) this.messageService, technicalName + "/main");
				autowiredAnnotationBeanPostProcessor.processInjection(messageService);
				this.messageService = messageService;
			} catch (Exception exception) {
				LOGGER.warn("Failed to load some message properties : " + exception.getMessage(), exception);
			}
		}
		htmlElementRenderService.setMessageService(this.messageService);

		final UI.Glyph glyphAttribute = getAnnotation(UI.Glyph.class);
		this.glyph = glyphAttribute == null ? null : glyphAttribute.value();
		final Hidden hiddenAttribute = getAnnotation(Hidden.class);
		if (hiddenAttribute != null) {
			this.alwaysHidden = hiddenAttribute.always();
		}

		adminInit(config);

    }
    protected abstract void adminInit(ServletConfig config) throws ServletException;

    protected Object getRequestAttribute(String name) {
        return localRequest.getAttribute(name);
    }
    protected void setRequestAttribute(String name, Object object) {
        localRequest.setAttribute(name, object);
    }


    protected String getTrimmedInitParameter(String name) {
        return ServletInitParameterHelper.getTrimmedParameter(this, name);
    }
    protected String getTrimmedInitParameter(String name, String defaultz) {
        return ServletInitParameterHelper.getTrimmedParameter(this, name, defaultz);
    }
    protected String[] getTrimmedInitParametersList(String name, String separator ) {
        return ServletInitParameterHelper.getTrimmedParametersList(this, name, separator);
    }
    protected String[] getTrimmedInitParametersList(String name, String separator, String defaultz[] ) {
        return ServletInitParameterHelper.getTrimmedParametersList(this, name, separator, defaultz);
    }

    @Override
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        if (!isUserLogged()) {
            response.sendRedirect(REDIRECT_URL);
            return;
        }

        if (Boolean.FALSE.equals(alwaysHidden) ) {
            localSession.see(this);
        }

        if (request.getPathInfo() != null) {
            if (request.getPathInfo().equals("/static/admin-generated.js")) {
                serveGeneratedJS(request, response);
                return;
            }

            if (StaticResourceHelper.isStaticResourcePath(staticResourcesBase, request.getPathInfo())) {
                serveResource(request.getPathInfo(), request, response);
                return;
            }
        }

        final HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);

        try {
            adminDoGet(request, responseWrapper);
            //int size = responseWrapper.getBufferSize();
            //responseWrapper.setContentLength(size);
        } catch (Exception exception) {
            renderFailure(request, response, null, message("${error.unexpected.title}"), exception, 200);
        }

    }

    private boolean isUserLogged() {
        return true;
    }

    protected abstract void adminDoGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected final void renderBadParameterResponse(HttpServletRequest request, HttpServletResponse response, String message, Throwable exception) {
        renderFailure(request, response, null, message, exception, 422);
    }

    /**
     * Appelle <code>renderFailure(HttpServletRequest, HttpServletResponse, String, Throwable, Integer=<b>null</b>)</code>
     */
    protected final void renderFailure(HttpServletRequest request, HttpServletResponse response, final ActionResponse.Status.Code code, final String message) {
        renderFailure(request, response, code, message, (Integer) null);
    }

    protected final void renderFailure(HttpServletRequest request, HttpServletResponse response, ActionResponse.Status.Code code, final String message, final Throwable exception, Integer httpCode) {
        renderFailure(request, response, code, message, exception, null, httpCode);
    }

    protected final void renderFailure(HttpServletRequest request, HttpServletResponse response, ActionResponse.Status.Code code, final String message, final AbstractHtmlTemplate.Renderer customRenderer, Integer httpCode) {
        renderFailure(request, response, code, message, null, customRenderer, httpCode);
    }

    protected final void renderFailure(HttpServletRequest request, HttpServletResponse response, final ActionResponse.Status.Code code, final String message, Integer httpCode) {
        renderFailure(request, response, code, message, null, null, null);
    }

    /**
     * @param code peut-être null
     * @param message peut-être null
     * @param exception peut-être null
     * @param httpCode peut-être null, dans ce cas 'internal error' (500) est utilisé
     */
    private final void renderFailure(final HttpServletRequest request, final HttpServletResponse response, ActionResponse.Status.Code code, final String message, final Throwable exception, final AbstractHtmlTemplate.Renderer customRenderer, Integer httpCode) {
        
        final CommonUserParameter userParameter = new CommonUserParameter();
        
        parser.parse(userParameter, request);
        
        if (userParameter.getReponseContentType() == null) {
            userParameter.setReponseContentType(ContentType.HTML);
        }

        if (code == null) {
            code = ActionResponse.Status.Code.FAILURE_UNKOWN;
        }
        
        if (httpCode == null) {
            httpCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        LOGGER.error("", exception);
        
        try {
            response.setStatus(httpCode);
            
            if (userParameter.getReponseContentType() != ContentType.HTML) {
                String allMessage = message != null ? message : "";
                if (exception != null) {
                    if (message==null) {
                        allMessage = StringUtil.escapeXml(ExceptionUtil.getStacktrace(exception));
                    } else {
                        allMessage = message + "\n" + StringUtil.escapeHtml(ExceptionUtil.getStacktrace(exception), false);
                    }
                }
                final ActionResponse responseBean = new GenericFailureActionResponse(escape(allMessage));
                
                writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
                
                return;
            }

            response.setContentType("text/html");
            
            new AdminHtmlTemplate(this, request, response) {
                @Override
                public Renderer getMenuRenderer() {
                    return null;
                }
            }.render(response.getWriter(), null, new AbstractHtmlTemplate.Renderer() {

                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                    writer.print("<h1>");
                    writer.print(escape(message("${domain.error}"), false));
                    writer.println("</h1>");

                    if (message != null) {
                        if (exception == null) {
                            writer.println("<h2 class='" + TECHNICAL_ACCESSOR_CSS_CLASS + "'>" + escape(message) + "</h2>");
                        } else {
                            writer.println("<h2>" + escape(message) + "</h2>");
                        }
                    }

                    if (exception != null) {
                        final String quickInformation;
                        final String title;
                        if (exception != null) {
                            quickInformation = exception.getMessage();
                            title = exception.getClass().getName();
                        } else {
                            quickInformation = message("No available detail...");
                            title = null;
                        }
                        writer.print("<h3><span class=\"");
                        writer.print(TECHNICAL_ACCESSOR_CSS_CLASS);
                        writer.print(" ");
                        writer.print(ERROR_INFORMATIONAL_CSS_CLASS);
                        writer.print("\"");
                        if (title!=null) {
                            writer.print(" title=\"");
                            writer.print(escapeXmlAttribute(title));
                            writer.print("\"");
                        }
                        writer.print(">");
                        writer.print(escape(quickInformation));
                        writer.println("</span></h3>");

                        printTechnicalInfo(writer, request, response, new AbstractHtmlTemplate.Renderer() {

                            @Override
                            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                                writer.println("<span class='technical-code' />");
                                writer.print(escape(message("${domain.technical-information}")));
                                writer.println(" :<br/>");
                                writer.println("<span class='code'><pre>" + escape(ExceptionUtil.getStacktrace(exception), false) + "<pre></span>");
                                writer.println("</span");
                            }
                        } );

                        writer.println("<br/>");
                    } else if (customRenderer != null) {
                        try {
                            customRenderer.render(writer, request, response);
                        } catch(RuntimeException rex) {
                            // Fuck ! seriously ?
                            LOGGER.error("Oops, while render error : "+rex.getMessage(), rex);
                        }
                    }

                }
            });
            
        } catch (IOException ex) {
            // On ne peut vraiement plus rien faire dans ce cas à part logger...
            LOGGER.error(ex.getMessage(), ex);
        }
        
    }

    protected void writeNonHtmlResponse(ContentType responseContentType,  HttpServletResponse response, Object responseBean) throws IOException {
        
        if (responseContentType == ContentType.HTML) {
            throw new IllegalArgumentException("Cette methode est dédiée aux aux réponses qui ne sont pas en HTML");
        }

        if (responseContentType == ContentType.XML) {

            response.setContentType("application/xml");

            try {
                serialisationService.generateXml(responseBean, response.getWriter());
            } catch (JAXBException jaxbex) {
                throw new RuntimeException("Marshalling failed : " + jaxbex.getMessage(), jaxbex);
            }
        
        } else if (responseContentType == ContentType.JSON) {

            response.setContentType("application/json");

            try {
                serialisationService.generateJson(responseBean, response.getWriter());
            } catch (JAXBException jaxbex) {
                throw new RuntimeException("Marshalling failed : " + jaxbex.getMessage(), jaxbex);
            }
        } else {
            throw new IllegalArgumentException("This response type is not supported : "+responseContentType); 
        }
        
        
    }

    private void serveGeneratedJS(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType(StaticResourceHelper.getMimeType(this, "generated.js"));

        final String js = "AnyAdmin_staticResources_URL_prefix = \""+getResourcesPath()+"\";";
        final InputStream input = new ByteArrayInputStream(js.getBytes());

        int l;
        final byte buffer[] = new byte[1024];
        final OutputStream output = response.getOutputStream();
        while((l = input.read(buffer))>0) {
            output.write(buffer, 0, l);
        }

    }

    protected void serveResource(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String resourcePath = path;

        final InputStream input = StaticResourceHelper.getResourceInputStream(this.getClass().getClassLoader(),staticResourcesBase, request.getPathInfo(), staticResourcesFolder);

        if (input == null) {
            makeItHtmlResponse(response, 404);

            new AdminHtmlTemplate(this, request, response) {
                @Override
                public Renderer getMenuRenderer() {
                    return null;
                }
            }.render(response.getWriter(), null, new AbstractHtmlTemplate.Renderer() {

                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {
                    writer.print("<h1>");
                    writer.print("404 : ");
                    writer.print(escape(message("${domain.not-found}")));
                    writer.println("</h1><br/>");
                }
            });

            return;
        }

        response.setContentType(StaticResourceHelper.getMimeType(this, path));

        int l;
        final byte buffer[] = new byte[1024];
        final OutputStream output = response.getOutputStream();
        while((l = input.read(buffer))>0) {
            output.write(buffer, 0, l);
        }

    }

    protected void makeItHtmlResponse(HttpServletResponse response, int status) {
        makeItHtmlResponse(response);
        response.setStatus(status);
    }
    protected void makeItHtmlResponse(HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
    }

    public String getAdminPath() {
        final HttpServletRequest request = localRequest.getRequest();
        return getServletConfig().getServletContext().getContextPath()+request.getServletPath();
    }
    public String getResourcesPath() {
        return getServletContext().getContextPath()+defaultPath+staticResourcesBase;
    }
    public void includeJSFromResources(PrintWriter writer, String js) {
		HTMLRenderHelper.renderIncludeJS(writer, getResourcesPath()+"/js/"+js);
    }
    public void includeCSSFromResources(PrintWriter writer, String css) {
		HTMLRenderHelper.renderIncludeCSS(writer, getResourcesPath()+"/css/"+css);
    }

    protected void printTechnicalInfo(PrintWriter writer, HttpServletRequest request, HttpServletResponse response, AbstractHtmlTemplate.Renderer renderer) throws IOException {
        writer.println("<div class='technical-info' >");
        writer.println("<hr/>");
        renderer.render(writer, request, response);
        writer.println("</div>");
    }

    protected final String escapeXmlAttribute(String string) {
        if (string==null) {
            return null;
        }
        return StringUtil.escapeXmlAttribute(string);
    }

    private String getDefaultPath(ServletConfig config) {
        final List<String> urls = adminUtilService.findMappedPaths(config);
        String url = (urls != null && urls.size()>0) ? urls.get(0) : null;
        if (url == null) {
            return UNKNOWN_PATH;
        }
        url = removeSuffix(url, "/*");
        url = removeSuffix(url, "/**");
        return url;
    }

    private String removeSuffix(String url, String suffix) {
        if (url.endsWith(suffix)) {
            url = url.substring(0, url.length()-suffix.length());
        }
        return url;
    }

    protected ApplicationContext getWebApplicationContext() { return webApplicationContext; };
    protected ApplicationContext getApplicationContext() { return applicationContext; };

	public TemplateConfiguration getTemplateConfiguration() {
		return templateConfiguration;
	}


	public String getApplicationName() {
        return applicationName;
    }
    public List<AbstractAdminServlet> getAllAdminServlets() { return adminUtilService.getServlets(); }
    public String getDefaultPath() {
        return defaultPath;
    }
    public String getGlyph() {
        return glyph;
    }
    public Boolean getAlwaysHidden() {
        return alwaysHidden;
    }
    public boolean seen() {
        return localSession.seen(this);
    }

    public String message(String text) {
        return messageService.getMessage(text);
    }
    public String message(String text, Object...args) {
        return messageService.getMessage(text, args);
    }

    private <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        Class<?> clazz = this.getClass();
        do {
            final A annotation = clazz.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
            clazz = clazz.getSuperclass();
        } while(clazz != null);
        return null;
    }

}
