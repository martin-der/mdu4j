package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.logger.bean.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.File;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"name", "retrievalMethod", "file", "rolledInstance", "content" })
public class PropertiesResponse {

    private String name;

    private File file;

    private String retrievalMethod;

    private boolean rolledInstance;

    private String content;

    public PropertiesResponse() {
    }

    public String getName() {
        return name;
    }

    public String getRetrievalMethod() {
        return retrievalMethod;
    }

    public File getFile() {
        return file;
    }

    public boolean isRolledInstance() {
        return rolledInstance;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class Factory {

        public static PropertiesResponse createFromLogger(Logger logger) {
            final PropertiesResponse response = new PropertiesResponse();
                response.name = logger.getName();
                response.retrievalMethod = logger.getRetrievalMethod();
                response.file = logger.getFile();
            return response;
        }
        public static PropertiesResponse createFromRolledFileLogger(Logger logger, File file) {
            final PropertiesResponse response = createFromLogger(logger);
            response.file = file;
            response.rolledInstance = true;
            return response;
        }
        public static PropertiesResponse createFromFileInFolder(Logger originalLoggerFolderScan, File file) {
            final PropertiesResponse response = new PropertiesResponse();
            response.name = file.getName();
            response.retrievalMethod = "full-scan";
            if (originalLoggerFolderScan != null) {
                response.retrievalMethod = response.retrievalMethod + " from " + originalLoggerFolderScan.getName();
            }
            response.file = file;
            return response;
        }
    }

}
