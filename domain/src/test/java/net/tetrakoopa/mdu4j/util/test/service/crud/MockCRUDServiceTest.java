package net.tetrakoopa.mdu4j.util.test.service.crud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import net.tetrakoopa.mdu.service.crud.MockCrud;
import net.tetrakoopa.mdu.service.crud.MockCrud.Service.PersistenceType;
import net.tetrakoopa.mdu.service.crud.MockCrud.Service.PopulationStategy;
import net.tetrakoopa.mdu.service.crud.exception.CrudException;
import net.tetrakoopa.mdu4j.test.things.Truc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class MockCRUDServiceTest {

	static long seed = 0;

	MoooockAlwaysPopulate REPO_ALWAYS_POPULATE;
	MoooockNeverPopulate REPO_NEVER_POPULATE;

	@Before
	public void init() {

		REPO_ALWAYS_POPULATE = new MoooockAlwaysPopulate();
		REPO_NEVER_POPULATE = new MoooockNeverPopulate();

		seed = 0;
	}

	@MockCrud.Service(populationStategy = PopulationStategy.NEVER, persistenceType = PersistenceType.NONE)
	public static class MoooockNeverPopulate extends Moooooock {

		@MockCrud.StaticSingletonInstance
		static MockCrud<Long, Truc> mock;

	}

	@MockCrud.Service(populationStategy = PopulationStategy.ALWAYS, fileSystemResourcePathPatten = "/net/tetrakoopa/mdu4j/util/test/service/crud/#{class.name}.MOCK-DATA.raw")
	// @MockCrud.Service(populationStategy = PopulationStategy.ALWAYS,
	// classpathResourceNamePattern = "class.name")
	public static class MoooockAlwaysPopulate extends Moooooock {

		@MockCrud.StaticSingletonInstance
		static MockCrud<Long, Truc> mock;

	}

	public static abstract class Moooooock extends MockCrud<Long, Truc> {

		public Moooooock() {
			this(null);
		}
		public Moooooock(Object seed) {
			super(Truc.class, seed);
		}

		@Override
		protected Long getIdFromModel(Truc model) {
			return model.getId();
		}

		@Override
		protected void setIdToModel(Long id, Truc model) {
			model.setId(id);
		}

		@Override
		protected void populate(List<Truc> models, Object seed) {
			models.add(createTrucWidthId("Foo"));
			models.add(createTrucWidthId("Bar"));
		}

		@Override
		protected void copy(Truc src, Truc dest) {
			dest.setId(src.getId());
			dest.setName(src.getName());

		}

		@Override
		protected Long generateId() {
			return seed++;
		}

	}

	// @Test
	public void fakeTestToSaveInstance() throws CrudException, FileNotFoundException {
		REPO_ALWAYS_POPULATE.freezeContent(new FileOutputStream(new File("/tmp/mycrud.raw")));
	}

	@Test
	public void checkFrozenDatasNoPreloading() throws CrudException {

		Assert.assertTrue(REPO_NEVER_POPULATE.fetchAll().isEmpty());
	}

	@Test
	public void checkFrozenDatasPreloadingFromClasspath() throws CrudException {

		Assert.assertFalse(REPO_ALWAYS_POPULATE.fetchAll().isEmpty());
		
		Truc truc = REPO_ALWAYS_POPULATE.retrieve(1l);
		Assert.assertEquals("Bar", truc.getName());
	}

	@Test
	public void testSomeManipulations() throws CrudException {

		seed = REPO_ALWAYS_POPULATE.count();

		REPO_ALWAYS_POPULATE.create(createTruc("Lapin"));

		Truc truc, truc2;

		truc = REPO_ALWAYS_POPULATE.retrieve(2l);
		Assert.assertEquals("Lapin", truc.getName());

		truc.setName("CaroOOote-Eater");
		REPO_ALWAYS_POPULATE.update(truc);
		truc2 = REPO_ALWAYS_POPULATE.retrieve(2l);

		Assert.assertSame(truc.getName(), truc2.getName());

	}


	private static Truc createTruc(String name) {
		Truc truc = new Truc();
		truc.setName(name);
		return truc;
	}

	private static Truc createTrucWidthId(String name) {
		Truc truc = createTruc(name);
		truc.setId(seed++);
		return truc;
	}
	

}
