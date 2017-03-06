package net.tetrakoopa.mdu4j.util.xml.adapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

public final class InlinedKeyMapAdapter extends XmlAdapter<InlinedKeyMapAdapter.InlinedKeyAdaptedMap, Map<Object,Object>> {

    @Override
    public InlinedKeyAdaptedMap marshal(Map<Object,Object> map) throws Exception {
        return marshal_new(map);
        //return marshal_old(map);
    }
    public InlinedKeyAdaptedMap marshal_old(Map<Object,Object> map) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element rootElement = document.createElement("map");
        document.appendChild(rootElement);

        for(Map.Entry<Object,Object> entry : map.entrySet()) {
            Element mapElement = document.createElement(entry.getKey().toString());
            mapElement.setTextContent(entry.getValue().toString());
            rootElement.appendChild(mapElement);
        }

        InlinedKeyAdaptedMap inlinedKeyAdaptedMap = new InlinedKeyAdaptedMap();
        inlinedKeyAdaptedMap.setValue(document);
        return inlinedKeyAdaptedMap;
    }

    public InlinedKeyAdaptedMap marshal_new(Map<Object,Object> map) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();

        for(Map.Entry<Object,Object> entry : map.entrySet()) {
            Element element = document.createElement(entry.getKey().toString());
            document.appendChild(element);
            //Element mapElement = document.createElement(entry.getKey().toString());
            //mapElement.setTextContent(entry.getValue().toString());
            element.setTextContent(entry.getValue().toString());
            //element.appendChild(mapElement);
            //element.appendChild(element);
        }

        InlinedKeyAdaptedMap inlinedKeyAdaptedMap = new InlinedKeyAdaptedMap();
        inlinedKeyAdaptedMap.setValue(document);
        return inlinedKeyAdaptedMap;
    }

    @Override
    public Map<Object,Object> unmarshal(InlinedKeyAdaptedMap inlinedKeyAdaptedMap) throws Exception {
        Map<Object,Object> map = new HashMap<Object,Object>();
        Element rootElement = (Element) inlinedKeyAdaptedMap.getValue();
        NodeList childNodes = rootElement.getChildNodes();
        for(int x=0,size=childNodes.getLength(); x<size; x++) {
            Node childNode = childNodes.item(x);
            if(childNode.getNodeType() == Node.ELEMENT_NODE) {
                map.put(childNode.getLocalName(), childNode.getTextContent());
            }
        }
        return map;
    }

    public static class InlinedKeyAdaptedMap {

        private Object value;

        @XmlAnyElement
        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

    }
}