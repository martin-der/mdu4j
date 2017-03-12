package net.tetrakoopa.mdu4j.view;

import net.tetrakoopa.mdu4j.test.TestResourcesFetcher;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class AbstractBuilderTest {

	private static final TestResourcesFetcher testResourcesFetcher = new TestResourcesFetcher();

	public static class Engine {

		private float horsePower;

		public float getHorsePower() {
			return horsePower;
		}

		public void setHorsePower(float horsePower) {
			this.horsePower = horsePower;
		}
	}
	public static class Vehicle {

		@UIAttribute.Label("identifiant")
		private String id;

		protected final Engine engines[];

		protected Vehicle(Engine[] engines) {
			this.engines = engines;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	public static class Plane extends Vehicle {
		private int takeOffDistance;
		private int landingDistance;

		public Plane(Engine engines[]) {
			super(engines);
		}
	}
	public static class Motorcycle extends Vehicle {
		public Motorcycle(float horsePower) {
			super(new Engine[] {new Engine()});
			engines[0].setHorsePower(horsePower);
		}
	}

	public static class Rocket extends Vehicle {

		private Rocket attachedRocket;

		public Rocket(float horsePower) {
			super(new Engine[] {new Engine()});
			engines[0].setHorsePower(horsePower);
		}

		public Rocket getAttachedRocket() {
			return attachedRocket;
		}

		public void setAttachedRocket(Rocket attachedRocket) {
			this.attachedRocket = attachedRocket;
		}

	}

	@Test
	public void testPlane() {

		final Engine engine = new Engine();
		engine.horsePower = 1200f;

		final Engine engines[] = new Engine[] {
			engine, engine, engine, engine
		};
		final Plane plane = new Plane(engines);

		plane.setId("A347");

		plane.landingDistance = 300;
		plane.takeOffDistance = 250;

		final ToStringBuilder builder = printerBuilder(System.out);

		builder.build(null, plane);

		Assert.assertEquals(testResourcesFetcher.getTextForThisMethod("txt"), builder.getResult());
	}

	@Test
	public void testMotorcycle() {

		final Vehicle vehicle = new Motorcycle(1.5f);

		final ToStringBuilder builder = printerBuilder(System.out);

		builder.build(null, vehicle);

		Assert.assertEquals(testResourcesFetcher.getTextForThisMethod("txt"), builder.getResult());
	}

	@Test
	@Ignore
	public void testRocket() {

		final Rocket rocket = new Rocket(1000f);

		// Oops, setting itself as attached rocket
		// let's hope it won't turn the process into a endless loop
		rocket.setAttachedRocket(rocket);

		final ToStringBuilder builder = printerBuilder(System.out);

		builder.build(null, rocket);

		Assert.assertEquals(testResourcesFetcher.getTextForThisMethod("txt"), builder.getResult());
	}

	private class ToStringBuilder extends AbstractBuilder<Object> {

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		private final PrintStream out;

		public ToStringBuilder() {
			this.out = new PrintStream(outputStream);
		}

		@Override
		protected void buildForField(Object o, ViewElement element) {
			printElement(out, element);
		}

		public String getResult() {
			return new String(outputStream.toByteArray());
		}
	}

	private ToStringBuilder printerBuilder(PrintStream out) {
		return new ToStringBuilder();
	}

	private void printShift(PrintStream stream, AbstractBuilder.ViewElement element) {
		AbstractBuilder.ViewElement parent = element;
		while ((parent = parent.parent)!=null) {
			stream.print("    ");
		}
	}
	private void printElement(PrintStream stream, AbstractBuilder.ViewElement element) {
		printShift(stream, element);
		stream.println("["+element.internalReference+"]");
		printShift(stream, element);
		stream.println("  Label : "+element.label);
		printShift(stream, element);
		stream.println("  Type : "+element.type);
		printShift(stream, element);
		stream.println("  Value : "+element.value);
		printShift(stream, element);
		stream.println("  Parent : "+(element.parent == null ? "<none>" : element.parent.internalReference));
	}
}
