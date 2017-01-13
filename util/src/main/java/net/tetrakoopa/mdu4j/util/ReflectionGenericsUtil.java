package net.tetrakoopa.mdu4j.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReflectionGenericsUtil {

	private static Class<?> myclass = null;

	private Map<String, Class<?>> genericMap = new HashMap<String, Class<?>>();

	private Map<String, String> reverseIntermediate = new HashMap<String, String>();

	public static ReflectionGenericsUtil forClass(Class<?> concrete) {
		return new ReflectionGenericsUtil(concrete);
	}

	public static ReflectionGenericsUtil forField(Field field) {
		return forGenericType(field.getGenericType());
	}

	public static ReflectionGenericsUtil forReturnType(Method method) {
		return forGenericType(method.getGenericReturnType());
	}

	public static ReflectionGenericsUtil forParameter(Method method, int index) {
		return forGenericType(method.getGenericParameterTypes()[index]);
	}

	public static ReflectionGenericsUtil forGenericType(Type type) {
		if (type instanceof Class) {
			return forClass((Class<?>) type);
		} else if (type instanceof ParameterizedType) {
			return new ReflectionGenericsUtil((ParameterizedType) type);
		} else {
			return forClass(Object.class);
			// throw new MagmaException("Dont know how to build a GenericsUtil
			// out of
			// {0}", type.getClass());
		}
	}

	private ReflectionGenericsUtil(Class<?> concrete) {
		TypeVariable<?>[] parameters = concrete.getTypeParameters();
		// if (parameters.length > 0) throw new MagmaException("Cannot parse
		// {0}, it
		// is a generic class, use a concrete class instead", concrete);
		myclass = concrete;
		recurse(concrete);
		coalesceMap();
	}

	private ReflectionGenericsUtil(ParameterizedType type) {
		myclass = (Class<?>) type.getRawType();
		recurse(myclass, myclass, type);
		coalesceMap();
	}

	private void coalesceMap() {
		int cnt = reverseIntermediate.size();
		while (reverseIntermediate.size() > 0 && cnt > 0) {
			for (Iterator<Map.Entry<String, String>> iterator = this.reverseIntermediate.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, String> entry = iterator.next();
				String target = entry.getValue();
				String search = entry.getKey();
				Class<?> clazz = genericMap.get(search);
				if (clazz == null)
					continue;
				if (genericMap.containsKey(target))
					throw new RuntimeException("Impossible situation, it's a bug : {0} generic is both {1} and bound to {2}" + target + genericMap.get(target)
						+ search);
				genericMap.put(target, clazz);
				iterator.remove();
			}
			cnt--;
		}
		if (reverseIntermediate.size() > 0) {
			for (Iterator<Map.Entry<String, String>> iterator = this.reverseIntermediate.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry<String, String> entry = iterator.next();
				String target = entry.getValue();
				String search = entry.getKey();
				Class<?> clazz = genericMap.get(search);
				if (clazz == null)
					clazz = Object.class;
				if (genericMap.containsKey(target))
					throw new RuntimeException("Impossible situation, it's a bug : {0} generic is both {1} and bound to {2}" + target + genericMap.get(target)
						+ search);
				genericMap.put(target, clazz);
			}
		}
	}

	public Class<?> getBaseClass() {
		return this.myclass;
	}

	private void recurse(Class<?> clazz, Class<?> simplesup, ParameterizedType partype) {
		Type[] typeArguments = partype.getActualTypeArguments();
		TypeVariable<?>[] parameters = simplesup.getTypeParameters();

		for (int i = 0; i < typeArguments.length; i++) {
			if (typeArguments[i] instanceof Class) {
				genericMap.put(simplesup.getName() + "--" + parameters[i].getName(), (Class) typeArguments[i]);
			} else if (typeArguments[i] instanceof TypeVariable) {
				reverseIntermediate.put(clazz.getName() + "--" + ((TypeVariable<?>) typeArguments[i]).getName(),
						simplesup.getName() + "--" + parameters[i].getName());
			}
		}

		recurse(simplesup);
	}

	private void recurse(Class<?> clazz) {
		Type supclass = clazz.getGenericSuperclass();
		Class simplesup = clazz.getSuperclass();
		if (supclass == null)
			return;
		if (supclass instanceof ParameterizedType) {
			recurse(clazz, simplesup, (ParameterizedType) supclass);
		} else {
			recurse(simplesup);
		}
	}

	/**
	 * Return real, "generics dereferenced", parameter types for the given
	 * method.
	 * 
	 * @param method
	 *            The method to analyze
	 * @return The real classes, dereferencing generics
	 */
	public ReflectionGenericsUtil[] getParameterTypes(Method method) {
		Class<?> declaring = method.getDeclaringClass();
		String declname = declaring.getName();

		Type[] parameterTypes = method.getGenericParameterTypes();
		ReflectionGenericsUtil[] ret = new ReflectionGenericsUtil[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			Type type = parameterTypes[i];
			if (type instanceof Class) {
				ret[i] = forClass((Class<?>) type);
			} else if (type instanceof TypeVariable) {
				String name = ((TypeVariable<?>) type).getName();
				Class<?> sub = genericMap.get(declname + "--" + name);
				if (sub == null)
					sub = Object.class;
				ret[i] = forClass(sub);
			} else {
				ret[i] = forGenericType(type);
			}
		}
		return ret;
	}

	public ReflectionGenericsUtil resolveType(Type type) {
		if (type instanceof Class) {
			return forClass((Class<?>) type);
		} else if (type instanceof TypeVariable) {
			GenericDeclaration gd = ((TypeVariable<?>) type).getGenericDeclaration();
			Class<?> acclass = null;
			if (gd instanceof Class) {
				acclass = (Class<?>) gd;
			} else if (gd instanceof Method) {
				acclass = ((Method) gd).getDeclaringClass();
			} else if (gd instanceof Constructor) {
				acclass = ((Constructor<?>) gd).getDeclaringClass();
			}
			String name = ((TypeVariable<?>) type).getName();
			return forClass(genericMap.get(acclass.getName() + "--" + name));
		} else {
			return forGenericType(type);
		}
	}

	/**
	 * Search for all occurrencies of a specific method.
	 * <p>
	 * The type parameters passed in may be Class or null. If they are null,
	 * that means that we don't know which class they should be, if they are a
	 * class, that means we are searching for that class, and the comparison is
	 * made on dereferenced generics.
	 * </p>
	 * <p>
	 * Specifying no parameter types explicitly means "a method without
	 * parameters".
	 * </p>
	 * 
	 * @param name
	 *            The name of the method
	 * @param parameterTypes
	 *            The types of the parameters
	 * @return A list of {@link MethodDef}, ordered with methods from current
	 *         class first, then method from superclass and so on.
	 */
	public List<MethodDef> findMethods(String name, Class<?>... parameterTypes) {
		List<MethodDef> founds = new ArrayList<MethodDef>();
		Class<?> current = myclass;
		while (current != null) {
			Method[] methods = current.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isBridge() && !method.isSynthetic() && method.getName().equals(name)) {
					Type[] types = method.getGenericParameterTypes();
					if (types.length == parameterTypes.length) {
						ReflectionGenericsUtil[] genericClasses = getParameterTypes(method);
						Class<?>[] classes = toRawClasses(genericClasses);
						boolean good = true;
						for (int i = 0; i < types.length; i++) {
							if (parameterTypes[i] != null) {
								if (!classes[i].equals(parameterTypes[i])) {
									good = false;
									break;
								}
							}
						}
						if (good) {
							MethodDef def = new MethodDef(method, genericClasses);
							if (!founds.contains(def))
								founds.add(def);
						}
					}
				}
			}
			current = current.getSuperclass();
		}
		return founds;
	}

	public static Class<?>[] toRawClasses(ReflectionGenericsUtil[] genclasses) {
		Class<?>[] ret = new Class<?>[genclasses.length];
		for (int i = 0; i < genclasses.length; i++) {
			ret[i] = genclasses[i].getBaseClass();
		}
		return ret;
	}

	/**
	 * Search for all methods having that name, no matter which parameter they
	 * take.
	 * 
	 * @param name
	 *            The name of the methods
	 * @return A list of {@link MethodDef}, ordered with methods from current
	 *         class first, then method from superclass and so on.
	 */
	public List<MethodDef> findAllMethods(String name) {
		List<MethodDef> founds = new ArrayList<MethodDef>();
		Class<?> current = myclass;
		while (current != null) {
			Method[] methods = current.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isBridge() && !method.isSynthetic() && method.getName().equals(name)) {
					MethodDef def = new MethodDef(method);
					if (!founds.contains(def))
						founds.add(def);
				}
			}
			current = current.getSuperclass();
		}
		return founds;
	}

	public List<MethodDef> getMethods() {
		List<MethodDef> founds = new ArrayList<MethodDef>();
		Class<?> current = myclass;
		while (current != null) {
			Method[] methods = current.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isBridge() && !method.isSynthetic()) {
					MethodDef def = new MethodDef(method);
					if (!founds.contains(def))
						founds.add(def);
				}
			}
			current = current.getSuperclass();
		}
		return founds;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ReflectionGenericsUtil))
			return false;
		ReflectionGenericsUtil oth = (ReflectionGenericsUtil) obj;
		return this.getBaseClass().equals(oth.getBaseClass());
	}

	public class MethodDef {
		private Method method = null;

		private ReflectionGenericsUtil[] params = null;

		MethodDef(Method m) {
			this.method = m;
		}

		MethodDef(Method m, ReflectionGenericsUtil[] params) {
			this.method = m;
			this.params = params;
		}

		public String getName() {
			return this.method.getName();
		}

		public Method getBaseMethod() {
			return this.method;
		}

		public ReflectionGenericsUtil[] getParameterTypes() {
			if (this.params == null) {
				this.params = ReflectionGenericsUtil.this.getParameterTypes(method);
			}
			return this.params;
		}

		public ReflectionGenericsUtil getReturnType() {
			return resolveType(method.getGenericReturnType());
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof MethodDef))
				return false;
			MethodDef oth = (MethodDef) other;
			return (method.getName().equals(oth.method.getName())) && (Arrays.equals(getParameterTypes(), oth.getParameterTypes()));
		}

		public Class<?> getDeclaringClass() {
			return this.method.getDeclaringClass();
		}
	}

}
