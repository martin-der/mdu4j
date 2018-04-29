package net.tetrakoopa.mdu4j.view.schema.converter;


import net.tetrakoopa.mdu4j.util.Holder;
import net.tetrakoopa.mdu4j.view.schema.bean.JDornJsonSchema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PojoToJDornConverter extends AbstractConverter<JDornJsonSchema.Schema, JDornJsonSchema.Primitive, JDornJsonSchema.Object, String> {

	@Override
	protected JDornJsonSchema.Schema createSchema() {
		return new JDornJsonSchema.Schema();
	}

	@Override
	protected void convertToSchema(Context context, Class<? extends Object> clazz, JDornJsonSchema.Schema schema) {
		buildObject(context, clazz, schema);
		final Map<String, JDornJsonSchema.Object> mappings = context.getMapping();
		for (Map.Entry<String, JDornJsonSchema.Object> mapping : mappings.entrySet()) {
			schema.addDefinition(mapping.getKey(), mapping.getValue());
		}
	}

	@Override
	protected JDornJsonSchema.Object createPojoMapping() {
		final JDornJsonSchema.Object pojoMapping = new JDornJsonSchema.Object();
		return pojoMapping;
	}

	@Override
	protected void convertToPojoMapping(Context context, Class<?> clazz, JDornJsonSchema.Object pojoMapping, String reference) {
		if (reference != null) {
			pojoMapping.setReference(reference);
		} else {
			buildObject(context, clazz, pojoMapping);
		}
	}

	@Override
	protected JDornJsonSchema.Primitive createPrimitiveMapping(Context.Parent parentContext, PrimitiveType primitiveType) {
		final boolean needOrder =  parentContext != null && parentContext.getType() == Context.Parent.Type.POJO;

		switch (primitiveType) {
			case STRING:
				return needOrder
					? new JDornJsonSchema.PrimitiveProperty(JDornJsonSchema.Type.STRING)
					: new JDornJsonSchema.Primitive(JDornJsonSchema.Type.STRING);
			case INTEGER:
				return needOrder
					? new JDornJsonSchema.PrimitiveProperty(JDornJsonSchema.Type.INTEGER)
					: new JDornJsonSchema.Primitive(JDornJsonSchema.Type.INTEGER);
			case NUMBER: return needOrder
					? new JDornJsonSchema.PrimitiveProperty(JDornJsonSchema.Type.NUMBER)
					: new JDornJsonSchema.Primitive(JDornJsonSchema.Type.NUMBER);
			case BOOLEAN: return needOrder
					? new JDornJsonSchema.PrimitiveProperty(JDornJsonSchema.Type.BOOLEAN)
					: new JDornJsonSchema.Primitive(JDornJsonSchema.Type.BOOLEAN);
		}

		return null;
	}

	@Override
	protected void convertToPrimitiveMapping(Context context, Class<?> clazz, JDornJsonSchema.Primitive primitiveMapping) {
		primitiveMapping.setTitle(context.getLabel());
	}

	@Override
	protected String createReference(Class<?> clazz) {
		return "#/definitions/"+(clazz.getName().replaceAll("\\.","/"));
	}

	private void buildObject(Context context, Class<?> type, final JDornJsonSchema.Object pojoMapping) {

		pojoMapping.setProperties(new HashMap<>());

		pojoMapping.setTitle(context.getLabel());

		withAllFields(context, type, (fieldContext, field, name, hints, fromParent) -> {

			final Class<?> fieldClazz = field.getType();

			final String label = fieldContext.getLabel();

			if (isPrimitiveType(fieldClazz)) {
				final JDornJsonSchema.Primitive primitiveMapping = getPrimitiveMapping(fieldContext, fieldClazz);
				pojoMapping.addProperty(name, primitiveMapping);
				return;
			}

			if (List.class.isAssignableFrom(fieldClazz)) {
				final Type collectionType = field.getGenericType();
				final Type elementsType;
				if (collectionType instanceof ParameterizedType) {
					elementsType = ((ParameterizedType) collectionType).getActualTypeArguments()[0];
				} else {
					elementsType = null;
				}
				final JDornJsonSchema.PrimitiveProperty elementsMapping = (JDornJsonSchema.PrimitiveProperty) getPrimitiveMapping(fieldContext, (Class<?>) elementsType);
				elementsMapping.setPropertyOrder(hints.getOrder());
				pojoMapping.addProperty(name, new JDornJsonSchema.Array(elementsMapping));
				return;
			}

			final JDornJsonSchema.Object objectMapping = getPojoMapping(fieldContext, fieldClazz);
			if (label != null) objectMapping.setTitle(label);
			pojoMapping.addProperty(name, objectMapping);
		});
	}

}
