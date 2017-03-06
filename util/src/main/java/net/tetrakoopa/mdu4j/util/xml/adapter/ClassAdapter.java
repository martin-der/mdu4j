package net.tetrakoopa.mdu4j.util.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ClassAdapter extends XmlAdapter<String, Object> {

    @Override
    public Class<?> unmarshal(String value) throws Exception {
        return Class.forName(value);
    }

    @Override
    public String marshal(Object object) throws Exception {
        final Class<?> clazz = object instanceof Class ? (Class<?>)object : object.getClass();
        return clazz.getName();
    }
}
