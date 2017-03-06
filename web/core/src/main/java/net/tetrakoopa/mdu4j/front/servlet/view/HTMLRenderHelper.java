package net.tetrakoopa.mdu4j.front.servlet.view;

import net.tetrakoopa.mdu4j.util.StringUtil;

import java.io.IOException;
import java.io.PrintWriter;

public class HTMLRenderHelper {

    public interface ObjectRenderer<CONTEXT> {
        void render(PrintWriter writer, CONTEXT context) throws IOException;
    };
    public static class ExtraRenderer<CONTEXT> {
        public ObjectRenderer<CONTEXT> beforeOut;
        public ObjectRenderer<CONTEXT> beforeIn;
        public ObjectRenderer<CONTEXT> afterIn;
        public ObjectRenderer<CONTEXT> afterOut;
    }

    public  static void renderIncludeJS(PrintWriter writer, String src) {
        writer.append("<script type=\"text/javascript\" src=\"");
        writer.append(src);
        writer.append("\"></script>\n");
    }
    public  static void renderIncludeCSS(PrintWriter writer, String href) {
        writer.append("<link href=\"");
        writer.append(href);
        writer.append("\" rel=\"stylesheet\" type=\"text/css\" />\n");
    }


    public static final class Actionable {

        private static final void renderLabel(PrintWriter writer, String label) throws IOException {
            renderLabel(writer, label, null, null, null, null, null);
        }
        private static final <CONTEXT> void renderLabel(PrintWriter writer, String label, CONTEXT context, ObjectRenderer<CONTEXT> beforeOutRenderer, ObjectRenderer<CONTEXT> beforeInRenderer, ObjectRenderer<CONTEXT> afterInRenderer, ObjectRenderer<CONTEXT> afterOutRenderer) throws IOException {
            if (beforeOutRenderer != null)
                beforeOutRenderer.render(writer, context);
            writer.write("<span>");
            if (beforeInRenderer != null)
                beforeInRenderer.render(writer, context);
            writer.write(escape(label, true));
            if (afterInRenderer != null)
                afterInRenderer.render(writer, context);
            writer.write("</span>");
            if (afterOutRenderer != null)
                afterOutRenderer.render(writer, context);
        }

        public static <CONTEXT> void renderAnchor(PrintWriter writer, String uri, String label, CONTEXT context, ObjectRenderer<CONTEXT> extraAttributesRenderer, ExtraRenderer<CONTEXT> extraLabelRenderer, boolean selected) throws IOException {
            writer.write("<a href='"+ StringUtil.escapeLiteralString(uri)+"'");
            if (selected) {
                writer.print(" class='selected'");
            }
            if (extraAttributesRenderer!=null) {
                extraAttributesRenderer.render(writer, context);
            }
            writer.write(">");
            if (extraLabelRenderer != null) {
                renderLabel(writer, label, context, extraLabelRenderer.beforeOut, extraLabelRenderer.beforeIn, extraLabelRenderer.afterIn, extraLabelRenderer.afterOut);
            } else {
                renderLabel(writer, label);
            }
            writer.write("</a>");
        }

        public static <CONTEXT> void renderActionableDiv(PrintWriter writer, String uri, String label, CONTEXT context, ObjectRenderer<CONTEXT> extraAttributesRender, ExtraRenderer<CONTEXT> extraLabelRenderer, boolean selected, String cssClasses) throws IOException {
            renderActionableElement(writer, "div", uri, label, context, extraAttributesRender, extraLabelRenderer, selected, cssClasses);
        }
        public static <CONTEXT> void renderActionableLi(PrintWriter writer, String uri, String label, CONTEXT context, ObjectRenderer<CONTEXT> extraAttributesRender, ExtraRenderer<CONTEXT> extraLabelRenderer, boolean selected, String cssClasses) throws IOException {
            renderActionableElement(writer, "li", uri, label, context, extraAttributesRender, extraLabelRenderer, selected, cssClasses);
        }
        private static <CONTEXT> void renderActionableElement(PrintWriter writer, String element, String uri, String label, CONTEXT context, ObjectRenderer<CONTEXT> extraAttributesRender, ExtraRenderer<CONTEXT> extraLabelRenderer, boolean selected, String cssClasses) throws IOException {
            writer.print("<");
            writer.print(element);
            if (cssClasses!=null || selected) {
                writer.print(" class='");
                if (cssClasses!=null) {
                    writer.print(cssClasses);
                }
                if (selected) {
                    writer.print(" selected");
                }
                writer.print("'");

            }
            writer.println(">");
            renderAnchor(writer, uri, label, context, extraAttributesRender, extraLabelRenderer, selected);
            writer.print("</");
            writer.print(element);
            writer.println(">");
        }
        public static void renderActionableLi(PrintWriter writer, String uri, String label, boolean selected, String cssClasses) throws IOException {
            renderActionableLi(writer, uri, label, null, null, null, selected, cssClasses);
        }
    }


    public static class Input {
        public static <E extends Enum<E>> void renderSelectForEnum(PrintWriter writer, String id, Class<E> choicesClass, ObjectRenderer<E> labelRender, ObjectRenderer<E> valueRender,  E selection )throws IOException {
            writer.print("<select id='"+id+"'>");
            //HtmlParameterEnum.Util.asHtmlParameterEnum(enuum);
            for (E choice : choicesClass.getEnumConstants()) {
                writer.print("<option value='");
                if (valueRender == null) {
                    writer.append(escapeXmlAttribute(choice.name()));
                } else {
                    valueRender.render(writer, choice);
                }
                writer.print("'");
                writer.append(choice == selection ? "selected>":">");
                if (labelRender == null) {
                    writer.append(choice.name());
                } else {
                    labelRender.render(writer, choice);
                }
                writer.print("</option>");
            }
            writer.print("</select>");
        }


    }

    public static class Glyph {
        public static final String FONT_STYLE_PREFIX = "font:";

        public static boolean renderGlyph(PrintWriter writer, String glyph) {
            if (glyph.startsWith(FONT_STYLE_PREFIX)) {
                final String charName = glyph.substring(FONT_STYLE_PREFIX.length());
                writer.println("<i class=\"fa fa-" + charName + "\" aria-hidden=\"false\"></i>");
                return true;
            }
            return false;
        }
    }

    public static String escape(String string, boolean alsoCr) {
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

    public static String escapeXmlAttribute(String string) {
        if (string==null) {
            return null;
        }
        return string.replace("\'", "&apos;").replace("\"","&quot;");
    }

}
