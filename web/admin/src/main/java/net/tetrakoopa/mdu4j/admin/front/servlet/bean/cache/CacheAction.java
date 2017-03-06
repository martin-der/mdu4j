package net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache;


import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.HtmlParameterEnum;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;

public enum CacheAction implements HtmlParameterEnum, ActionAttribute {

    @Label("${action.view.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"eye")
    @Info(quickInfo = "${action.view.explanation}", explanation = "${action.view.explanation}")
    VIEW,

    @Label("${action.reset.label}")
    @Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"eraser")
    @Criticality(Criticality.Level.WARNING)
    @Info(quickInfo = "${action.reset.explanation}", explanation = "${action.reset.explanation}")
    RESET;
    
    private final String parameterName;
    
    CacheAction() {
        parameterName = name().toLowerCase();
    }
    
    public static CacheAction fromParameterName(String name) throws NoSuchEnumException {
        return Util.fromParameterName(CacheAction.class, name);
    }

    public static String[] buildActionsParameterName() {
        return Util.buildActionsParameterName(CacheAction.class);
    }

    @Override
    public String getParameterName() {
        return parameterName;
    }

}
