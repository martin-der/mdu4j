package net.tetrakoopa.mdu4j.util.xml.adapter;

import org.springframework.core.GenericTypeResolver;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class EnumAdapter<E extends Enum<E>> extends XmlAdapter<String, E> {

    protected abstract String getSerialized(E e);

    private final Class<E> enumClass;

    protected EnumAdapter() {
        enumClass = (Class<E>)GenericTypeResolver.resolveTypeArguments(getClass(), EnumAdapter.class)[0];
    }

    @Override
    public E unmarshal(String value) throws Exception {
        for (E e : enumClass.getEnumConstants()) {
            if (getSerialized(e).equals(value))
                return e;
        }
        throw new IllegalArgumentException("Cannot unmarshal Enum '"+enumClass.getName()+"' from '"+value+"'");
    }

    @Override
    public String marshal(E object) throws Exception {
        return getSerialized(object);
    }
}
