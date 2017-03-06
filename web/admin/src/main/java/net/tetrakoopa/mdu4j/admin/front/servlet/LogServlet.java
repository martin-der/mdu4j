package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.logger.LoggerRetrievalService;
import net.tetrakoopa.mdu4j.front.servlet.bean.ContentType;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.log.*;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.LogActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.LogResponse;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterParser;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLActionRenderHelper;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.LogHtmlTemplate;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.logger.bean.Logger;
import net.tetrakoopa.mdu4j.util.FormatterUtil;
import net.tetrakoopa.mdu4j.util.IOUtil;
import net.tetrakoopa.mdu4j.view.UIAttribute;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Servlet parameter :
 * <ul>
 *     <li>
 *         logger-types<br/>What kinds of logger ( Logback, JUL, ... ) should be retrieved<br/>
 *         See known loggers here : <code>LoggerRetrievalService.LoggerType</code>
 *     </li>
 * </ul>
 */
@UIAttribute.Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"newspaper-o")
public class LogServlet extends AbstractActionAdminServlet<LogAction, LogUserParameter> implements LogRequestParameter {

    @Autowired
    private ParameterParser parser;

    @Autowired
    private LoggerRetrievalService loggerRetrievalService;

    /**
     * Si zero, alors aucune restriction n'est appliquée<br/>
     * ( en octect )
     */
    private Long maximumSizeAllowedForViewing = 2l*1024*1024;

    private LoggerRetrievalService.LoggerType loggerTypes[] = LoggerRetrievalService.LoggerType.values();

    @Override
    protected String getTechnicalName() {
        return "log";
    }

    @Override
    protected void adminInit(ServletConfig config) throws ServletException {

        final String loggerTypesNames[] = getTrimmedInitParametersList("logger-types", ",");

        if (loggerTypesNames != null) {
            final List<LoggerRetrievalService.LoggerType> types = new ArrayList<LoggerRetrievalService.LoggerType>();
            for (String typeName : loggerTypesNames) {
                final LoggerRetrievalService.LoggerType type;
                try {
                    type = LoggerRetrievalService.LoggerType.fromName(typeName);
                } catch (IllegalArgumentException iaex) {
                    LOGGER.warn("logger-types : '"+typeName+"' is not a valid logger type");
                    continue;
                }
                types.add(type);
            }
            loggerTypes = types.toArray(new LoggerRetrievalService.LoggerType[types.size()]);
        }
    }

    @Override
    protected LogUserParameter buildUserParamter(HttpServletRequest request) {
        final LogUserParameter userParameter = new LogUserParameter();
        parser.parse(userParameter, request);
        return userParameter;
    }

