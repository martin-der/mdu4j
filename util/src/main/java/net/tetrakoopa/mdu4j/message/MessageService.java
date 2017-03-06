package net.tetrakoopa.mdu4j.message;

public interface MessageService {

    String getMessage(String text);

    String getMessage(String text, Object... args);
}
