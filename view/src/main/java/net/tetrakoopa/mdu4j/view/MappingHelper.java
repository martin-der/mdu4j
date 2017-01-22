package net.tetrakoopa.mdu4j.view;

import net.tetrakoopa.mdu4j.util.StringUtil;

import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MappingHelper<BEAN, VIEW> implements UIAttribute {

	protected static class AttributeMapping<VIEW> {

		public final Field readerWriter;
		public final Method getter;
		public final Method setter;
		public VIEW view;

		private AttributeMapping(Field readerWriter, Method getter, Method setter) {
			this.readerWriter = readerWriter;
			this.getter = getter;
			this.setter = setter;
		}

	}

	public static class MappingException extends RuntimeException {
		public MappingException(String message) {
			super(message);
		}
		public MappingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	protected final Class<BEAN> beanClass;
	protected final Class<VIEW> viewClass;
	protected final Map<String, AttributeMapping> mappings = new HashMap<>();

	public MappingHelper() {
		final Class<?> genericArguments[] = GenericTypeResolver.resolveTypeArguments(this.getClass(),MappingHelper.class);
		this.beanClass = (Class<BEAN>) genericArguments[0];
		this.viewClass = (Class<VIEW>) genericArguments[1];
		scanUIELements();
	}

	private void scanUIELements() {
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.getAnnotation(Element.class) != null) {
				addAttribute(field.getName());
			}
		}
	}

	public void addAttribute(String name) {
		addAttribute(name, true);
	}
	public void addAttribute(String name, boolean writable) {
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.getName().equals(name)) {

				final Class<?> attributeType = field.getType();

				final int modifiers = field.getModifiers();
				if (Modifier.isPublic(modifiers)) {
					if (writable) {
						if (Modifier.isFinal(modifiers)) {
							throw new MappingException(faultingAttribute(name)+" won't be settable since its public field is final");
						}
					}
					mappings.put(name, new AttributeMapping(field, null, null));
					return;
				}

				final boolean isPrimitiveBoolean = boolean.class.equals(attributeType);
				final String gettterName = getterName(name, isPrimitiveBoolean);
				final String settterName = writable ? setterName(name) : null;
				Method getter = null;
				Method setter = null;

				for (Method method : beanClass.getDeclaredMethods()) {
					final int methodModifiers = method.getModifiers();
					if (method.getName().equals(gettterName)
							&& (method.getParameterTypes().length==0)
							&& method.getReturnType().equals(attributeType))
					{
						if (!Modifier.isPublic(methodModifiers)) {
							throw new MappingException(faultingAttribute(name)+" have a getter ( '"+gettterName+"' ) but is not public");
						}
						getter = method;
					}

					if (settterName != null) {
						if (method.getName().equals(settterName)
								&& (method.getParameterTypes().length==1)
								&& (method.getParameterTypes()[0].equals(attributeType))
								&& method.getReturnType().equals(Void.TYPE))
						{
							if (!Modifier.isPublic(methodModifiers)) {
								throw new MappingException(faultingAttribute(name)+" have a setter ( '"+settterName+"' ) but is not public");
							}
							setter = method;
						}
					}
				}

				if (getter != null && (setter != null || !writable)) {
					mappings.put(name, new AttributeMapping(field, getter, setter));
					return;
				}

				if (writable)
					throw new MappingException(faultingAttribute(name)+" does not have getter and setter ( '"+gettterName+"' and '"+settterName+"' )");
				else
					throw new MappingException(faultingAttribute(name)+" does not have getter ( '"+gettterName+"' )");
			}
		}

		throw new MappingException(faultingAttribute(name)+" does not exist");
	}

	public VIEW getView(String name) {
		final AttributeMapping<VIEW> mapping = mappings.get(name);
		if (mapping == null)
			throw buildNoSuchAttributeException(name);
		return mapping.view;
	}
	public void setView(String name, VIEW view) {
		final AttributeMapping<VIEW> mapping = mappings.get(name);
		if (mapping == null)
			throw buildNoSuchAttributeException(name);
		mapping.view = view;
	}

	public Field getField(String name) {
		final AttributeMapping mapping = mappings.get(name);
		if (mapping == null)
			throw buildNoSuchAttributeException(name);
		return mapping.readerWriter;
	}
	public void set(String name, BEAN bean, Object value) {
		final AttributeMapping mapping = mappings.get(name);
		if (mapping == null)
			throw buildNoSuchAttributeException(name);
		set(mapping, bean, value);
	}
	protected void set(AttributeMapping mapping, BEAN bean, Object value) {
		try {
			if (mapping.setter != null) {
				mapping.setter.invoke(bean, value);
			} else {
				mapping.readerWriter.set(bean, value);
			}
		} catch (IllegalAccessException iaex) {
			throw  new MappingException(iaex.getMessage(), iaex);
		} catch (InvocationTargetException itex) {
			throw  new MappingException(itex.getMessage(), itex);
		}
	}
	public Object get(String name, BEAN bean) {
		final AttributeMapping mapping = mappings.get(name);
		if (mapping == null)
			throw buildNoSuchAttributeException(name);
			return get(mapping, bean);
	}
	protected Object get(AttributeMapping mapping, BEAN bean) {
		try {
			if (mapping.getter != null) {
				return mapping.getter.invoke(bean);
			} else {
				return mapping.readerWriter.get(bean);
			}
		} catch (IllegalAccessException iaex) {
			throw  new MappingException(iaex.getMessage(), iaex);
		} catch (InvocationTargetException itex) {
			throw  new MappingException(itex.getMessage(), itex);
		}
	}


	private static String setterName(String attribute) {
		return "set"+ StringUtil.firstCharToUpperCase(attribute);
	}
	private static String getterName(String attribute, boolean forBoolean) {
		return (forBoolean ? "is" : "get")+ StringUtil.firstCharToUpperCase(attribute);
	}

	private final MappingException buildNoSuchAttributeException(String name) {
		return new MappingException("No such mapped attribute '"+name+"' in "+beanClass.getName());
	}
	private final String faultingAttribute(String name) {
		return "Attribute '"+name+"' in "+beanClass.getName();
	}

	protected RuntimeException buildNoInformationException(String attribute) {
		throw new RuntimeException("Attribute '"+attribute+"' in "+beanClass+" has no mapping configuration");
	}


}
