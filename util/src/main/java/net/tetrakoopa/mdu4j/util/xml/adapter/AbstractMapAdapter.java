package net.tetrakoopa.mdu4j.util.xml.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractMapAdapter<K, V, A extends AbstractMapAdapter.AdaptedMap<K, V>> extends XmlAdapter<AbstractMapAdapter.AdaptedMap<K, V>, Map<K, V>> {


	@Override
    public AbstractMapAdapter.AdaptedMap<K, V> marshal(Map<K, V> hashMap) throws Exception {
        if (hashMap==null)
            return null;
        final AbstractMapAdapter.AdaptedMap<K, V> adaptedMap = getAdaptedMap();
        for(Map.Entry<K, V> entry : hashMap.entrySet()) {
            adaptedMap.getItem().add(new HashMapEntry<K, V>(entry.getKey(), entry.getValue()));
        }
        return adaptedMap;
    }

    @Override
    public HashMap<K, V> unmarshal(AbstractMapAdapter.AdaptedMap<K, V> entries) throws Exception {
        final HashMap<K, V> result = new HashMap<K, V>();
        for(HashMapEntry<K, V> entry : entries.getItem())
            result.put(entry.key, entry.value);
        return result;
    }
	protected abstract A getAdaptedMap();

	@XmlAccessorType(XmlAccessType.FIELD)
	public abstract static class AdaptedMap<K, V> {
        public List<HashMapEntry<K, V>> item = new ArrayList<HashMapEntry<K, V>>();
        //@XmlElement(name="titi")
        public abstract List<HashMapEntry<K, V>> getItem();
    }

    public static class HashMapEntry<K, V> {

        @XmlAttribute
        public K key;

        @XmlValue
        public V value;

        public HashMapEntry() {
        }

        public HashMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}