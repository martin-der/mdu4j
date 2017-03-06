package net.tetrakoopa.mdu4j.front.servlet.view;

import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.util.EnumUtil;
import net.tetrakoopa.mdu4j.view.UIAttribute;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

public class HTMLActionRenderHelper extends HTMLRenderHelper {

    public static class Input {

        private final static class EnumValueRenderer<E extends Enum<E>> implements HTMLRenderHelper.ObjectRenderer<E> {
            @Override
            public void render(PrintWriter writer, E enuum) throws IOException {
                writer.append(HtmlParameterEnum.Util.asHtmlParameterEnum(enuum).getParameterName());
            }
        }
        private final static class EnumLabelRenderer<E extends Enum<E>> implements HTMLRenderHelper.ObjectRenderer<E> {
            @Override
            public void render(PrintWriter writer, E enuum) throws IOException {
                final Field field = EnumUtil.getValue((Class<E>)enuum.getClass(), enuum);
                writer.append(enuum.name());
            }
        }

        private final static class ActionAttributeExtraRenderer extends HTMLRenderHelper.ExtraRenderer<ActionAttribute> {

        }


        private final static EnumValueRenderer enumParameterValueRenderer = new EnumValueRenderer();
        private final static EnumLabelRenderer enumLabelRenderer = new EnumLabelRenderer();

        public static <E extends Enum<E>> void renderEnum(PrintWriter writer, E enuum) throws IOException {
            enumLabelRenderer.render(writer, enuum);
        }

        public static <E extends Enum<E>> void renderSelectForParameter(PrintWriter writer, String id, Class<E> choicesClass, E selection) throws IOException {
            HTMLRenderHelper.Input.renderSelectForEnum(writer, id, choicesClass, enumLabelRenderer, enumParameterValueRenderer, selection);
        }
    }

    private final static class FieldActionAttributeExtraRenderer extends HTMLRenderHelper.ExtraRenderer<Field> {
        public FieldActionAttributeExtraRenderer() {
            this.beforeIn = new ObjectRenderer<Field>() {

                @Override
                public void render(PrintWriter writer, Field field) throws IOException {
                    final UIAttribute.Glyph glyphAnnotation = field.getAnnotation(UIAttribute.Glyph.class);
                    if (glyphAnnotation == null) return;
                    HTMLRenderHelper.Glyph.renderGlyph(writer, glyphAnnotation.value());
                }
            };
        }
    }

    public final static FieldActionAttributeExtraRenderer fieldActionAttributeExtraRenderer = new FieldActionAttributeExtraRenderer();

}
