package net.tetrakoopa.mdu4j.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {

	/** works also with AnonymousClass */
	public static String getClassName(Class<?> clazz) {
		if (clazz.isAnonymousClass()) {
			clazz.getEnclosingClass().getName();
		}
		return clazz.getName();
	}

	public static <A extends Annotation> Field findFirstAnnotatedFieldIfAny(Class<A> annotationClass, Class<?> clazz) {
		return findFirstFieldIfAny(annotationClass, clazz, true);
	}

	public static <A extends Annotation> Field findFirstFieldIfAny(Class<A> annotationClass, Class<?> clazz, final boolean searchParent) {
		do {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.getAnnotation(annotationClass) != null)
					return field;
			}
			clazz = clazz.getSuperclass();
		} while (searchParent && clazz != null);
		return null;
	}

	public static <A extends Annotation> A findFirstIfAny(Class<A> annotationClazz, Class<?> clazz) {
		A annotation;
		while ((annotation = clazz.getAnnotation(annotationClazz)) == null) {
			clazz = clazz.getSuperclass();
			if (clazz == null)
				return null;
		}

		return annotation;
	}

	/**
	 * Override this class in order to provide a small factory utility for the
	 * object<br/>
	 * Default implemention try to invoke the constructor with no argument
	 * 
	 * @param clazz
	 *            the clazz we need a instance of
	 * @return <MDL> Model used within the crud
	 */
	public static <T> T intanciateObject(final Class<T> clazz, Object... constructorArguments) {
		return intanciateObjectWithArguments(clazz, constructorArguments);
	}

	private static <T> T intanciateObjectWithArguments(final Class<T> clazz, Object constructorArguments[]) {
		try {
			return clazz.getConstructor().newInstance(constructorArguments);
		} catch (final NoSuchMethodException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " does not have an default constructor");
		} catch (final SecurityException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " does not have an usable default constructor");
		} catch (final InstantiationException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " have a faulting default constructor");
		} catch (final IllegalAccessException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " does not have an usable default constructor");
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " does not have an usable default constructor");
		} catch (final InvocationTargetException e) {
			throw new IllegalArgumentException("Class " + clazz.getName() + " does not have an usable default constructor");
		}
	}

	@SuppressWarnings("unchecked")
	public final static Class<?> getGenericClass(Object object, int pos) {

		final Type thisType = object.getClass().getGenericSuperclass();
		final Type type;

		if (thisType instanceof ParameterizedType) {
			type = ((ParameterizedType) thisType).getActualTypeArguments()[pos];
		} else if (thisType instanceof Class) {
			type = ((ParameterizedType) object.getClass().getGenericSuperclass()).getActualTypeArguments()[pos];
			// type = ((ParameterizedType ) ((Class )
			// thisType).getGenericSuperclass()).getActualTypeArguments()[pos];
		} else {
			return null;
			// throw new
			// IllegalArgumentException("Problem handling type construction for "
			// + getClass());
		}

		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			return null;
			// throw new
			// IllegalArgumentException("Problem determining the class of the generic for "
			// + getClass());
		}

	}

	public static Field getField(Class<?> clazz, String attributName) {

		try {
			return clazz.getDeclaredField(attributName);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static <T> T get(Object object, String attributName) {
		final Class<?> clazz = object.getClass();

		try {
			final Field field = clazz.getDeclaredField(attributName);
			if (field==null) {
				return getViaGet(object, attributName);
			}
			field.setAccessible(true);
			return (T) field.get(object);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static <T> T getViaGet(Object object, String attributName) {
		final Class<?> clazz = object.getClass();
		String methodeName = getterizeName(attributName);
		try {
			Method method = clazz.getDeclaredMethod(methodeName);
			return (T) method.invoke(object);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public static String getterizeName(String attributeName) {
		String firstLetter = String.valueOf(attributeName.charAt(0)).toUpperCase();
		String endOfname = attributeName.substring(1);
		return "get" + firstLetter + endOfname;
	}

	public static String setterizeName(String attributeName) {
		String firstLetter = String.valueOf(attributeName.charAt(0)).toUpperCase();
		String endOfname = attributeName.substring(1);
		return "set" + firstLetter + endOfname;
	}

}
