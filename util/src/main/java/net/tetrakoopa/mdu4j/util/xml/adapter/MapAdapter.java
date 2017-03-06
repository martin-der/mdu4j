package net.tetrakoopa.mdu4j.util.xml.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapAdapter extends XmlAdapter<List<MapAdapter.HashMapEntry>, Map<Object, Object>> {

    @Override
    public List<HashMapEntry> marshal(Map<Object, Object> hashMap) throws Exception {
        if (hashMap==null)
            return null;
        AdaptedMap adaptedMap = new AdaptedMap();
        for(Map.Entry<Object, Object> entry : hashMap.entrySet()) {
            adaptedMap.item.add(new HashMapEntry(entry.getKey(), entry.getValue()));
        }
        return adaptedMap.item;
    }

    @Override
    public HashMap<Object, Object> unmarshal(List<HashMapEntry> entries) throws Exception {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        for(HashMapEntry entry : entries)
            result.put(entry.key, entry.value);
        return result;
    }

    public static class AdaptedMap {
        public List<HashMapEntry> item = new ArrayList<HashMapEntry>();
    }

    public static class HashMapEntry {

        @XmlAttribute
        public Object key;

        @XmlValue
        public Object value;

        public HashMapEntry() {
        }

        public HashMapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

}