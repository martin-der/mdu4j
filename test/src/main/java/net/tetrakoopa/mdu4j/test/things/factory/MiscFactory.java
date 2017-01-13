package net.tetrakoopa.mdu4j.test.things.factory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MiscFactory {

	public static final Date priseDeLaBastille;

	static {
		Calendar calendar;

		calendar = GregorianCalendar.getInstance();
		calendar.set(1789, 6, 14, 12, 00, 00);

		priseDeLaBastille = calendar.getTime();
	}
}
