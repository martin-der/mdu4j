package net.tetrakoopa.mdu4j.service.json;

import net.tetrakoopa.mdu4j.service.json.bean.JsonObject;

import net.tetrakoopa.mdu4j.util.xml.adapter.EnumAdapter;

public class JsonSchemaService {

    public static class JsonTypeAdapter extends EnumAdapter<JsonObject.JsonDefinition.JsonType> {

        @Override
        protected String getSerialized(JsonObject.JsonDefinition.JsonType jsonType) {
            return jsonType.value;
        }
    }

}
