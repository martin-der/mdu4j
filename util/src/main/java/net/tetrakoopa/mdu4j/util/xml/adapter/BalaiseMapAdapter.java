package net.tetrakoopa.mdu4j.util.xml.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

public abstract class BalaiseMapAdapter<K, V> extends XmlAdapter<JAXBElement<BalaiseMapAdapter.HashMapEntry[]>, Map<K, V>> {


	@Override
    public JAXBElement<HashMapEntry[]> marshal(Map<K, V> hashMap) throws Exception {

	    final JAXBElement<HashMapEntry[]> element = new JAXBElement<HashMapEntry[]>(new QName("toto-item"),
				HashMapEntry[].class, toHashMapEntryArray(hashMap));
		return element;
    }

    @Override
    public HashMap<K, V> unmarshal(JAXBElement<HashMapEntry[]> elements) throws Exception {
        final HashMap<K, V> result = new HashMap<K, V>();
        for(HashMapEntry<K, V> entry : elements.getValue())
            result.put(entry.key, entry.value);
        return result;
    }


    private HashMapEntry<K,V>[] toHashMapEntryArray(Map<K, V> hashMap) {
		final Set<Map.Entry<K, V>> entriesSet = hashMap.entrySet();
		final HashMapEntry<K, V>[] entries = new HashMapEntry[entriesSet.size()];
		int index = 0;
		for(Map.Entry<K, V> entry : entriesSet) {
			entries[index] = new HashMapEntry<K, V>(entry.getKey(), entry.getValue());
			index ++;
		}
		return entries;
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