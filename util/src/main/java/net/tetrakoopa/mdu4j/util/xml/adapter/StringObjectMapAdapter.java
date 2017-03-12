package net.tetrakoopa.mdu4j.util.xml.adapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

public final class StringObjectMapAdapter extends AbstractMapAdapter {

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement
	@XmlType(propOrder={"name", "content"})
    protected static class ItemStringObjectAdaptedMap extends AbstractMapAdapter.AdaptedMap {

		@XmlElement(name = "truc")
		//public List<HashMapEntry<String, Object>> item = new ArrayList<HashMapEntry<String, Object>>();

		@Override
        public List<HashMapEntry<String, Object>> getItem() {
            return item;
        }
    }

    @Override
    protected AbstractMapAdapter.AdaptedMap getAdaptedMap() {
		return new ItemStringObjectAdaptedMap();
    }
}