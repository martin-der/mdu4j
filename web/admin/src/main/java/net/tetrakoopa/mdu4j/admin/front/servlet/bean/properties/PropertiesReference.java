package net.tetrakoopa.mdu4j.admin.front.servlet.bean.properties;

import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.front.servlet.bean.common.ResourceOrigin;

public class PropertiesReference implements ActionAttribute {

    private String reference;

    private ResourceOrigin origin;

    public ResourceOrigin getOrigin() {
        return origin;
    }

    public String getFileSystemReference() {
        return origin == ResourceOrigin.FILE_SYSTEM ? reference : null;
    }

    public void setFileSystemReference(String reference) {
        this.reference = reference;
        this.origin = ResourceOrigin.FILE_SYSTEM;
    }
    public String getClasspathResourceReference() {
        return origin == ResourceOrigin.CLASSPATH ? reference : null;
    }

    public void setClasspathResourceReference(String reference) {
        this.reference = reference;
        this.origin = ResourceOrigin.CLASSPATH;
    }
}
