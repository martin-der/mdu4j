package net.tetrakoopa.mdu4j.util.sel;

import java.util.ArrayList;
import java.util.List;

public interface SimpleELToStringConverter<T> {

	String convert(T object);

	public static class KeyedConverter<T, C extends SimpleELToStringConverter<T>> {
		private Class<?> key;
		private C converter;

		public KeyedConverter(Class<?> key, C converter) {
			this.key = key;
			this.converter = converter;
		}

		public Class<?> getKey() {
			return key;
		}

		public void setKey(Class<?> key) {
			this.key = key;
		}

		public C getConverter() {
			return converter;
		}

		public void setConverter(C converter) {
			this.converter = converter;
		}

	}

	public static class KeyedConverterHolder {
		private List<KeyedConverter<?, ?>> converters = new ArrayList<KeyedConverter<?, ?>>();
		
		public <T, C extends SimpleELToStringConverter<T>> void add(Class<T> key, C converter) {
			converters.add(new KeyedConverter<T, C>(key, converter));
		}

		public <T, C extends SimpleELToStringConverter<T>> SimpleELToStringConverter<T> find(Class<T> key) {
			for (KeyedConverter<?, ?> kc : converters) {
				if (kc.key.equals(key))
					return (SimpleELToStringConverter<T>) kc.converter;
			}
			return null;
		}

		public void clear() {
			converters.clear();
		}
	}
	
}
