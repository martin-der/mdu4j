package net.tetrakoopa.mdu4j.service.json.bean;

import net.tetrakoopa.mdu4j.service.json.JsonSchemaService;
import net.tetrakoopa.mdu4j.util.xml.adapter.AbstractMapAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"properties", "definitions"})
public class JsonObject {

    public static class JsonProperty {

        private int propertyOrder;

        private String title;

        @XmlAttribute(name = "$ref")
        private String jsonTypeReference__;

        public int getPropertyOrder() {
            return propertyOrder;
        }

        public void setPropertyOrder(int propertyOrder) {
            this.propertyOrder = propertyOrder;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJsonTypeReference__() {
            return jsonTypeReference__;
        }

        public void setJsonTypeReference__(String jsonTypeReference) {
            this.jsonTypeReference__ = jsonTypeReference__;
        }

    }

    public static class JsonDefinition {

        @XmlEnum(JsonSchemaService.JsonTypeAdapter.class)
        public enum JsonType {
            INTEGER("integer"), STRING("string");

            public final String value;

            JsonType(String value) {
                this.value = value;
            }
        }

        private JsonType type;
        private Integer minLength;
        private Integer maxLength;

        public JsonType getType() {
            return type;
        }

        public void setType(JsonType type) {
            this.type = type;
        }

        public Integer getMinLength() {
            return minLength;
        }

        public void setMinLength(Integer minLength) {
            this.minLength = minLength;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
        }
    }

    @XmlJavaTypeAdapter(AbstractMapAdapter.class)
    private final Map<String, JsonProperty> properties = new HashMap<String, JsonProperty>();

    @XmlJavaTypeAdapter(AbstractMapAdapter.class)
    private final Map<String, JsonDefinition> definitions = new HashMap<String, JsonDefinition>();


    public Map<String, JsonProperty> getProperties() {
        return properties;
    }
    public Map<String, JsonDefinition> getDefinitions() {
        return definitions;
    }
}
