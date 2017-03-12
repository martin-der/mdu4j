package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.bean.log.LogAction;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="logAction", namespace= ActionResponse.XMLNS)
@XmlAccessorType(XmlAccessType.FIELD)
public class LogActionResponse extends ActionResponse {

    @XmlElement(name="name")
    private LogAction action;

    @XmlElementWrapper(name="logs")
    @XmlElement(name="log")
    private List<LogResponse> involvedLogs;

    public LogAction getAction() {
        return action;
    }

    public void setAction(LogAction action) {
        this.action = action;
    }

    public List<LogResponse> getInvolvedLogs() {
        if (involvedLogs == null) {
            involvedLogs = new ArrayList<LogResponse>();
        }
        return involvedLogs;
    }

    public void setInvolvedLogs(List<LogResponse> involvedLogs) {
        this.involvedLogs = involvedLogs;
    }
    
}
