package net.tetrakoopa.mdu4j.admin.front.servlet.component.data;


import net.tetrakoopa.mdu4j.admin.front.servlet.DataServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataUserParameter;
import net.tetrakoopa.mdu4j.util.StringUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class DataHandlers {

    public static abstract class AbstractDataHandler implements DataServlet.DataHandler {
        @Override
        public byte[] retrieveSerializedData(String type, String id) {
            throw new RuntimeException("Not Implemented Yet");
        }
    }

    public static abstract class AbstractTextHandler extends AbstractDataHandler implements DataServlet.DataHandler {
    }

    public static class Renderer {

        public static abstract class AbstractHtmlRenderer implements DataServlet.DataHandler.HtmlRenderer {
            protected static String xmlEscaped(String string) {
                return string == null ? null : StringUtil.escapeXml(string);
            }
            protected static String getHTMLTitle(String title) {
                return getHTMLTitle(title, null);
            }
            protected static String getHTMLTitle(String title, String cssClasses) {
                if (cssClasses!=null) {
                    return "<h2 class='"+cssClasses+"'>"+xmlEscaped(title)+"</h2>";
                } else {
                    return "<h2>"+xmlEscaped(title)+"</h2>";
                }
            }

        }

        public static abstract class AbstractSimpleTextHtmlRenderer extends AbstractHtmlRenderer implements DataServlet.DataHandler.HtmlRenderer {
            public abstract void renderText(DataServlet servlet, PrintWriter writer, Object object) throws IOException;

            @Override
            public final void renderBody(DataServlet servlet, DataUserParameter parameter, PrintWriter writer, Object object) throws IOException {
                writer.append("<pre class='wrapped'>");
                try {
                    renderText(servlet, writer, object);
                } finally {
                    writer.append("</pre>");
                }
            }
        }

        /**
         * Render XML as a Tree<br/>
         * Override <ul>
         *     <li>renderExtraHead(DataServlet servlet, PrintWriter writer)</li>
         *     <li>renderXmlBodyHeader(DataServlet servlet, PrintWriter writer)</li>
         *     <li>renderXmlBodyFooter(DataServlet servlet, PrintWriter writer)</li>
         * </ul> for customizing the generated HTML
         */
        public static abstract class AbstractXmlHtmlRenderer extends AbstractHtmlRenderer implements DataServlet.DataHandler.HtmlRenderer {

            public static class Modifier {
                private final String xslt;

                private final String decorator;

                public Modifier(String xslt, String decorator) {
                    this.xslt = xslt;
                    this.decorator = decorator;
                }

                public String getXslt() {
                    return xslt;
                }
                public String getDecorator() {
                    return decorator;
                }
            }

            protected abstract String getXml(Object object);

            /**
             * @return title displayed at the top of the page<br/>
             * defaults to the ID of the data ( as provided as parameter )
             */
            protected String getTitle(DataUserParameter parameter, Object object) {
                return parameter.getDataId();
            }

            protected void renderExtraHead(DataServlet servlet, PrintWriter writer) { }

            protected void renderBodyHeader(DataServlet servlet, PrintWriter writer) { }
            protected void renderBodyFooter(DataServlet servlet, PrintWriter writer) { }

            public Map<String,Modifier> getModifiers() {
                return null;
            }


            /**
             * @return name of the javascript variable used as xml holder
             */
            protected String getXmlVarName() {
                return "dataHandler_xml";
            }

            @Override
            public final void renderHead(DataServlet servlet, DataUserParameter parameter, PrintWriter writer, Object object) throws IOException {
                servlet.includeJSFromResources(writer, "vakata-jstree/jstree.min.js");
                servlet.includeCSSFromResources(writer, "../js/vakata-jstree/themes/default/style.min.css");
                servlet.includeJSFromResources(writer, "jstree_helper.js");
                servlet.includeJSFromResources(writer, "jquery.xslt.js");
                servlet.includeJSFromResources(writer, "component/data/xml.js");
                renderExtraHead(servlet, writer);
            }

            @Override
            public final void renderBody(DataServlet servlet, DataUserParameter parameter, PrintWriter writer, Object object) throws IOException {

                final String title = getTitle(parameter, object);

                if (title != null) {
                    writer.append("<h2>"+getHTMLTitle(title, "hit-the-floor")+"</h2>\n");
                }

                final String xml = getXml(object);
                writer.append("<div class='tools'>");
                writer.append("<table>");
                writer.append("<tr>");
                writer.append("<td>");
                writer.append("Search <input id='dataSearch'/>");
                writer.append("</td>");
                writer.append("<td>");
                writeModifiers(writer);
                writer.append("</td>");
                writer.append("</tr>");
                writer.append("</table>");
                writer.append("</div>\n<br/>");

                writer.append("<div id=\"data-xmlTree\" />\n");
                writer.append("<script type=\"text/javascript\" >\n");
                writer.append("//<![CDATA[\n");
                writer.append("var "+getXmlVarName()+" = \""+xml.replace("\"","\\\"")+"\";\n");
                writer.append("jQuery(function(){ initxmlTree("+getXmlVarName()+"); });\n");
                writer.append("//]]>\n");
                writer.append("</script>\n");
            }

            public void writeModifiers(PrintWriter writer) {
                final Map<String, Modifier> modifiers = getModifiers();
                if (modifiers != null && modifiers.size()>0) {
                    writer.append("Clean <select id='dataTransform'>");
                    writer.append("<option value='none'>");
                    writer.append(xmlEscaped("none"));
                    writer.append("</option>");
                    int keyIndex = 0;
                    for (String xsltName : modifiers.keySet()) {
                        writer.append("<option value='xsl_"+(keyIndex++)+"'>");
                        writer.append(xmlEscaped(xsltName));
                        writer.append("</option>");
                    }
                    writer.append("</select>");
                    writer.append("<script type='text/javascript'>\n");
                    writer.append("//<![CDATA[\n");
                    writer.append("var "+getXmlVarName()+"_modifiers = [];\n");
                    for (String xsltName : modifiers.keySet()) {
                        final Modifier modifier = modifiers.get(xsltName);
                        final String xslt = modifier.xslt == null ? "null" : StringUtil.escapeLiteralString(modifier.xslt);
                        final String decorator = modifier.decorator == null ? "null" : modifier.decorator;
                        writer.append(getXmlVarName()+"_modifiers.push({xslt:\""+ xslt+"\", decorator:null});\n");
                    }
                    writer.append("\n\njQuery(\"select[id='dataTransform']\").on('change', function() {\n");
                    writer.append("if( this.value == 'none') {\n");
                    writer.append("xmlTreeSetInputAndDecoration("+getXmlVarName()+");\n");
                    writer.append("} else {\n");
                    writer.append("var index=0;\n");
                    writer.append("var modifier="+getXmlVarName()+"_modifiers[index];\n");
                    writer.append("xmlTreeSetTransformedInputAndDecoration("+getXmlVarName()+",modifier.xslt,modifier.decorator);\n");
                    writer.append("}\n");
                    writer.append("});\n");
                    writer.append("//]]>\n");
                    writer.append("</script>");
                }

            }

        }

        public static abstract class BlindToStringHtmlRenderer extends AbstractSimpleTextHtmlRenderer implements DataServlet.DataHandler.HtmlRenderer {

            @Override
            public final void renderText(DataServlet servlet, PrintWriter writer, Object object) throws IOException {
                writer.append(xmlEscaped(object.toString()));
            }
        }

    }

}
