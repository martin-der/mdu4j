package net.tetrakoopa.mdu4j.view;


import net.tetrakoopa.mdu4j.test.things.Foobar;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

@Ignore
public class MappingHelperTest {


	@Test(expected = MappingHelper.MappingException.class)
	public void tryAddingNonExistingAttribute() throws InvocationTargetException, IllegalAccessException {
		final MappingHelper mappingHelper = new MappingHelper();
		mappingHelper.addAttribute("metaFoobar");
	}

	@Test
	public void testSomeFoobarModifications() throws InvocationTargetException, IllegalAccessException {
		final Foobar foobar = new Foobar();

		final MappingHelper mappingHelper = createHelper();

		foobar.setId(42);
		assertEquals(42, mappingHelper.get("id", foobar));

		foobar.setReady(true);
		assertEquals(true, mappingHelper.get("ready", foobar));

		foobar.setName("Abcdef");
		assertEquals("Abcdef", mappingHelper.get("name", foobar));

		foobar.setName(null);
		assertEquals(null, mappingHelper.get("name", foobar));

		mappingHelper.set("ready", foobar, false);
		assertEquals(false, foobar.isReady());
	}

	@Test(expected = MappingHelper.MappingException.class)
	public void tryGettingNonExistingAttribute() throws InvocationTargetException, IllegalAccessException {
		final Foobar foobar = new Foobar();

		final MappingHelper mappingHelper = createHelper();

		Object value = mappingHelper.get("metaFoobar", foobar);
	}

	@Test(expected = MappingHelper.MappingException.class)
	public void trySettingNonExistingAttribute() throws InvocationTargetException, IllegalAccessException {
		final Foobar foobar = new Foobar();

		final MappingHelper mappingHelper = createHelper();

		mappingHelper.set("metaFoobar", foobar, false);
	}

	@Test
	public void regularPrimitiveBooleanAccess() throws InvocationTargetException, IllegalAccessException {
		final Foobar foobar = new Foobar();

		final MappingHelper mappingHelper = createHelper();

	}

	private MappingHelper createHelper() {
		final MappingHelper helper = new MappingHelper();
		helper.addAttribute("name");
		helper.addAttribute("size");
		helper.addAttribute("ready");
		helper.addAttribute("id");
		return helper;
	}
}
