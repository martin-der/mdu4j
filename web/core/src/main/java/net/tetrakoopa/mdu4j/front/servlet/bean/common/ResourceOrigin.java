package net.tetrakoopa.mdu4j.front.servlet.bean.common;

import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;

public enum ResourceOrigin implements ActionAttribute {

        @Label("${domain.resource.origin.filesystem}")
        FILE_SYSTEM,
        @Label("${domain.resource.origin.classpath}")
        CLASSPATH;
}
