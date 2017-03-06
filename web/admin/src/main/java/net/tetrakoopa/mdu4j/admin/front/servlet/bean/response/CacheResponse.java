package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.util.xml.adapter.MapAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name", "content"})
public class CacheResponse {

    public CacheResponse() {
    }
    public CacheResponse(String name) {
        this.name = name;
    }

    private String name;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, Object> content;

    public Map<String, Object> getContent() {
        if (content == null) {
            content= new HashMap<String, Object>();
        }
        return content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
