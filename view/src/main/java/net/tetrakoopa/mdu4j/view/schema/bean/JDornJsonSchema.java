package net.tetrakoopa.mdu4j.view.schema.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JDornJsonSchema {

	public enum Type {
		OBJECT(false), STRING(true), INTEGER(true), NUMBER(true), BOOLEAN(true), ARRAY(false);

		private final boolean primitive;

		private final String code;

		Type(boolean primitive) {
			this.primitive = primitive;
			this.code = this.name().toLowerCase();
		}

		public boolean isPrimitive() {
			return primitive;
		}

		public String getCode() {
			return code;
		}

	}

	public static class Schema extends Object {

		private Map<String, Element> definitions;

		public Map<String, Element> getDefinitions() {
			return definitions;
		}

		public void addDefinition(String reference, Element element) {
			if (definitions == null) {
				definitions = new HashMap<>();
			}
			definitions.put(reference, element);
		}
	}

	public static abstract class Element {

		private final Type type;

		private String title;

		public Element(Type type, String title) {
			this.type = type;
			this.title = title;
		}
		public Element(Type type) {
			this(type, null);
		}

		public Type getType() {
			return type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	public interface Typed {
		String getReference();
	}
	public interface OrderedProperty {
		Integer getPropertyOrder();
	}

	public static abstract class TypedElement extends Element implements Typed {

		private String reference;

		public TypedElement(Type type) {
			this(type, null);
		}
		public TypedElement(Type type, String reference) {
			super(type);
			this.reference = reference;
		}

		@Override
		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}
	}

	public static class Primitive extends Element {
		public Primitive(Type type) {
			this(type, null);
		}
		public Primitive(Type type, String label) {
			super(type, label);
			if (!type.primitive) throw new IllegalArgumentException("'"+type.name()+"' is not a primitive type");
		}
	}
	public static class PrimitiveProperty extends Primitive implements OrderedProperty {

		private Integer propertyOrder;

		public PrimitiveProperty(Type type) {
			super(type);
		}

		public PrimitiveProperty(Type type, String label) {
			super(type, label);
		}

		public void setPropertyOrder(Integer propertyOrder) {
			this.propertyOrder = propertyOrder;
		}

		@Override
		public Integer getPropertyOrder() {
			return null;
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

			public Items(Element elementsType) {
				super(elementsType.getType(), elementsType instanceof TypedElement ? ((TypedElement)elementsType).getReference():null);
			}
		}

		private final Items items;

		public Array(Element elementsType) {
			super(Type.ARRAY);
			items = new Items(elementsType);
		}

		public Items getItems() {
			return items;
		}
	}

}
