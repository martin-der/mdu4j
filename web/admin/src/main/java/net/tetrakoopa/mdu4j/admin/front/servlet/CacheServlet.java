package net.tetrakoopa.mdu4j.admin.front.servlet;


import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheActionResponse;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.response.CacheResponse;
import net.tetrakoopa.mdu4j.front.servlet.parameter.ParameterParser;
import net.tetrakoopa.mdu4j.front.servlet.view.HTMLRenderHelper;
import net.tetrakoopa.mdu4j.front.view.AbstractHtmlTemplate;
import net.tetrakoopa.mdu4j.front.servlet.bean.ContentType;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheRequestParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheUserParameter;
import net.tetrakoopa.mdu4j.admin.front.servlet.view.CacheHtmlTemplate;
import net.tetrakoopa.mdu4j.util.StringUtil;
import net.tetrakoopa.mdu4j.view.UIAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@UIAttribute.Glyph(HTMLRenderHelper.Glyph.FONT_STYLE_PREFIX +"microchip")
public class CacheServlet extends AbstractActionAdminServlet<CacheAction, CacheUserParameter> implements CacheRequestParameter {
    
    private static final long serialVersionUID = -644755913609083952L;
    
    @Autowired
    private ParameterParser parser;
   
    @Autowired
    private CacheManager cacheManager;

    @Override
    protected String getTechnicalName() {
        return "cache";
    }

    @Override
    protected void adminInit(ServletConfig config) throws ServletException {
    }

    @Override
    protected CacheUserParameter buildUserParamter(HttpServletRequest request) {
        final CacheUserParameter userParameter = new CacheUserParameter();
        parser.parse(userParameter, request);
        return userParameter;
    }


