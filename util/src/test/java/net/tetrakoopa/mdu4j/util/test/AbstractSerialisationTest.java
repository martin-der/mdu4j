package net.tetrakoopa.mdu4j.util.test;

import net.tetrakoopa.mdu4j.service.SerialisationService;
import net.tetrakoopa.mdu4j.test.TestResourcesFetcher;
import net.tetrakoopa.mdu4j.util.IOUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

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

    protected String serializeXml(Object object) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);
        try {
            new SerialisationService().generateXml(object, writer, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.getBuffer().toString();
    }

    protected String serializeJson(Object object) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);
        try {
            new SerialisationService().generateJson(object, writer, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.getBuffer().toString();
    }

}
