package net.tetrakoopa.mdu4j.view;


import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractBuilder<CONTEXT> implements UIAttribute {
	
	/** Type de donnée => influe sur le Contrôle généré */
	public enum Type {
		LABEL,
		TEXT,
		NUMBER,
		DATE,
		BOOLEAN,
		OBJECT,
		ONE_IN_MANY,
		MANY_IN_MANY,
		ARRAY,
	}
	
	/** Type Spécific view choice */
	public static class ElementPresentation<W> {
		/** How to display a boolean input */
		public static class Boolean<W>
		extends ElementPresentation<W> {
			public enum Widget {
				CHECK_BOX,
				DROP_DOWN;
			}
			public final W widget;
			
			public Boolean(final W widget) {
				this.widget = widget;
			}
		}
		
		/** How to display a OneAmongstMany input */
		public enum OneAmongstMany {
			RADIO_BUTTONS,
			DROP_DOWN;
		}
	}

	protected abstract void buildForField(CONTEXT context, ViewElement element);

	public static class ViewElement {

		public static class InternalReference {
			final Field field;
			final int index;
			final String key;

			InternalReference(Field field) {
				this.field = field;
				this.index = 0;
				this.key = null;
			}
			InternalReference(int index) {
				this.field = null;
				this.index = index;
				this.key = null;
			}
			InternalReference(String key) {
				this.field = null;
				this.index = 0;
				this.key = key;
			}
			@Override
			public String toString() {
				if (field != null)
					return field.getName();
				if (key != null)
					return key;
				return String.valueOf(index);
			}
		}

		final InternalReference internalReference;

		public final Class<?> clazz;
		public final ViewElement parent;

		public final String label;
		public final Type type;

		public final Object value;

		public ViewElement(InternalReference internalReference, String label, Type type, Class<?> clazz, Object value, ViewElement parent) {
			this.internalReference = internalReference;
			this.label = label;
			this.clazz = clazz;
			this.type = type;
			this.value = value;
			this.parent = parent;
		}

	}

	private static class BuilderContext {
	}

	private <BEAN> void build(final BuilderContext builderContext, final List<ViewElement> elements, Class<?> clazz, ViewElement.InternalReference internalReference, final ViewElement parent,final BEAN bean) {

		final Type type = infereType(clazz);

		final String label;
		if (internalReference.field != null) {
			final Label labelAnnotation = internalReference.field.getAnnotation(Label.class);
			label = labelAnnotation == null ? internalReference.field.getName() : labelAnnotation.value();
		} else if (internalReference.key != null) {
			label = internalReference.key;
		} else {
			label = String.valueOf(internalReference.index);
		}

		final Object value;
		if (type == Type.ARRAY) {
			value = Array.getLength(bean);
		} else if (type == Type.OBJECT ) {
			value = clazz.getName();
		} else {
			value = bean;
		}
		final ViewElement element = new ViewElement(internalReference, label, type, clazz, value, parent);

		elements.add(element);

		if (bean == null)
			return;
		if (type == Type.ARRAY) {
			final int length = Array.getLength(bean);
			for (int index = 0; index < length; index ++) {
				final Object arrayElement = Array.get(bean, index);
				build(builderContext, elements, clazz.getComponentType(), new ViewElement.InternalReference(index), element, arrayElement);
			}
			return;
		}

		if (type == Type.OBJECT) {
			final PojoPolicy policy =  clazz.getAnnotation(PojoPolicy.class);
			final boolean partakeByDefault = policy == null || (policy.fieldsPartaking()==FieldPartaking.INCLUDE);
			ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					ReflectionUtils.makeAccessible(field);
					final Partaking partaking = field.getAnnotation(Partaking.class);
					if (!partakeByDefault) {
						if(partaking == null || partaking.value() == FieldPartaking.EXCLUDE) {
							return;
						}
					} else {
						if (partaking != null && partaking.value() == FieldPartaking.EXCLUDE) {
							return;
						}
					}
					build(builderContext, elements, field.getType(), new ViewElement.InternalReference(field), element, field.get(bean));
				}
			});
			return;
		}
	}

	private Type infereType(Class<?> clazz) {
		if (clazz.isArray())
			return Type.ARRAY;
		if (clazz.equals(String.class))
			return Type.TEXT;
		if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE) || clazz.equals(Long.class) || clazz.equals(Long.TYPE))
			return Type.NUMBER;
		if (clazz.equals(Float.class) || clazz.equals(Float.TYPE) || clazz.equals(Double.class) || clazz.equals(Double.TYPE))
			return Type.NUMBER;
		if (clazz.equals(Date.class) || clazz.equals(Calendar.class) )
			return Type.DATE;
		return Type.OBJECT;
	}

	public <BEAN> void build(final CONTEXT context, final BEAN bean) {

		final List<ViewElement> elements = new ArrayList<>();

		final BuilderContext builderContext = new BuilderContext();

		final Class<?> clazz = bean.getClass();
 		final Type type = infereType(clazz);

		if (type != Type.OBJECT) {
			throw new IllegalArgumentException("Argument 'bean' must be a POJO ( provided '"+bean.getClass().getName()+"' )");
		}


		ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtils.makeAccessible(field);
				build(builderContext, elements, field.getType(), new ViewElement.InternalReference(field),null,field.get(bean));
			}
		});

		for (ViewElement element : elements) {
			buildForField(context, element);
		}
	}
	
}
