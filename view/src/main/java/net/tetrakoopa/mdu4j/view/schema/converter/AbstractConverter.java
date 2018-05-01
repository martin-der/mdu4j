package net.tetrakoopa.mdu4j.view.schema.converter;

import net.tetrakoopa.mdu4j.util.Holder;
import net.tetrakoopa.mdu4j.view.UI;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @param <S> The schema
 * @param <P> An primitive in the schema representation
 * @param <O> An object in the schema representation
 * @param <R> Reference class to an known object
 */
public abstract class AbstractConverter<S, P, O, R> {

	private static final Map<String, FieldSortingInformer.OrderedField> NO_ORDERED_FIELD = Collections.unmodifiableMap(new HashMap<>());

	public enum PrimitiveType {
		STRING, INTEGER, NUMBER, BOOLEAN
	}

	public static class Context<R, O> {

		public static class Parent {

			public enum Type {
				ARRAY, POJO
			}

			private final Context context;

			private final Object object;
			private final Type type;

			public Parent(Context context, Object object, Type type) {
				this.context = context;
				this.object = object;
				this.type = type;
			}

			public Object getObject() {
				return object;
			}

			public Type getType() {
				return type;
			}
		}

		private final AbstractConverter converter;

		private String label;

		private Parent parent;

		private Context(Context parent, Parent.Type parentType) {
			checkParentContextParameter(parent);
			this.converter = parent.converter;
			this.parent = new Parent(parent, null, parentType);
		}
		private Context(AbstractConverter converter) {
			this.converter = converter;
			this.parent = null;
		}

		public Map<R, O> getMapping() {
			return converter.getMapping();
		}

		public Context getParentContext() {
			return parent == null ? null : parent.context;
		}
		public Parent.Type getParentType() {
			return parent == null ? null : parent.type;
		}

