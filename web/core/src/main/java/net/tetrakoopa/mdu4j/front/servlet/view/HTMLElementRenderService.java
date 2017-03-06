package net.tetrakoopa.mdu4j.front.servlet.view;

import net.tetrakoopa.mdu4j.front.servlet.bean.ActionAttribute;
import net.tetrakoopa.mdu4j.message.MessageService;
import net.tetrakoopa.mdu4j.util.EnumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class HTMLElementRenderService extends HTMLActionRenderHelper {

    @Autowired
    private MessageService messageService;

    public <E extends Enum<E>> String getEnumLabel(E enuum) {
        final Field field = EnumUtil.getValue((Class<E>)enuum.getClass(), enuum);
        final ActionAttribute.Label label = field.getAnnotation(ActionAttribute.Label.class);
        return escape(messageService.getMessage(label == null ? enuum.name() : label.value() ),false);
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

}
