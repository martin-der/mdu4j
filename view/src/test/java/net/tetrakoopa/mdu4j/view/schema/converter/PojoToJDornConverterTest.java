package net.tetrakoopa.mdu4j.view.schema.converter;

import net.tetrakoopa.mdu4j.service.SerialisationService;
import net.tetrakoopa.mdu4j.test.things.CaptainRecursive;
import net.tetrakoopa.mdu4j.test.things.Truc;
import net.tetrakoopa.mdu4j.view.schema.bean.JDornJsonSchema;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintWriter;

import javax.xml.bind.JAXBException;


public class PojoToJDornConverterTest {

	PojoToJDornConverter converter = new PojoToJDornConverter();

	@Test
	public void createBiduleSchema() {
		Truc truc = new Truc();
		JDornJsonSchema.Object schema = converter.convert(truc.getClass());
		Assert.assertEquals(schema.getProperties().size(), 6);
		//org_eclipse_persistence_jaxb_MarshallerProperties_MEDIA_TYPE
		//org.eclipse.persistence.jaxb.MarshallerProperties.JSON_INCLUDE_ROOT
	}

	@Test
	public void createCaptainRecursiveSchema() throws JAXBException {
		CaptainRecursive captain = new CaptainRecursive();
		JDornJsonSchema.Object schema = converter.convert(captain.getClass());
		new SerialisationService().generateJson(schema, new PrintWriter(System.out));
		Assert.assertEquals(schema.getProperties().size(), 6);
	}
}
