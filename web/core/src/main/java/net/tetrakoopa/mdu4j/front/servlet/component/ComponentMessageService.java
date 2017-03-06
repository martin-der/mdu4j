package net.tetrakoopa.mdu4j.front.servlet.component;

import net.tetrakoopa.mdu4j.message.CommonMessageService;
import net.tetrakoopa.mdu4j.service.message.LangRequestAwareMessageService;

public class ComponentMessageService extends LangRequestAwareMessageService {

    public ComponentMessageService(CommonMessageService fallback, String name) {
        super(fallback, name);
    }
}
