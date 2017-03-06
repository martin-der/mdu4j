package net.tetrakoopa.mdu4j.service.json;

import net.tetrakoopa.mdu4j.service.json.JsonSchemaService;
import net.tetrakoopa.mdu4j.service.json.bean.JsonObject;
import net.tetrakoopa.mdu4j.util.test.AbstractSerialisationTest;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonSchemaServiceTest extends AbstractSerialisationTest {

    JsonSchemaService service = new JsonSchemaService ();

    @Test
    @Ignore
    public void testSimpleSchema() {

        String text;
        String expected = loadJsonResourceForThisMethod();

        JsonObject object = new JsonObject();
        JsonObject.JsonProperty property;

        property = new JsonObject.JsonProperty();
        property.setTitle("declaredAge");
        object.getProperties().put("age", property);

        property = new JsonObject.JsonProperty();
        object.getProperties().put("name", property);


        text = serializeJson(object);

        assertEquals(expected, text);

    }

}
