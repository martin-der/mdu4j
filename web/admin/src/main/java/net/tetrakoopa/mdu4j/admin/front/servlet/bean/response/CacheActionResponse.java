package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.cache.CacheAction;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="cacheAction", namespace= ActionResponse.XMLNS)
@XmlAccessorType(XmlAccessType.FIELD)
public class CacheActionResponse extends ActionResponse {

    @XmlElement(name="name")
    private CacheAction action;

    @XmlElementWrapper(name="caches")
    @XmlElement(name="cache")
    private List<CacheResponse> involvedCaches;

    public CacheAction getAction() {
        return action;
    }

    public void setAction(CacheAction action) {
        this.action = action;
    }

    public List<CacheResponse> getInvolvedCaches() {
        if (involvedCaches == null) {
            involvedCaches = new ArrayList<CacheResponse>();
        }
        return involvedCaches;
    }

    public void setInvolvedCaches(List<CacheResponse> involvedCaches) {
        this.involvedCaches = involvedCaches;
    }
    
}
