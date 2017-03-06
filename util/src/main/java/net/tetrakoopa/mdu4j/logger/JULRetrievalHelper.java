package net.tetrakoopa.mdu4j.logger;

import net.tetrakoopa.mdu4j.logger.bean.Logger;

import java.util.Enumeration;
import java.util.List;

public class JULRetrievalHelper implements LoggerRetrievalService.LoggerFinder{

    @Override
    public void appendLoggers(List<Logger> loggers) {
        appendLoggersFromJUL(loggers);
        appendLoggersFromJUL2(loggers);
    }

    private void appendLoggersFromJUL2(List<Logger> loggers) {
        final Enumeration<String> names = java.util.logging.LogManager.getLogManager().getLoggerNames();
        while(names.hasMoreElements()) {
            final String name = names.nextElement();
            final Logger logger = new Logger();
            logger.setRetrievalMethod("jul.LogManager.loggerNames");
            logger.setName(name);
            loggers.add(logger);
        }
    }
    private void appendLoggersFromJUL(List<Logger> loggers) {
        java.util.logging.Logger julLogger = java.util.logging.Logger.getAnonymousLogger();
        while ( julLogger.getParent() != null) {
            julLogger = julLogger.getParent();
        }
        final java.util.logging.Handler[] handlers = julLogger.getHandlers();
        for ( java.util.logging.Handler handler : handlers) {
            final Logger logger = new Logger();
            logger.setRetrievalMethod("jul.Logger.anonymousLogger..parent.handlers");
            logger.setName(julLogger.getName());
            loggers.add(logger);
        }
    }

}
