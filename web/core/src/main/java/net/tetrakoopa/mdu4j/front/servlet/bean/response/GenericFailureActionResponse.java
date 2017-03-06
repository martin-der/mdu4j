package net.tetrakoopa.mdu4j.front.servlet.bean.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="failure")
public class GenericFailureActionResponse extends ActionResponse {

    public GenericFailureActionResponse() { this(Status.Code.FAILURE_UNKOWN, ""); }
    public GenericFailureActionResponse (String message) {
        this(Status.Code.FAILURE_UNKOWN, message);
    }
    public GenericFailureActionResponse(Status.Code code, String message) {
        getStatus().setCodeAndMessage(code, message);
    }
    
}
