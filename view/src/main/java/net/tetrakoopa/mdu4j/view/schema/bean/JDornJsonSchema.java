package net.tetrakoopa.mdu4j.view.schema.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JDornJsonSchema {

	public enum Type {
		OBJECT(false), STRING(true), INTEGER(true), BOOLEAN(true), ARRAY(false);

		private final boolean primitive;

		Type(boolean primitive) {
			this.primitive = primitive;
		}

		public boolean isPrimitive() {
			return primitive;
		}
	}

	public static class Schema extends Object {

		private Map<String, Object> definitions;


 	}

	public static abstract class Element {

		private final Type type;

		public Element(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}
	}

	public static abstract class TypedElement extends Element {

		private String reference;

		public TypedElement(Type type) {
			this(type, null);
		}
		public TypedElement(Type type, String reference) {
			super(type);
			this.reference = reference;
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}
	}

	public static class Primitive extends Element {
		public Primitive(Type type) {
			super(type);
			if (!type.primitive) throw new IllegalArgumentException("'"+type.name()+"' is not a primitive type");
		}
	}

	public static class Object extends TypedElement {

		private Map<String, Element> properties;

		public Object() {
			super(Type.OBJECT);
		}
		public Object(String reference) {
			super(Type.OBJECT, reference);
		}

		public Object addProperty(String name, Element property) {
			properties.put(name, property);
			return this;
		}

		public Map<String, Element> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, Element> properties) {
			this.properties = properties;
		}

	}

	public static class Array extends Element {

		public static class Items extends TypedElement {
			private String title;

			public Items(String reference) {
				super(Type.ARRAY, reference);
			}
		}

		private final List<Element> items = new ArrayList<>();

		public Array() {
			super(Type.ARRAY);
		}

		public void addItem(Element element) {
			items.add(element);
		}
	}

}
