package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;


import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesAction;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties.PropertiesReference;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="propertiesAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertiesActionResponse extends ActionResponse {

    @XmlElement(name="name")
    private PropertiesAction action;

    @XmlElementWrapper(name="properties")
    @XmlElement(name="propertie")
    private List<PropertiesReference> properties;

    public PropertiesAction getAction() {
        return action;
    }

    public void setAction(PropertiesAction action) {
        this.action = action;
    }

    public List<PropertiesReference> getProperties() {
        if (properties == null) {
            properties = new ArrayList<PropertiesReference>();
        }
        return properties;
    }
}
