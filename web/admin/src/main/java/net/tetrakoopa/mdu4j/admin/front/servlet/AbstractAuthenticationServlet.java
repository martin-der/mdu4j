package net.tetrakoopa.mdu4j.admin.front.servlet;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheRequestParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.view.UI;

@UI.Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"user")
public abstract class AbstractAuthenticationServlet extends AbstractActionAdminServlet<CacheAction, CacheUserParameter> implements CacheRequestParameter {

    @Override
    protected String getTechnicalName() {
        return "authentication";
    }

}
