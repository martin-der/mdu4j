package net.tetrakoopa.mdu4j.service;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SerialisationService {

    public static Pattern XML_PROLOG = Pattern.compile("^<\\?xml (.*)\\?>(.*)", Pattern.DOTALL);

    private static String org_eclipse_persistence_jaxb_MarshallerProperties_MEDIA_TYPE = "eclipselink.media-type";
    private static String org_eclipse_persistence_jaxb_MarshallerProperties_JSON_INCLUDE_ROOT = null;

    public String generateXml(Object rootElement) throws Exception {
        return generateXml(rootElement, Boolean.FALSE);
    }

    public String generateXml(Object rootElement, boolean format) throws Exception {

        final Writer writer = new StringWriter();

        generateXml(rootElement, writer, format);

        return writer.toString();
    }

    /**
     * Call <code>generateXml(Object, Writer, (Boolean)false)</code>
     */
    public void generateXml(Object rootElement, Writer writer) throws JAXBException {
        generateXml(rootElement, writer, false);
    }
    public void generateXml(Object rootElement, Writer writer, boolean format) throws JAXBException  {

        final JAXBContext context = JAXBContext.newInstance(rootElement.getClass());
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
        marshaller.marshal(rootElement, writer);
    }

    /**
     * Call <code>generateJson(Object, Writer, (Boolean)false)</code>
     */
    public void generateJson(Object rootElement, Writer writer) throws JAXBException  {
        generateJson(rootElement, writer, false);
    }
    public void generateJson(Object rootElement, Writer writer, boolean format) throws JAXBException  {

        JAXBContext context = JAXBContext.newInstance(rootElement.getClass());
        Marshaller marshaller = context.createMarshaller();

        getEclipseMarshallerProperties();

        //marshaller.setProperty(org_eclipse_persistence_jaxb_MarshallerProperties_MEDIA_TYPE, "application/json");
        //marshaller.setProperty(org_eclipse_persistence_jaxb_MarshallerProperties_JSON_INCLUDE_ROOT, "eclipselink.json.include-root");

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
        marshaller.marshal(rootElement, writer);
    }

    private static synchronized void getEclipseMarshallerProperties() {
        if (org_eclipse_persistence_jaxb_MarshallerProperties_MEDIA_TYPE != null || org_eclipse_persistence_jaxb_MarshallerProperties_JSON_INCLUDE_ROOT != null) {
            return;
        }
        final Class<?> marshallerPropertiesClass;
        final String marshallerPropertiesClassName = "org.eclipse.persistence.jaxb.MarshallerProperties";
        try {
            marshallerPropertiesClass = Class.forName(marshallerPropertiesClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable se(/de)rialize json : '"+marshallerPropertiesClassName+"' class in not in the classpath : "+e.getMessage(), e);
        }
        try {
            final Field MEDIA_TYPE_field = marshallerPropertiesClass.getField("MEDIA_TYPE");
            final Field JSON_INCLUDE_ROOT_field = marshallerPropertiesClass.getField("JSON_INCLUDE_ROOT");
            org_eclipse_persistence_jaxb_MarshallerProperties_MEDIA_TYPE = (String)MEDIA_TYPE_field.get(null);
            org_eclipse_persistence_jaxb_MarshallerProperties_JSON_INCLUDE_ROOT = (String)JSON_INCLUDE_ROOT_field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
