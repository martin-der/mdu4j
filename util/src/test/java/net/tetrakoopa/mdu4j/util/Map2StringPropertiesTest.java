package net.tetrakoopa.mdu4j.util;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class Map2StringPropertiesTest {

	public enum Activity {
		BOARDING_ANOTHER_SHIP("bas"), CREW_IS_EATING("cie"), CREW_IS_DRINKING("cid");

		final String codeName;

		Activity (String name) {
			this.codeName = name;
		}
	}

	static final Map<String, String> shipProperties = new HashMap<>();
	static {
		shipProperties.put("captain.name", "Robert");
		shipProperties.put("captain.age", "54");
		shipProperties.put("equipage.members.count", "150");
		shipProperties.put("crew.mutiny-in-progress", "true");
		shipProperties.put("ship.current-activity", Activity.CREW_IS_EATING.name());
	}

	@Test
	public void getString() {
		String name = Map2StringProperties.getString("captain.name", shipProperties);
		assertEquals("Robert", name);
	}
	@Test
	public void getDefaultString() {
		String forname = Map2StringProperties.getString("captain.forname", shipProperties, "J.L.");
		assertEquals("J.L.", forname);
	}

	@Test
	public void getInteger() {
		int age = Map2StringProperties.getInteger("captain.age", shipProperties);
		assertEquals(54, age);
	}
	@Test
	public void getDefaultInteger() {
		int weightInTonne = Map2StringProperties.getInteger("ship.weight", shipProperties, 300);
		assertEquals(300, weightInTonne);
	}
	@Test(expectedExceptions = Map2StringProperties.BadTypeException.class)
	public void failToGetInteger() {
		Map2StringProperties.getInteger("captain.name", shipProperties);
	}

	@Test
	public void getLong() {
		long age = Map2StringProperties.getLong("captain.age", shipProperties);
		assertEquals(54, age);
	}
	@Test
	public void getDefaultLong() {
		long weightInTonne = Map2StringProperties.getLong("ship.weight", shipProperties, 300);
		assertEquals(300, weightInTonne);
	}
	@Test(expectedExceptions = Map2StringProperties.BadTypeException.class)
	public void failToGetLong() {
		Map2StringProperties.getLong("captain.name", shipProperties);
	}

	@Test
	public void getBoolean() {
		boolean mutinyInProgress = Map2StringProperties.getBoolean("crew.mutiny-in-progress", shipProperties);
		assertEquals(true, mutinyInProgress);
	}
	@Test
	public void getDefaultBoolean() {
		boolean drunk = Map2StringProperties.getBoolean("crew.drunk", shipProperties, false);
		assertEquals(false, drunk);
	}
	@Test(expectedExceptions = Map2StringProperties.BadTypeException.class)
	public void failToGetBoolean() {
		Map2StringProperties.getBoolean("captain.name", shipProperties);
	}

	@Test
	public void getEnumeration() {
		Activity activity = Map2StringProperties.getEnumeration(Activity.class, "ship.current-activity", shipProperties);
		assertEquals(Activity.CREW_IS_EATING, activity);
	}
	@Test(expectedExceptions = Map2StringProperties.BadTypeException.class)
	public void failToGetEnumeration() {
		Map2StringProperties.getEnumeration(Activity.class, "captain.name", shipProperties);
	}
}
