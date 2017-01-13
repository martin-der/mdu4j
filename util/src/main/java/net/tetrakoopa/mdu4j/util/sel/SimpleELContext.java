package net.tetrakoopa.mdu4j.util.sel;

import java.util.HashMap;
import java.util.Map;


public class SimpleELContext {
	
	private final Map<String, Object> objects = new HashMap<String, Object>();
	
	private final SimpleELToStringConverter.KeyedConverterHolder converters = new SimpleELToStringConverter.KeyedConverterHolder();


	public Map<String, Object> getObjects() {
		return objects;
	}

	public <T, C extends SimpleELToStringConverter<T>> void addConverter(Class<T> key, C converter) {
		converters.add(key, converter);
	}

	public void clearConverters() {
		converters.clear();
	}

	public <T> String convertToView(T object) {
		if (object == null)
			return null;
		@SuppressWarnings("unchecked")
		SimpleELToStringConverter<T> converter = converters.find((Class<T>) object.getClass());
		if (converter != null) {
			return converter.convert(object);
		}
		return object.toString();
	}
}
