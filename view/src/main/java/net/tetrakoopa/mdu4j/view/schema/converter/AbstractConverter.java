package net.tetrakoopa.mdu4j.view.schema.converter;

import net.tetrakoopa.mdu4j.util.Holder;
import net.tetrakoopa.mdu4j.view.UI;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @param <S> The schema
 * @param <O> An object in the schema representation
 * @param <R> Reference class to an known object
 */
public abstract class AbstractConverter<S, O, R> {

	private static class ReferenceableMapping<O, R> {
		public final R reference;
		public final O mapping;
		public boolean used;

		private ReferenceableMapping(R reference, O mapping) {
			this.reference = reference;
			this.mapping = mapping;
		}
	}

	final Map<Class<? extends Object>, ReferenceableMapping<O, R>> referenceablePojoMappings = new HashMap<>();

	public final S convert(Class<? extends Object> clazz) {
		final S schema = createSchema();
		convertToSchema(clazz, schema);
		return schema;
	}

	protected abstract S createSchema();

	protected abstract void convertToSchema(Class<? extends Object> clazz, S schema);

	protected abstract O createPojoMapping();

	protected abstract void convertToPojoMapping(Class<? extends Object> clazz, O pojoMapping, R reference);

	protected abstract R createPojoReference(Class<? extends Object> clazz);

	private Map<R, O> getMapping() {
		return referenceablePojoMappings.entrySet().stream().filter(e -> e.getValue().used).collect(Collectors.toMap(e -> e.getValue().reference, e -> e.getValue().mapping));
	}

	protected final O getPojoMapping(Class<? extends Object> clazz) {
		return getPojoMapping(clazz, null);
	}
	protected final O getPojoMapping(Class<? extends Object> clazz, Holder<R> reference) {
		if (referenceablePojoMappings.containsKey(clazz)) {
			final ReferenceableMapping<O,R> referenceableMapping = referenceablePojoMappings.get(clazz);
			if (reference != null) {
				reference.set(referenceableMapping.reference);
				referenceableMapping.used = true;
			}
			return referenceableMapping.mapping;
		}
		final O pojoMapping = createPojoMapping();
		final R pojoReference = createPojoReference(clazz);
		referenceablePojoMappings.put(clazz, new ReferenceableMapping<>(pojoReference, pojoMapping));
		convertToPojoMapping(clazz, pojoMapping, pojoReference);
		return pojoMapping;
	}

	public interface FieldFinder {
		class Hints {
			private boolean mandatory;

			public boolean isMandatory() {
				return mandatory;
			}

			public void setMandatory(boolean mandatory) {
				this.mandatory = mandatory;
			}
		}
		void withField(Field field, String label, Hints hints, Object fromParent);
	}

	protected void withAllFields(Class<? extends Object> clazz, FieldFinder fieldFinder) {
		for (Field field : clazz.getDeclaredFields()) {
			final UI.Partaking partaking = field.getAnnotation(UI.Partaking.class);
			if (partaking == null || partaking.value() == UI.FieldPartaking.INCLUDE) {
				final UI.Label label = field.getAnnotation(UI.Label.class);
				final String name = label != null ? label.value() : field.getName();
				final FieldFinder.Hints hints = new FieldFinder.Hints();
				fieldFinder.withField(field, name, hints,null);
			}
		}
	}
}
