package net.tetrakoopa.mdu4j.service.message;

import net.tetrakoopa.mdu4j.message.CommonMessageService;
import net.tetrakoopa.mdu4j.service.LocalRequest;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class LangRequestAwareMessageService extends CommonMessageService {

    @Autowired
    private LocalRequest localRequest;

    public LangRequestAwareMessageService(CommonMessageService fallback, String name) {
        super(fallback, name);
    }

    public LangRequestAwareMessageService(String name) {
        super(name);
    }

    @Override
    protected String getLang() {
        return localRequest.getLang();
    }
}
