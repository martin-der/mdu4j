package net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties;


import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public enum PropertiesAction implements HtmlParameterEnum, ActionAttribute {

    @Label("${action.list-all.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"list")
    @Info(quickInfo = "${action.list-all.quick-info}")
    LIST,

    @Label("${action.view.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"eye")
    @Info(quickInfo = "${action.view.quick-info}")
    VIEW;

    private final String parameterName;

    PropertiesAction() {
        parameterName = name().toLowerCase();
    }
    
    public static PropertiesAction fromParameterName(String name) throws NoSuchEnumException {
        return Util.fromParameterName(PropertiesAction.class, name);
    }

    public static String[] buildActionsParameterName() {
        return Util.buildActionsParameterName(PropertiesAction.class);
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

}