    @Override
    protected AbstractHtmlTemplate.Renderer getMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return new LogHtmlTemplate(servlet, request, response).getMenuRenderer();
    }
    @Override
    protected AbstractHtmlTemplate.Renderer getSpecificMenuRenderer(final AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        final LogAction action = getActionOrNull();
        final LogUserParameter parameter = ((LogServlet)servlet).getUserParameter();
        if (action == LogAction.LIST) {
            return new AbstractHtmlTemplate.Renderer() {
                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                    writer.print("<label for='admin:log:retrieval-mode'>Log Files</label>");
                    HTMLActionRenderHelper.Input.renderSelectForParameter(writer, "admin:log:retrieval-mode", RetrievalMode.class, parameter.getMode());
                }
            };
        }
        if (action == LogAction.VIEW) {
            return new AbstractHtmlTemplate.Renderer() {
                @Override
                public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                    writer.print("<label for='admin:log:line-return'>Line return</label>");
                    writer.print("<input id='admin:log:line-return' type='checkbox' ");
                    writer.print("/>");
                }
            };
        }
        return null;
    }

    @Override
    protected void actionDoGet(LogUserParameter userParameter, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (userParameter.getMode() == null) {
            userParameter.setMode(RetrievalMode.FULL_SCAN);
        }

        if (!actionProvidedOrRespondNoActionProvided(request, response, userParameter)) {
            return;
        }

        final LogAction action = getActionOrRespondNoSuchAction(request,response,userParameter);
        if (action == null) {
            return;
        }

        if (action == LogAction.LIST) {
            renderListAction(userParameter, request, response);
            return;
        }

        if (action == LogAction.VIEW) {
            renderViewAction(userParameter, request, response);
            return;
        }

        if (action == LogAction.DOWNLOAD) {
            renderDownloadAction(userParameter, request, response);
            return;
        }

        renderNotImplementedYet(request, response, action);
    }


    private void renderListAction(final LogUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException  {

        final LogActionResponse responseBean = new LogActionResponse();

        populateAvailableLoggers(responseBean.getInvolvedLogs(), userParameter.getMode());

        responseBean.setAction(LogAction.VIEW);

        if (userParameter.getReponseContentType() != ContentType.HTML) {

            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);

            return;
        }

        makeItHtmlResponse(response);

        new LogHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {
                includeCSSFromResources(writer, "main.css");
                LogServlet.this.includeJSFromResources(writer, "component/log/list.js");
            }
        }, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {

                final String prefix = request.getContextPath()+request.getServletPath();

                writer.println("<table class='main table'>");
                writer.println("<caption>"+escape(message("${logs-table.caption}"))+"</caption>");
                writer.println("<tr>");
                writer.println("<th colspan=\"2\">"+escape(message("${logs-table.column.name}"))+"</th>");
                writer.println("<th colspan=\"2\">"+escape(message("${logs-table.column.actions}"))+"</th>");
                writer.println("<th >"+escape(message("${logs-table.column.size}"))+"</th>");
                writer.println("<th >"+escape(message("${logs-table.column.path}"))+"</th>");
                writer.println("</tr>");
                boolean oddRow = true;
                int unnamedIndex = 0;
                int index = 0;
                final String extraParamters = "mode="+userParameter.getMode().getParameterName();
                for (LogResponse log : responseBean.getInvolvedLogs()) {

                    final boolean validFile = log.getFile() != null;

                    if (!validFile) {
                        index++;
                        continue;
                    }

                    final String rowCssClass = oddRow?"odd":"even";

                    writer.println("<tr class='"+rowCssClass+"'>");

                    writer.println("<td colspan=\"2\">");
                    writer.println("<span title=\"From:"+log.getRetrievalMethod()+"\"");
                    if (log.isRolledInstance()) {
                        writer.println(" class=\"rolled-log\"");
                    }
                    writer.println(" >");
                    final String logName = log.getName();
                    writer.println((logName == null || logName.trim().equals("")) ? ("unnamed-"+(unnamedIndex++)) : escape(logName));
                    writer.println("</span>");
                    writer.println("</td>");

                    writer.println("<td colspan=\"2\">");
                    writer.print("<span class=\"quick-action\" >");
                    final String escapedViewLabel = escape(message("${action.view.label}"));
                    if (validFile) {
                        writer.print("<a href=\"" + prefix + "/" + LogAction.VIEW.getParameterName() + "?index=" + index + "&"+extraParamters+"\">"+escapedViewLabel+"</a>");
                    } else {
                        writer.print(escapedViewLabel);
                    }
                    writer.print("</span>");

                    writer.println("<span class=\"quick-action\">");
                    final String escapedDownloadwLabel = escape(message("${action.download.label}"));
                    if (validFile) {
                        writer.println("<a href=\"" + prefix + "/" + LogAction.DOWNLOAD.getParameterName() + "?index=" + index + "&"+extraParamters+"\">"+escapedDownloadwLabel+"</a>");
                    } else {
                        writer.print(escapedDownloadwLabel);
                    }
                    writer.print("</span>");
                    writer.println("</td>");

                    writer.println("<td style='text-align: right'>");
                    if (validFile) {
                        writer.println(FormatterUtil.fileSizeAsString(log.getFile().length(),FormatterUtil.FILE_SIZE_FORMAT_FR));
                    }
                    writer.println("</td>");

                    writer.println("<td>");
                    if (validFile) {
                        writer.println(log.getFile().getAbsolutePath());
                    }
                    writer.println("</td>");


                    writer.println("</tr>");

                    index++;
                    oddRow = ! oddRow;
                }
                writer.println("</table>");

            }

        });

    }

    private void renderDownloadAction(LogUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final LogResponse logger = getRequestedLogger(userParameter, true);

        LOGGER_ACTIVITY.info("Download log '{}' located in '{}'", logger.getName(), logger.getFile() == null ? "" : logger.getFile().getCanonicalPath());

        final File file = logger.getFile();
        final String filename = file == null ? "log.txt" : file.getName();;

        response.setContentType("application/force-download");
        response.setContentLength((int)logger.getFile().length());
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition","attachment; filename=\"" + filename+"\"");

        final InputStream in = new FileInputStream(logger.getFile());

        try {
			IOUtil.copy(in, response.getWriter());
        } finally {
            in.close();
        }
        response.getWriter().close();
    }
    private void renderViewAction(LogUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final LogResponse logger = getRequestedLogger(userParameter, true);

        LOGGER_ACTIVITY.info("View log '{}' located in '{}'", logger.getName(), logger.getFile() == null ? "" : logger.getFile().getCanonicalPath());

        if (maximumSizeAllowedForViewing > 0 && logger.getFile().length() > maximumSizeAllowedForViewing)  {
            final String size = FormatterUtil.fileSizeAsString(maximumSizeAllowedForViewing,FormatterUtil.FILE_SIZE_FORMAT_EN);
            LOGGER_ACTIVITY.info(LOGGER_CONSEQUENCE_PREFIX+"Denied : File bigger than "+size);
            throw new IllegalArgumentException(message("${error.view.file-too-big}", size));
        }

        makeItHtmlResponse(response);

        new LogHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {
                includeCSSFromResources(writer, "main.css");
                LogServlet.this.includeJSFromResources(writer, "component/log/view.js");
            }
        }, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response)  {

                writer.print("<h2><span>");
                writer.print(escape(logger.getFile().getName()));
                writer.println("<span></h2>");

                final BufferedReader reader;
                try {
                    reader = new BufferedReader(new FileReader(logger.getFile()));
                } catch (FileNotFoundException e) {
                    writer.print("<span class='error'>");
                    writer.print("File not found : "+logger.getFile().getAbsolutePath());
                    writer.print("<span>");
                    return;
                }
                writer.print("<pre class='log wrapped'>");
                try {

                    String line;

                    while ((line = reader.readLine()) != null) {
                        writer.println(escape(line));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    writer.print("</pre>");
                    try {
                        if (reader != null)
                            reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void renderSearchAction(LogUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final LogActionResponse responseBean = new LogActionResponse();

        populateAvailableLoggers(responseBean.getInvolvedLogs(), userParameter.getMode());

        responseBean.setAction(LogAction.VIEW);

        for (LogResponse log : responseBean.getInvolvedLogs()) {
            if (log.getFile() == null) {

            }
            final Scanner scanner = new Scanner(log.getFile());

            //if (scanner.findWithinHorizon())
            //findWithinHorizon


        }
    }


    private LogResponse getRequestedLogger(LogUserParameter userParameter, boolean assertHasAFile) {
        final List<String> loggerNames = new ArrayList<String>();

        final List<Logger> loggers = loggerRetrievalService.findDeclaredLoggers(loggerTypes);

        final Integer index = userParameter.getIndex();
        final String name = userParameter.getAppenderName();

        if (index == null && name == null) {
            throw new IllegalActionArgumentException("Parameter 'name' or 'index' expected", null);
        }

        final ArrayList<LogResponse> loggersAvailable = new ArrayList<LogResponse>();
        populateAvailableLoggers(loggersAvailable, userParameter.getMode());

        final LogResponse logger = loggersAvailable.get(index);

        if (logger.getFile() == null && assertHasAFile) {
            throw new IllegalArgumentException("Logger has no linked file");
        }

        return logger;
    }

    private void populateAvailableLoggers(List<LogResponse> loggersAvailable, RetrievalMode mode)  {

        final List<Logger> loggers = loggerRetrievalService.findDeclaredLoggers(loggerTypes);

        if (mode == RetrievalMode.APPENDERS ||mode == RetrievalMode.APPENDERS_AND_ROLLED ) {

            for (Logger logger : loggers) {
                final LogResponse logResponse = LogResponse.Factory.createFromLogger(logger);
                loggersAvailable.add(logResponse);
                if (logResponse.getFile() != null) {
                    if (mode == RetrievalMode.APPENDERS_AND_ROLLED) {
                        final List<File> rolledFiles = findRolledFiles(logger);
                        if (rolledFiles == null) {
                            continue;
                        }
                        for (File rolledFile : rolledFiles) {
                            loggersAvailable.add(LogResponse.Factory.createFromRolledFileLogger(logger, rolledFile));
                        }
                    }
                }
            }
        } else if  (mode == RetrievalMode.FULL_SCAN) {

            final List<String> foldersPaths = new ArrayList<String>();

            for (Logger logger : loggers) {
                if (logger.getFile() != null) {
                    final File parent = logger.getFile().getParentFile();
                    if (parent == null) {
                        continue;
                    }

                    boolean folderAlreadyScanned = false;
                    final String parentPath;
                    try {
                        parentPath = parent.getCanonicalPath();
                    } catch (IOException ioex) {
                        LOGGER.warn("Failed to compare files around : "+ ioex.getMessage(), ioex);
                        continue;
                    }
                    for (String folderPath : foldersPaths) {
                        if (parentPath.equals(folderPath)) {
                            folderAlreadyScanned = true;
                            break;
                        }
                    }

                    if (folderAlreadyScanned) {
                        continue;
                    }

                    foldersPaths.add(parentPath);
                    appendFilesfromFolder(loggersAvailable, parent, logger);
                }
            }

        }

    }

    private void appendFilesfromFolder(List<LogResponse> loggersAvailable, File folder, Logger originalLogger) {
        final File[] directoryListing = folder.listFiles();

        if (directoryListing == null) {
            // Le répertoire a probablement été effacé pendant qu'on itérait dessus
            return;
        }

        for (File child : directoryListing) {
            if (child.isDirectory()) {
                // TODO récursivité ?
            } else {
                loggersAvailable.add(LogResponse.Factory.createFromFileInFolder(originalLogger, child));
            }
        }
    }

    /**
     * @return peut retourner <code>null</code> si le répertoire n'existait plus au moment où on a essayé d'en le lister le contenu
     */
    private List<File> findRolledFiles(Logger logger) {
        if (logger.getFile() == null) {
            return null;
        }

        final File[] directoryListing = logger.getFile().getParentFile().listFiles();
        if (directoryListing == null) {
            // Le répertoire a probablement été effacé pendant qu'on itérait dessus
            return null;
        }

        final List<File> rolledFiles = new ArrayList<File>();
        for (File child : directoryListing) {
            if (child.equals(logger.getFile())) {
                continue;
            }
            try {
                if (logger.isRolledFile(child)) {
                    rolledFiles.add(child);
                }
            } catch (Exception e) {
                LOGGER.warn("Impossible de déterminer si le fichier '"+child.getAbsolutePath()+"' est une copie roulante du logger "+logger.getName(), e);
            }
        }

        return rolledFiles;
    }


}
