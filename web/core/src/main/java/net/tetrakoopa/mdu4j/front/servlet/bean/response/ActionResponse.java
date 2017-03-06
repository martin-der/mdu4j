package net.tetrakoopa.mdu4j.front.servlet.bean.response;


import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ActionResponse {

    public final static String XMLNS = "http://www.tetrakoopa.net/admin";
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Status {

        public enum Code {
            FAILURE_UNKOWN("BAD-STATUS"),
            FAILURE_NO_ACTION("ACTION-MISSING"),
            FAILURE_UNKNOWN_ACTION("UNKNOWN-ACTION"),
            FAILURE_ACTION_BAD_PARAMETER("ACTION-BAD-PARAMETER"),
            SUCCESS("OK");

            public final String message;

            Code(String message) {
                this.message = message;
            }
        }
        
        @XmlElement
        private String code;

        @XmlElement
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
        public void setCode(Code code) {
            this.code = code.message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
        public void setCodeAndMessage(Code code, String message) {
            setCode(code);
            this.message = message;
        }
    }

    @XmlElement
    private Status status;

    public Status getStatus() {
        if (status==null) {
            status = new Status();
        }
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
}
