package net.tetrakoopa.mdu4j.logger.bean;

import java.io.File;

public class Logger {

    public interface RollingFileNameMatcher {
        boolean match(File file) throws Exception;
    }

    private String retrievalMethod;

    private String name;

    private File file;

    private RollingFileNameMatcher rollingFileNameMatcher;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRetrievalMethod() {
        return retrievalMethod;
    }

    public void setRetrievalMethod(String retrievalMethod) {
        this.retrievalMethod = retrievalMethod;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public RollingFileNameMatcher getRollingFileNameMatcher() {
        return rollingFileNameMatcher;
    }

    public void setRollingFileNameMatcher(RollingFileNameMatcher rollingFileNameMatcher) {
        this.rollingFileNameMatcher = rollingFileNameMatcher;
    }

    public boolean isRolledFile(File file ) throws Exception {
        if (this.file == null || rollingFileNameMatcher == null) {
            return false;
        }
        return rollingFileNameMatcher.match(file);
    }

}
