package net.tetrakoopa.mdu4j.admin.front.servlet.bean.response;

import net.tetrakoopa.mdu4j.admin.front.servlet.DataServlet;
import net.tetrakoopa.mdu4j.admin.front.servlet.bean.data.DataAction;
import net.tetrakoopa.mdu4j.front.servlet.bean.response.ActionResponse;
import net.tetrakoopa.mdu4j.util.xml.adapter.ClassAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="dataAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataActionResponse extends ActionResponse {

    public enum Status {
        OK, NO_AVAILABLE_HANDLER, HANDLER_CANNOT_SERIALIZE, NO_SUCH_ELEMENT
    }

    @XmlElement(name="name")
    private DataAction action;

    public static class AvailableDataHandler {
        public final String type;
        public final String springName;
        @XmlElement(name = "className")
        @XmlJavaTypeAdapter(ClassAdapter.class)
        public final DataServlet.DataHandler handler;
        public final DataServlet.DataHandler.HandlingAbility ability;

        /** Don't use : for some reason it is required for serialisation */
        @Deprecated
        public AvailableDataHandler() {
            this.type = null;
            this.springName = null;
            this.handler = null;
            this.ability = null;
        }

        public AvailableDataHandler(String type, String springName, DataServlet.DataHandler handler, DataServlet.DataHandler.HandlingAbility ability) {
            this.type = type;
            this.springName = springName;
            this.handler = handler;
            this.ability = ability;
        }
    }



    public static class Retrieval {

        private Status status;

        private String mimeType;

        private byte [] object;

        public byte[] getObject() {
            return object;
        }

        public void setObject(byte[] object) {
            this.object = object;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

    }

    @XmlElementWrapper(name="handlers")
    @XmlElement(name="handler")
    private List<AvailableDataHandler> handlers;

    private Retrieval retrieval;

    public List<AvailableDataHandler> getHandlers() {
        if (handlers==null) {
            handlers = new ArrayList<AvailableDataHandler>();
        }
        return handlers;
    }

    public Retrieval getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(Retrieval retrieval) {
        this.retrieval = retrieval;
    }

    public DataAction getAction() {
        return action;
    }

    public void setAction(DataAction action) {
        this.action = action;
    }


}
