package net.tetrakoopa.mdu4j.util.test.service.sel;

import java.util.Date;
import java.util.Map;

import net.tetrakoopa.mdu4j.test.things.Bidule;
import net.tetrakoopa.mdu4j.test.things.Truc;
import net.tetrakoopa.mdu4j.test.things.factory.MiscFactory;
import net.tetrakoopa.mdu4j.util.sel.SimpleEL;
import net.tetrakoopa.mdu4j.util.sel.SimpleELContext;
import net.tetrakoopa.mdu4j.util.sel.SimpleELConverters;
import net.tetrakoopa.mdu4j.util.sel.exception.ELNullPointerException;
import net.tetrakoopa.mdu4j.util.sel.exception.ELSyntaxException;
import net.tetrakoopa.mdu4j.util.sel.exception.SimpleELException;
import net.tetrakoopa.mdu4j.util.sel.exception.UnknownAttributException;
import net.tetrakoopa.mdu4j.util.sel.exception.UnknownObjectException;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SimpleELTest {

	SimpleELContext context;

	Map<String, Object> objects;

	Truc truc;

	

	@BeforeTest
	public void init() {


		context = new SimpleELContext();
		context.addConverter(Date.class, new SimpleELConverters.DateConverter("dd/MM/yyyy"));


		truc = new Truc();

		truc.setName("Machin-Le-Rouge");
		truc.setBirthDate(MiscFactory.priseDeLaBastille);
		Bidule bidule = new Bidule();
		truc.setFirstBiduleEver(bidule);
		bidule.setName("Hector-Vert");

		objects = context.getObjects();
		// context.

		objects.put("truc", truc);

	}

	@Test
	public void test1() throws SimpleELException {
		
		Object object;

		object = SimpleEL.evaluateExpression(truc, "truc", "truc");
		Assert.assertEquals(object, truc);

		object = SimpleEL.evaluateExpression(truc, "truc", "truc.name");
		Assert.assertEquals(object, "Machin-Le-Rouge");
	}

	@Test
	public void test2() throws SimpleELException {

		Object object;

		object = SimpleEL.evaluateExpression(truc, "truc", "truc.firstBiduleEver");
		Assert.assertEquals(object, truc.getFirstBiduleEver());

		object = SimpleEL.evaluateExpression(truc, "truc", "truc.firstBiduleEver.name");
		Assert.assertEquals(object, "Hector-Vert");
	}

	@Test
	public void testToStringWithConverter() throws SimpleELException {

		Object object;


		object = SimpleEL.evaluateExpressionAndConvertToString(context, "truc.birthDate");
		Assert.assertEquals(object, "14/07/1789");
	}

	@Test(expectedExceptions = ELSyntaxException.class)
	public void testNotEvenAObject() throws SimpleELException {

		SimpleEL.evaluateExpression(truc, "truc", "");
	}

	@Test(expectedExceptions = UnknownObjectException.class)
	public void testUnknownObject() throws SimpleELException {

		SimpleEL.evaluateExpression(truc, "truc", "foobar");
	}

	@Test(expectedExceptions = UnknownAttributException.class)
	public void testUnknownAttribut() throws SimpleELException {

		SimpleEL.evaluateExpression(truc, "truc", "truc.space");
	}

	@Test(expectedExceptions = ELNullPointerException.class)
	public void testObjectNullPointer() throws SimpleELException {
		SimpleEL.evaluateExpression(null, "truc", "truc.name");
	}

	@Test(expectedExceptions = ELNullPointerException.class)
	public void testObjectNullPointerOnUnexistingAttribut() throws SimpleELException {
		SimpleEL.evaluateExpression(null, "truc", "truc.doesnotexist");
	}

	@Test(expectedExceptions = ELNullPointerException.class)
	public void testAttributNullPointer() throws SimpleELException {

		truc.setFirstBiduleEver(null);

		SimpleEL.evaluateExpression(truc, "truc", "truc.firstBiduleEver.name");
	}

}
