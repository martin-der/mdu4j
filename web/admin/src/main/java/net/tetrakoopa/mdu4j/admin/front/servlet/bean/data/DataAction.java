package net.tetrakoopa.mdu4j.admin.front.servlet.bean.data;


import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public enum DataAction implements HtmlParameterEnum, ActionAttribute {

    @Label("${action.view.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"eye")
    @Info(quickInfo = "${action.view.quick-info}", explanation = "${action.view.explanation}")
    VIEW,

    @Label("${action.list-handlers.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"list")
    @Info(quickInfo = "${action.list-handlers.quick-info}", explanation = "${action.list-handlers.explanation}")
    LIST_HANDLERS;

    private final String parameterName;

    DataAction() {
        parameterName = name().toLowerCase();
    }
    
    public static DataAction fromParameterName(String name) throws NoSuchEnumException {
        return Util.fromParameterName(DataAction.class, name);
    }

    public static String[] buildActionsParameterName() {
        return Util.buildActionsParameterName(DataAction.class);
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

}
