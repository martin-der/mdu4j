package net.tetrakoopa.mdu4j.test;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

public abstract class AbstractSerialisationTest {
	private final static TestResourcesFetcher testResourcesFetcher = new TestResourcesFetcher().excludeClass(AbstractSerialisationTest.class);

	protected String loadXmlResourceForThisMethod() {
		return loadStringResourceForThisMethod("xml");
	}
	protected String loadJsonResourceForThisMethod() {
		return loadStringResourceForThisMethod("json");
	}
	private String loadStringResourceForThisMethod(String extension) {
		return testResourcesFetcher.getTextForThisMethod(extension);
	}

	protected abstract String serializeXml(Object object);

	protected abstract String serializeJson(Object object);

}
