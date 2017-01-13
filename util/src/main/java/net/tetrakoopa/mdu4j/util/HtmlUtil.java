package net.tetrakoopa.mdu4j.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

public class HtmlUtil {

	public final static String RESOURCE_PATH_JAVA_CSS = "/net/tetrakoopa/mdu4j/util/webresource/css/java.css";
	public final static String RESOURCE_PATH_LOG_CSS = "/net/tetrakoopa/mdu4j/util/webresource/css/log.css";

	/**
	 * génère une page HTML complète présentant une erreur
	 * 
	 * @param message
	 *            le message principale à afficher, doit être succint
	 * @param ex
	 *            l'erreur qui a été attrapée
	 * @param writer
	 *            où écrire
	 * @throws IOException
	 *             ...
	 */
	public static void printFailure(final String message, final Throwable ex, final PrintWriter writer) throws IOException {
		printFailure(message, ex.getMessage(), ex, writer);
	}

	/**
	 * génère une page HTML complète présentant une erreur
	 * 
	 * @param message
	 *            le message principal à afficher, doit être succint
	 * @param detail
	 *            un message <b>optionnel</b> explicant l'erreur plus en détail
	 * @param ex
	 *            l'erreur qui a été attrapée
	 * @param writer
	 *            où écrire
	 * @throws IOException
	 *             ...
	 */
	public static void printFailure(final String message, final String detail, final Throwable ex, final PrintWriter writer) throws IOException {

		writer.print("<html><head>");

		writer.print("<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.10.2.min.js\"></script>");

		writer.print("<style type=\"text/css\">");
		writer.print(IOUtil.readString(RESOURCE_PATH_JAVA_CSS));
		writer.print("</style>");
		writer.print("<style type=\"text/css\">");
		writer.print(getDefaultCommonCss());
		writer.print("</style>");
		writer.print("<style type=\"text/css\">");
		writer.print("div.technical-content { display:none; }");
		writer.print(".unselectable { cursor:default; -webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; }");
		writer.print("</style>");

		writer.print("<script style=\"text/javascript\">");
		writer.print("function showTechnicalContent() {");
		writer.print("jQuery(\"div.technical-content\").toggle(400,'swing');");
		writer.print("}");
		writer.print("</script>");

		writer.print("</head><body>");

		writer.print("<div class=\"error\">");
		if (message != null) {
			writer.print("<h2><span ondblclick=\"showTechnicalContent()\" class=\"unselectable\">" + escapeXml(message) + "</span></h2>");
		}
		if (detail != null) {
			writer.print("<div class=\"explanation\" >");
			writer.print(escapeXml(message).replace("\n", "<br/>"));
			writer.print("</div>");
		}
		writer.print("<div class=\"technical-content\">");
		final String errorMessage = readStacktrace(ex);

		writer.print(errorMessage.replace("\n", "<br/>"));
		writer.print("</div>");
		writer.print("</div>");

		writer.print("</body></html>");

	}

	public static final String readStacktrace(final Throwable t) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		// t.printStackTrace(pw);
		printExceptionAsHtml(t, pw);
		return sw.toString();
	}

	public static void printExceptionAsHtml(final Throwable x, final PrintWriter writer) {
		writer.append("<span class=\"javax-x_class\">");
		writer.append(x.getClass().getName());
		final String message = x.getMessage();
		if (message != null) {
			writer.append(": ");
			writer.append("<span class=\"javax-x_message\">");
			writer.append(message);
			writer.append("</span>");
		}
		writer.append("</span><br/>");
		for (final StackTraceElement element : x.getStackTrace()) {
			writer.append("<span style=\"margin-left:4em;\">at ");
			printExceptionStackTraceElementAsHtml(element, writer);
			writer.append("</span><br/>");
		}
		final Throwable cause = x.getCause();
		if (cause != null) {
			if (cause != x) {
				writer.append("Caused by: ");
				printExceptionAsHtml(cause, writer);
			}
		}
	}

	private static void printExceptionStackTraceElementAsHtml(final StackTraceElement element, final PrintWriter writer) {
		writer.append("<span class=\"javax-x_stackElement\">");
		writer.append("<span class=\"javax_package-class\">").append(element.getClassName()).append("</span>");
		writer.append('.');
		writer.append("<span class=\"javax-x_methode\">").append(element.getMethodName()).append("</span>");
		writer.append("</span>");

		writer.append("<span class=\"javax-x_codeLocation\">(");
		final String fileName = element.getFileName();
		if (fileName != null) {
			writer.append(fileName);
			writer.append(":");
			writer.append(String.valueOf(element.getLineNumber()));
		} else {
			writer.append("Unknown Source");
		}
		writer.append(")</span>");

		writer.append("</span>");
	}
	
	private static enum SeverityNameBinding {
		DEBUG("DEBUG", "javax-x_severity_debug"),
		INFO("INFO", "javax-x_severity_info"),
		WARN("WARN", "javax-x_severity_warn"),
		ERROR("ERROR", "javax-x_severity_error");

		private String inLog;
		private final String inCss;
		
		private SeverityNameBinding(String inLog, String inCss) {
			this.inCss = inCss;
			this.inLog = inLog;
		}

	}
	
	public static final String htmlizeLogLine(String line) {
		
		line = escapeXml(line);

		SeverityNameBinding lineBinding = null;

		for (SeverityNameBinding binding : SeverityNameBinding.values()) {
			if (line.startsWith(binding.inLog)) {
				lineBinding = binding;
				break;
			}
		}

		String markerClasses = "javax-x_logLine-marker";
		String lineClasses = "javax-x_logLine";

		if (lineBinding != null) {
			markerClasses = markerClasses + " " + lineBinding.inCss;
			lineClasses = lineClasses + " " + lineBinding.inCss;
			line = StringUtil.suroundBy("<span class=\"" + markerClasses + "\" >", line, "</span>", 0, lineBinding.inLog.length());
		}

		line = spanClass(lineClasses, line);
		return line;
	}

	private static String spanClass(String classes, String string) {
		return "<span class=\"" + classes + "\" >" + string + "</span>";
	}

	public static String readLogFile(final String log) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		// t.printStackTrace(pw);
		printLogAsHtml(log, pw);
		return sw.toString();
	}

	public static void printLogAsHtml(final String log, final PrintWriter writer) {
		final Scanner scanner = new Scanner(log);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			writer.write(htmlizeLogLine(line));
			writer.write("<br/>\n");
		}
		scanner.close();

	}

	private static String getDefaultCommonCss() {
		return "body {" + "margin:10px;" + "}\n"
		// --
			+ "div.error h2 {" + "text-align:center;" + "}\n"
			// --
			+ "\n";

	}


	private static String escapeXml(final String string) {
		return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
	}


}
