package net.tetrakoopa.mdu4j.view;


import net.tetrakoopa.mdu4j.util.ReflectionUtil;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe parente des classes servant de monteur pour une vue<br/>
 */
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
		MANY_IN_MANY
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

		public final Field field;

		public final Class<?> clazz;
		public final ViewElement parent;

		public final String label;
		public final Type type;

		public ViewElement(Field field, String label, Type type, Class<?> clazz, ViewElement parent) {
			this.field = field;
			this.label = label;
			this.type = type;
			this.clazz = clazz;
			this.parent = parent;
		}
	}

	private static class BuilderContext {
	}

	private <BEAN> void build(final BuilderContext builderContext, final CONTEXT context, final List<ViewElement> elements, final ViewElement parent,final BEAN bean) {
		final Class<?> clazz = bean.getClass();
		final PojoPolicy policy =  clazz.getAnnotation(PojoPolicy.class);

		final boolean partakeByDefault = policy == null || (policy.fieldsPartaking()==FieldPartaking.INCLUDE);

		ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
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
				final Label labelAnnotation = field.getAnnotation(Label.class);
				final String label = labelAnnotation == null ? field.getName() : labelAnnotation.value();
				final Type type = infereType(field);
				final ViewElement element = new ViewElement(field, label, type, clazz, parent);
				if (type == Type.OBJECT) {
					ReflectionUtils.makeAccessible(field);
					build(builderContext, context, elements, element, field.get(bean));
				} else {
					elements.add(element);
				}
			}
		});
	}

	private Type infereType(Field field) {
		final Class<?> fieldType = field.getType();
		if (fieldType.equals(String.class))
			return Type.TEXT;
		if (fieldType.equals(Integer.class) || fieldType.equals(Long.class))
			return Type.NUMBER;
		if (fieldType.equals(Float.class) || fieldType.equals(Double.class))
			return Type.NUMBER;
		return Type.OBJECT;
	}

	public <BEAN> void build(final CONTEXT context, final BEAN bean) {

		final List<ViewElement> elements = new ArrayList<>();

		final BuilderContext builderContext = new BuilderContext();

		build(builderContext, context, elements, null, bean);

		for (ViewElement element : elements) {
			buildForField(context, element);
		}
	}
	
}
