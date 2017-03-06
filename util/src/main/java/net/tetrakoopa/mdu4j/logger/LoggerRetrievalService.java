package net.tetrakoopa.mdu4j.logger;


import net.tetrakoopa.mdu4j.logger.bean.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoggerRetrievalService {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggerRetrievalService.class);

    public enum LoggerType {
        LOGBACK(LoggerRetrievalService.class.getPackage().getName()+".LogbackRetrievalHelper"),
        JUL(LoggerRetrievalService.class.getPackage().getName()+".JULRetrievalHelper");

        private final String helperClass;

        LoggerType(String helperClass) {
            this.helperClass = helperClass;
        }

        public static LoggerType fromName(String name) {
            if (name == null) {
                return null;
            }
            for (LoggerType type : LoggerType.values()) {
                if (type.name().equals(name))
                    return type;
            }
            throw new IllegalArgumentException("No such "+LoggerType.class.getName()+" with name = '"+name+"'");
        }
    }

    interface LoggerFinder {
        void appendLoggers(List<Logger> loggers);
    }

    public List<Logger> findDeclaredLoggers(LoggerType... types) {
        final List<Logger> loggers = new ArrayList<Logger>();

        for (LoggerType type : types) {
            appendLoggers(loggers, type);
        }

        return filteredLoggers(loggers);
    }

    private void appendLoggers(List<Logger> loggers, LoggerType type) {
        final Class<?> loggerFinderClass;
        try {
            loggerFinderClass = getClass().forName(type.helperClass);
        } catch (ClassNotFoundException e) {
            notifyLoggerEnumerationError(type, e);
            return;
        }

        final LoggerFinder finder;
        try {
            finder = (LoggerFinder)loggerFinderClass.newInstance();
        } catch (InstantiationException e) {
            notifyLoggerEnumerationError(type, e);
            return;
        } catch (ClassCastException e) {
            notifyLoggerEnumerationError(type, e);
            return;
        } catch (IllegalAccessException e) {
            notifyLoggerEnumerationError(type, e);
            return;
        }

        try {
            finder.appendLoggers(loggers);
        } catch (Exception ex) {
            notifyLoggerEnumerationError(type, ex);
            return;
        }
    }

    private void notifyLoggerEnumerationError(LoggerType type, Exception exception) {
        LOGGER.warn("Unable to enumerate loggers for '"+type.name()+"' : "+exception.getClass().getName()+":"+exception.getMessage());
    }


    /*
     * Filter loogrs :  Keep only one loggers with the same output file
     * @return a new list
     */
    private List<Logger> filteredLoggers(List<Logger> loggers) {
        final List<Logger> uniqueLoggers = new ArrayList<Logger>();
        for (final Logger logger : loggers) {
            if (logger.getFile()!=null) {
                boolean alreadyExists = false;
                for (final Logger uniqueLogger : uniqueLoggers) {
                    if (uniqueLogger.getFile() != null) {
                        try {
                            if (sameFiles(logger.getFile(),uniqueLogger.getFile())) {
                                alreadyExists = true;
                                break;
                            }
                        } catch (IOException e) {
                            break;
                        }
                    }
                }
                if (alreadyExists) {
                    continue;
                }
            }
            uniqueLoggers.add(logger);
        }
        return uniqueLoggers;
    }

    static String extractNameWithoutExtension(File file) {
        final String fileName = file.getName();
        int p = fileName.indexOf('.');
        return (p<0) ? fileName : fileName.substring(0, p);
    }

    public static boolean sameFiles(File fileA, File fileB) throws IOException {
        return fileA.getCanonicalPath().equals(fileB.getCanonicalPath());
    }


}
