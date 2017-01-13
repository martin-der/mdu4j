package net.tetrakoopa.mdu4j.util.sel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleELConverters {

	public static class DateConverter implements SimpleELToStringConverter<java.util.Date> {
		
		private final SimpleDateFormat format;

		public DateConverter(String simpleDateFormat) {
			this.format = new SimpleDateFormat(simpleDateFormat);
		}

		@Override
		public String convert(Date object) {
			return format.format(object);
		}

		public SimpleDateFormat getFormat() {
			return format;
		}
	}

}