		public String getLabel() {
			return label;
		}
	}

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
		final Context<R,O> context = new Context<>(this);
		final UI.Label labelAnnotation = clazz.getAnnotation(UI.Label.class);
		if (labelAnnotation != null) context.label = labelAnnotation.value();
		final S schema = createSchema();
		convertToSchema(context, clazz, schema);
		return schema;
	}

	protected abstract S createSchema();

	protected abstract void convertToSchema(Context context, Class<? extends Object> clazz, S schema);

	protected abstract P createPrimitiveMapping(Context.Parent context, PrimitiveType primitiveType);

	protected abstract void convertToPrimitiveMapping(Context context, Class<? extends Object> clazz, P primitiveMapping);

	protected abstract O createPojoMapping();

	protected abstract void convertToPojoMapping(Context context, Class<? extends Object> clazz, O pojoMapping, R reference);

	protected abstract R createReference(Class<? extends Object> clazz);

	private Map<R, O> getMapping() {
		return referenceablePojoMappings.entrySet().stream()/*.filter(e -> e.getValue().used)*/.collect(Collectors.toMap(e -> e.getValue().reference, e -> e.getValue().mapping));
	}

	protected P getPrimitiveMapping(Context context, Class<?> type) {
		if (!isPrimitiveType(type)) throw new IllegalArgumentException("Class '"+type.getName()+"' cannot be mapped to primitive type");
		final PrimitiveType primitiveType = getPrimitiveType(type);
		final P mapping = createPrimitiveMapping(context.parent, primitiveType);
		convertToPrimitiveMapping(context, type, mapping);
		return mapping;
	}

	protected final O getPojoMapping(Context parent, Class<? extends Object> clazz) {
		return getPojoMapping(parent, clazz, null);
	}
	protected final O getPojoMapping(Context parent, Class<? extends Object> clazz, Holder<R> reference) {
		final Context<R,O> context = new Context<>(parent, Context.Parent.Type.POJO);
		final UI.Label labelAnnotation = clazz.getAnnotation(UI.Label.class);
		if (labelAnnotation != null) context.label = labelAnnotation.value();
		final O pojoMapping = createPojoMapping();
		if (referenceablePojoMappings.containsKey(clazz)) {
			final ReferenceableMapping<O,R> referenceableMapping = referenceablePojoMappings.get(clazz);
			if (reference != null) {
				reference.set(referenceableMapping.reference);
				referenceableMapping.used = true;
			}
			convertToPojoMapping(context, clazz, pojoMapping, referenceableMapping.reference);
			return pojoMapping;
		}
		final R pojoReference = createReference(clazz);
		referenceablePojoMappings.put(clazz, new ReferenceableMapping<>(pojoReference, pojoMapping));
		convertToPojoMapping(context, clazz, pojoMapping, null);
		return pojoMapping;
	}
	protected final boolean isPrimitiveType(Class<?> type) {
		return getPrimitiveType(type) != null;
	}

	/** @return <code>null</code> is the class cannot be mapped to a primitive type */
	protected PrimitiveType getPrimitiveType(Class<?> type) {
		if (type.equals(String.class)) {
			return PrimitiveType.STRING;
		}

		if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
			return PrimitiveType.INTEGER;
		}
		if (type.equals(Long.class) || type.equals(Long.TYPE)) {
			return PrimitiveType.INTEGER;
		}
		if (type.equals(BigInteger.class)) {
			return PrimitiveType.INTEGER;
		}

		if (type.equals(Float.class) || type.equals(Float.TYPE)) {
			return PrimitiveType.NUMBER;
		}
		if (type.equals(Double.class) || type.equals(Double.TYPE)) {
			return PrimitiveType.NUMBER;
		}
		if (type.equals(BigDecimal.class)) {
			return PrimitiveType.NUMBER;
		}

		if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
			return PrimitiveType.BOOLEAN;
		}
		return null;
	}

	public interface FieldFinder {
		class Hints {

			private boolean mandatory;

			private Integer order;

			public boolean isMandatory() {
				return mandatory;
			}

			public Integer getOrder() {
				return order;
			}
		}
		void withField(Context context, Field field, String name, Hints hints, Object fromParent);
	}
	public interface FieldSortingInformer {
		class OrderedField {

			private final Field field;
			private final int order;

			public OrderedField(Field field, int order) {
				this.field = field;
				this.order = order;
			}

			public Field getField() {
				return field;
			}

			public int getOrder() {
				return order;
			}
		}
		void withSortedFields(Map<String, OrderedField> sortedFields);
	}

	private final static Comparator<Map.Entry<Field, Integer>> fieldOrderComparator = new Comparator<Map.Entry<Field, Integer>>() {
		@Override
		public int compare(Map.Entry<Field, Integer> fia, Map.Entry<Field, Integer> fib) {
			return fia.getValue().compareTo(fib.getValue());
		}
	};

	protected void withAllFields(Context parentContext, Class<? extends Object> type, FieldFinder fieldFinder) {
		withAllFields(parentContext, type, fieldFinder,null);
	}

	/**
	 * If both <code>FieldFinder fieldFinder</code> and <code></code>FieldSortingInformer fieldSortingInformer</code>
	 * and given <code>fieldSortingInformer</code> is called first.
	 * @param fieldSortingInformer can be <code>null</code>
	 */
	protected void withAllFields(Context parentContext, Class<? extends Object> type, FieldFinder fieldFinder, FieldSortingInformer fieldSortingInformer) {
		checkParentContextParameter(parentContext);

		final List<Field> fields = new ArrayList<>();
		for (Field field : type.getDeclaredFields()) {
			final UI.Partaking partaking = field.getAnnotation(UI.Partaking.class);
			if (partaking == null || partaking.value() == UI.FieldPartaking.INCLUDE) {
				fields.add(field);
			}
		}


		final Map<String, FieldSortingInformer.OrderedField> orderedField;
		int fieldIndex = 0;
		final List<Field> sortedFields = getSortedField(fields);
		if (sortedFields!=null) {
			final Map<String, FieldSortingInformer.OrderedField> tmpSortedFields = new HashMap<>();
			for (Field sortedField : sortedFields) {
				tmpSortedFields.put(getFieldName(sortedField), new FieldSortingInformer.OrderedField(sortedField, fieldIndex));
				fieldIndex++;
			}
			orderedField = Collections.unmodifiableMap(tmpSortedFields);
		} else {
			orderedField = NO_ORDERED_FIELD;
		}
		if (fieldSortingInformer != null) {
			fieldSortingInformer.withSortedFields(orderedField);
		}

		for (Field field : fields) {
			final UI.Label labelAnnotation = field.getAnnotation(UI.Label.class);
			final String label = labelAnnotation != null ? labelAnnotation.value() : null;
			final String name = getFieldName(field);
			final FieldFinder.Hints hints = new FieldFinder.Hints();
			if (orderedField != null) {
				if (orderedField.containsValue(name))
					hints.order = orderedField.get(name).order;
			}
			fieldFinder.withField(createContext(parentContext, Context.Parent.Type.POJO, field), field, name, hints,null);
		}
	}

	private Context createContext(Context parent, Context.Parent.Type parentType, AccessibleObject accessibleObject) {
		final Context<R,O> context = new Context<>(parent, parentType);
		final UI.Label labelAnnotation = accessibleObject.getAnnotation(UI.Label.class);
		if (labelAnnotation != null) context.label = labelAnnotation.value();
		return context;
	}

	/** @return <code>null</code> if not order was specified */
	private Integer getFieldOrder(Field field) {
		final UI.Order order = field.getAnnotation(UI.Order.class);
		if (order != null) {
			return order.value();
		}
		return null;
	}
	private String getFieldName(Field field) {
		return field.getName();
	}

	/** @return <code>null</code> if there where no field with order information */
	private List<Field> getSortedField(List<Field> fields) {
		Map<Field, Integer> orders = null;
		for (Field field : fields) {
			final Integer order = getFieldOrder(field);
			if (order != null) {
				if (orders == null) orders = new HashMap<>();
				orders.put(field, order);
			}
		}

		if (orders == null) return null;
		return new ArrayList<>(orders.entrySet().stream().sorted(fieldOrderComparator).map(e -> e.getKey()).collect(Collectors.toList()));
	}

	private static void checkParentContextParameter(Context parentContext) {
		if (parentContext == null) throw new NullPointerException("Parent context must not be null");
	}

}