    @Override
    protected AbstractHtmlTemplate.Renderer getMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return new CacheHtmlTemplate(servlet, request, response).getMenuRenderer();
    }
    @Override
    protected AbstractHtmlTemplate.Renderer getSpecificMenuRenderer(AbstractActionAdminServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void actionDoGet(CacheUserParameter userParameter, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (!actionProvidedOrRespondNoActionProvided(request, response, userParameter)) {
            return;
        }

        final CacheAction action = getActionOrRespondNoSuchAction(request,response,userParameter);
        if (action == null) {
            return;
        }

        if (action == CacheAction.VIEW) {
            renderViewAction(userParameter, request, response);
            return;
        }

        if (action == CacheAction.RESET) {
            renderResetAction(userParameter, request, response);
            return;
        }

        renderNotImplementedYet(request, response, action);
    }
    
    private void renderResetAction(CacheUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        
        final List<String> resetedCachesNames = new ArrayList<String>(); 
        
        if (userParameter.getCacheName() == null) {
            LOGGER_ACTIVITY.info("Reset all caches");
            for (Cache cache : getCaches()) {
                cache.clear();
                LOGGER_ACTIVITY.info(LOGGER_CONSEQUENCE_PREFIX+"Cache '"+cache.getName()+"' reseted");
                resetedCachesNames.add(cache.getName());
            }
        } else {
            LOGGER_ACTIVITY.info("Reset cache '"+userParameter.getCacheName()+"'");
            final Cache cache = getCache(userParameter.getCacheName());
            cache.clear();
            LOGGER_ACTIVITY.info(LOGGER_CONSEQUENCE_PREFIX+"Cache '"+cache.getName()+"' reseted");
            resetedCachesNames.add(cache.getName());
        }
        
        if (userParameter.getReponseContentType() != ContentType.HTML) {
            
            final CacheActionResponse responseBean = new CacheActionResponse();
            responseBean.setAction(CacheAction.RESET);
            for (String name : resetedCachesNames) {
                responseBean.getInvolvedCaches().add(new CacheResponse(name));
            }
            
            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
            
            return;
        }

        makeItHtmlResponse(response);

        new CacheHtmlTemplate(this, request, response).render(response.getWriter(), null, new AbstractHtmlTemplate.Renderer() {

            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {
                writer.println("The following cache(s) has/have been reseted :<br/>");
                writer.println("<ul>");
                for (String name : resetedCachesNames) {
                    writer.println("<li>");
                    writer.println(escape(name));
                    writer.println("</li>");
                }
                writer.println("</ul>");
            }

        });
    }

    private void renderViewAction(CacheUserParameter userParameter, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        
        final List<String> cachesNames = new ArrayList<String>();

        if (userParameter.getCacheName() == null) {
            for (Cache cache : getCaches()) {
                cachesNames.add(cache.getName()); //cache.
            }
        } else {
            cachesNames.add(userParameter.getCacheName()); //cache.
        }

        final boolean wantCacheContent = Boolean.TRUE.equals(userParameter.getShowCacheContent());
        final boolean wantCacheKeys = wantCacheContent || Boolean.TRUE.equals(userParameter.getShowCacheKeys());

        final CacheActionResponse responseBean = new CacheActionResponse();
        responseBean.setAction(CacheAction.VIEW);
        for (String name : cachesNames) {
            final CacheResponse cacheResponse = new CacheResponse(name);
            if (wantCacheKeys) {
                final Map<String,Object> content = cacheResponse.getContent();
                final Cache cache = getCache(name);
                final List<Object> keys = getCacheKeys(cache);
                for (Object key : keys) {
                    final String value;
                    if (wantCacheContent) {
                        final Object rawValue = cache.get(key);
                        value = rawValue == null ? null : StringUtil.stringifiedValue(rawValue);
                    } else {
                        value = "";
                    }
                    content.put(key.toString(), value);
                }
            }
            responseBean.getInvolvedCaches().add(cacheResponse);
        }


        if (userParameter.getReponseContentType() != ContentType.HTML) {
            
            writeNonHtmlResponse(userParameter.getReponseContentType(), response, responseBean);
            
            return;
        }

        makeItHtmlResponse(response);

        new CacheHtmlTemplate(this, request, response).render(response.getWriter(), new AbstractHtmlTemplate.Renderer() {
            
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) {
                includeCSSFromResources(writer, "cache.css");
            }
        }, new AbstractHtmlTemplate.Renderer() {
            
            @Override
            public void render(PrintWriter writer, HttpServletRequest request, HttpServletResponse response) throws IOException {
                
                final String prefix = request.getContextPath()+request.getServletPath();
                boolean firstCache = true;
                
                writer.println("<table class='main table'>");
                writer.println("<caption>"+"Existing cache(s)"+"</caption>");
                writer.println("<tr>");
                writer.println("<th colspan=\"2\">Label</th>");
                writer.println("<th colspan=\"2\">Actions</th>");
                writer.println("</tr>");
                boolean oddRow = true;
                for (CacheResponse cache : responseBean.getInvolvedCaches()) {

                    if (firstCache) {
                        firstCache = false;
                    } else {
                        writer.println("<tr ><td colspan=\"3\"><hr/></td></tr>");
                    }

                    writer.println("<tr>");
                    
                    writer.println("<td colspan=\"2\">");
                    writer.println(escape(cache.getName()));
                    writer.println("</td>");

                    writer.println("<td colspan=\"2\">");

                    writer.print("<span class=\"quick-action\">");
                    final String cacheNameParameter = KEY_CACHE_NAME+"="+escape(cache.getName());
                    renderActionAsHTMLAnchor(writer, CacheAction.VIEW, null, cacheNameParameter, true, false);
                    renderActionAsHTMLAnchor(writer, CacheAction.VIEW, "-with-keys", cacheNameParameter+"&"+KEY_SHOW_CACHE_KEYS, true, false);
                    renderActionAsHTMLAnchor(writer, CacheAction.VIEW, "-and-content", cacheNameParameter+"&"+KEY_SHOW_CACHE_CONTENT, true, false);
                    writer.println("</span>");

                    writer.print("<span class=\"quick-action\">");
                    renderActionAsHTMLAnchor(writer, CacheAction.RESET, null, KEY_CACHE_NAME+"="+escape(cache.getName()), true, false);
                    writer.println("</span>");

                    writer.println("</td>");
                    
                    writer.println("</tr>");

                    if (wantCacheKeys) {
                        writer.println("<tr>");
                        writer.println("<td style=\"width:30px;\"></td>");
                        writer.println("<td colspan=\"3\">");
                        writer.println("<table id=\"cache-content\" class='table'>");
                        for (String key : cache.getContent().keySet()) {
                            final String rowCssClass = oddRow?"odd":"even";
                            writer.println("<tr class=\""+rowCssClass+"\">");
                            writer.println("<td>");
                            writer.println(escape(key));
                            writer.println("</td>");
                            writer.println("<td>");
                            Object value = cache.getContent().get(key);
                            writer.println(value == null ? escape("<null>") : escape(value.toString()));
                            writer.println("</td>");
                            writer.println("</tr>");
                            oddRow = ! oddRow;
                        }
                        writer.println("</table>");
                        writer.println("</td>");
                        writer.println("</tr>");
                    }
                }
                writer.println("</table>");
            }
            
        });
        
    }

    private List<Object> getCacheKeys(Cache cache) {
        if (cache instanceof org.springframework.cache.ehcache.EhCacheCache) {
            final org.springframework.cache.ehcache.EhCacheCache typedCache = (org.springframework.cache.ehcache.EhCacheCache)cache;
            final net.sf.ehcache.Ehcache nativeCache = typedCache.getNativeCache();
            @SuppressWarnings("unchecked")
            final List<Object> keys = nativeCache.getKeys();
            return keys;
        }
        throw new IllegalArgumentException("Don't know how to handle cache of type '"+cache.getClass().getName()+"'");
    }
    
    private List<Cache> getCaches() {
        final List<Cache> caches = new ArrayList<Cache>();
        for (String name : cacheManager.getCacheNames()) {
            caches.add(cacheManager.getCache(name));
        }
        return caches;
    }
    private Cache getCache(String name) {
        for (String cacheName : cacheManager.getCacheNames()) {
            if (cacheName.equals(name)) {
                return cacheManager.getCache(name);
            }
        }
        throw new IllegalArgumentException("No such cache named '"+name+"'");
    }

}
