package net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache;

import net.tetrakoopa.mdu4j.front.servlet.bean.CommonRequestParameter;

public interface CacheRequestParameter extends CommonRequestParameter {

    String KEY_CACHE_NAME = "name";
    
    String KEY_CACHE_KEY_NAME = "key";

    String KEY_SHOW_CACHE_KEYS = "show-keys";
    String KEY_SHOW_CACHE_CONTENT = "show-content";
    String KEY_SHOW_CACHE_CONTENT_DEPTH = "show-content-depth";

}
