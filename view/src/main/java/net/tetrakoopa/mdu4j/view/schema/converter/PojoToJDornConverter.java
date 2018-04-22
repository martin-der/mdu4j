package net.tetrakoopa.mdu4j.view.schema.converter;

import net.tetrakoopa.mdu4j.util.Holder;
import net.tetrakoopa.mdu4j.view.schema.bean.JDornJsonSchema;

import java.util.HashMap;

public class PojoToJDornConverter extends AbstractConverter<JDornJsonSchema.Object,JDornJsonSchema.Object, String> {



	@Override
	protected JDornJsonSchema.Object createSchema() {
		return new JDornJsonSchema.Object();
	}

	@Override
	protected JDornJsonSchema.Object createPojoMapping() {
		final JDornJsonSchema.Object pojoMapping = new JDornJsonSchema.Object();
		return pojoMapping;
	}

	@Override
	protected void convertToSchema(Class<? extends Object> clazz, JDornJsonSchema.Object schema) {
		buildObject(clazz, schema, null);
	}

	@Override
	protected void convertToPojoMapping(Class<?> clazz, JDornJsonSchema.Object pojoMapping, String reference) {
		buildObject(clazz, pojoMapping, reference);
	}

	@Override
	protected String createPojoReference(Class<?> clazz) {
		return "#/definitions/"+(clazz.getName().replaceAll("\\.","/"));
	}

	private void buildObject(Class<? extends Object> clazz, JDornJsonSchema.Object pojoMapping, String reference) {

		if (reference != null) {
			pojoMapping.setReference(reference);
			return;
		}

		pojoMapping.setProperties(new HashMap<>());

		withAllFields(clazz, (field, name, hints, fromParent) -> {

			final Class<? extends Object> fieldClazz = field.getType();

			if (fieldClazz.equals(String.class)) {
				pojoMapping.addProperty(name, new JDornJsonSchema.Primitive(JDornJsonSchema.Type.STRING) );
				return;
			}
			if (fieldClazz.equals(Integer.class) || fieldClazz.equals(Integer.TYPE)) {
				pojoMapping.addProperty(name, new JDornJsonSchema.Primitive(JDornJsonSchema.Type.INTEGER) );
				return;
			}
			if (fieldClazz.equals(Long.class) || fieldClazz.equals(Long.TYPE)) {
				pojoMapping.addProperty(name, new JDornJsonSchema.Primitive(JDornJsonSchema.Type.INTEGER) );
				return;
			}
			if (fieldClazz.equals(Boolean.class) || fieldClazz.equals(Boolean.TYPE)) {
				pojoMapping.addProperty(name, new JDornJsonSchema.Primitive(JDornJsonSchema.Type.BOOLEAN) );
				return;
			}

			pojoMapping.addProperty(name, getPojoMapping(fieldClazz));
		});
	}
}
