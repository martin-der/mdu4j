package net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache;


import net.tetrakoopa.mdu4j.front.servlet.bean.CommonUserParameter;
import net.tetrakoopa.mdu4j.front.servlet.bean.DynamicForm;

public class CacheUserParameter extends CommonUserParameter {

    @Attribute.Key(CacheRequestParameter.KEY_CACHE_NAME)
    @Attribute.EmptyIsNull
    @DynamicForm.Attribute.Label("Name")
    @DynamicForm.Attribute.Order(0)
    private String cacheName;

    @Attribute.Key(CacheRequestParameter.KEY_SHOW_CACHE_KEYS)
    @DynamicForm.Attribute.Label("Show keys")
    @DynamicForm.Attribute.Order(5)
    private Boolean showCacheKeys;

    @Attribute.Key(CacheRequestParameter.KEY_SHOW_CACHE_CONTENT)
    @DynamicForm.Attribute.Label("Show content")
    @DynamicForm.Attribute.Order(6)
    private Boolean showCacheContent;

    public Boolean getShowCacheKeys() {
        return showCacheKeys;
    }

    public void setShowCacheKeys(Boolean showCacheKeys) {
        this.showCacheKeys = showCacheKeys;
    }

    public Boolean getShowCacheContent() {
        return showCacheContent;
    }

    public void setShowCacheContent(Boolean showCacheContent) {
        this.showCacheContent = showCacheContent;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

}
