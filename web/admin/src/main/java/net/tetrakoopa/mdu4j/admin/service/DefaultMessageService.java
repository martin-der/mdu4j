package net.tetrakoopa.mdu4j.admin.service;

import net.tetrakoopa.mdu4j.service.message.LangRequestAwareMessageService;

import org.springframework.stereotype.Service;

@Service
public class DefaultMessageService extends LangRequestAwareMessageService {

    public DefaultMessageService() {
        super("main");
    }
}
