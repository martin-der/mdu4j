package net.tetrakoopa.mdu4j.admin.front.servlet.bean.log;


import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public enum LogAction implements HtmlParameterEnum, ActionAttribute {

    @Label("${action.list-all.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"list")
    @Info(quickInfo = "${action.list-all.quick-info}")
    LIST,

    @Label("${action.view.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"image")
    @Info(quickInfo = "${action.view.quick-info}", explanation = "${action.view.explanation}")
    VIEW,

    @Label("${action.download.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"download")
    @Info(quickInfo = "${action.download.quick-info}", explanation = "${action.download.explanation}")
    DOWNLOAD,

    @Label("${action.search.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"search")
    @Info(quickInfo = "${action.search.quick-info}", explanation = "${action.search.explanation}")
    SEARCH;


    private final String parameterName;

    LogAction() {
        parameterName = name().toLowerCase();
    }
    
    public static LogAction fromParameterName(String name) throws NoSuchEnumException {
        return Util.fromParameterName(LogAction.class, name);
    }

    public static String[] buildActionsParameterName() {
        return Util.buildActionsParameterName(LogAction.class);
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

}
