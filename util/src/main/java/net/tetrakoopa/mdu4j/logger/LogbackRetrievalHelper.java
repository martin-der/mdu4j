package net.tetrakoopa.mdu4j.logger;

import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import net.tetrakoopa.mdu4j.logger.bean.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class LogbackRetrievalHelper implements LoggerRetrievalService.LoggerFinder {

    @Override
    public void appendLoggers(List<Logger> loggers) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        //for (ch.qos.logback.classic.Logger logbackLogger : context.getLoggerList()) {
        //    for (Iterator<Appender<LoggingEvent>> index = logbackLogger.iteratorForAppenders(); index.hasNext();) {
        //        final Appender<LoggingEvent> appender = index.next();
        for (ch.qos.logback.classic.Logger logbackLogger : context.getLoggerList()) {
            for (Iterator<Appender<ILoggingEvent>> index = logbackLogger.iteratorForAppenders(); index.hasNext();) {
                final Appender<ILoggingEvent> appender = index.next();
                final Logger logger = new Logger();
                logger.setRetrievalMethod("logback.loggerContext / "+appender.getClass().getName());
                logger.setName(appender.getName());
                if (appender instanceof ch.qos.logback.core.FileAppender) {
                    logger.setFile(new File(((FileAppender)appender).getFile()));

                    if (appender instanceof RollingFileAppender) {

                        final String rollingNamePattern;
                        final RollingPolicy rollingPolicy = ((RollingFileAppender)appender).getRollingPolicy();
                        if (rollingPolicy instanceof TimeBasedRollingPolicy) {
                            rollingNamePattern = ((TimeBasedRollingPolicy)rollingPolicy).getFileNamePattern();
                        } else {
                            rollingNamePattern = null;
                        }

                        final String fileName = LoggerRetrievalService.extractNameWithoutExtension(logger.getFile());
                        logger.setRollingFileNameMatcher(new Logger.RollingFileNameMatcher() {
                            @Override
                            public boolean match(File file) throws Exception {
                                final String rolledFileName = LoggerRetrievalService.extractNameWithoutExtension(file);
                                if (rollingNamePattern == null) {
                                    return rolledFileName.startsWith(fileName);
                                } else {
                                    // TODO comparer avec la valeur trouvée rollingNamePattern
                                    return rolledFileName.startsWith(fileName);
                                }
                            }
                        });
                    }
                }

                loggers.add(logger);
            }
        }

    }
}
