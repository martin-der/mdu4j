package net.tetrakoopa.mdu4j.service.crud;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.tetrakoopa.mdu4j.service.crud.exception.CrudException;
import net.tetrakoopa.mdu4j.service.crud.exception.ElementAlreadyExistsException;
import net.tetrakoopa.mdu4j.service.crud.exception.NoSuchElementException;
import net.tetrakoopa.mdu4j.util.IOUtil;
import net.tetrakoopa.mdu4j.util.MarshalingUtil;
import net.tetrakoopa.mdu4j.util.ReflectionUtil;
import net.tetrakoopa.mdu4j.util.parser.EnvVarsLikeParser;
import net.tetrakoopa.mdu4j.util.sel.SimpleEL;
import net.tetrakoopa.mdu4j.util.sel.SimpleELContext;
import net.tetrakoopa.mdu4j.util.sel.exception.SimpleELException;


public abstract class MockCrud<ID extends Serializable, MDL> implements Crud<ID, MDL> {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.FIELD)
	public @interface StaticSingletonInstance {

	}
	
	protected final Class<MDL> modelClass;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.TYPE)
	public @interface Service {
		public enum PersistenceType {
			NONE, CLASS_PATH, FILE_SYSTEM
		};
		public enum PopulationStategy {
			NEVER, IF_NO_PERSISTENCE, IF_PERSISTENCE_FAILED, ALWAYS
		};


		PersistenceType persistenceType() default PersistenceType.CLASS_PATH;
		PopulationStategy populationStategy() default PopulationStategy.IF_PERSISTENCE_FAILED;

		boolean useEL() default true;
		String classpathResourceNamePattern() default "#{class.name}_CRUD-MOCK.raw";
		String fileSystemResourcePathPatten() default "/tmp/yes-if-hope-to-find-correctly-serialized-mock-datas-here.raw";
	}

	
	private Object seed;

	public MockCrud(Class<MDL> modelClass, Object seed) {
		this.seed = seed;
		this.modelClass = modelClass;
	}

	public MockCrud<ID, MDL> instance() throws IllegalArgumentException, IllegalAccessException {

		Field instanceField = ReflectionUtil.findFirstAnnotatedFieldIfAny(StaticSingletonInstance.class, this.getClass());

		if (instanceField == null) {
			throw new IllegalStateException("There must be a static field with an @" + StaticSingletonInstance.class.getName().replace("$", ".") + " in "
				+ this.getClass().getName().replace("$", "."));
		}

		instanceField.setAccessible(true);
		@SuppressWarnings("unchecked")
		FreezableCrudService<ID, MDL> actualInstance = (FreezableCrudService<ID, MDL>) instanceField.get(null);

		if (actualInstance == null) {

			final Service crudMockAnnoation = this.getClass().getAnnotation(Service.class);

			actualInstance = new FreezableCrudService<ID, MDL>(modelClass, this, seed) {
				
				@Override
				protected void setIdToModel(ID id, MDL model) {
					userInstance.setIdToModel(id, model);
				}

				@Override
				protected ID getIdFromModel(MDL model) {
					return userInstance.getIdFromModel(model);
				}

				@Override
				public void populate(List<MDL> models, Object seed) {
					userInstance.populate(models, seed);
				}

				@Override
				protected ID generateId() {
					return userInstance.generateId();
				}

				@Override
				protected void copy(MDL src, MDL dest) {
					userInstance.copy(src, dest);

				}

			};


			instanceField.set(null, actualInstance);

			
			// -- try to load the crud from some resource
			
			final boolean triedToLoadPersistedDatas = crudMockAnnoation != null && crudMockAnnoation.persistenceType() != Service.PersistenceType.NONE;
			boolean triedToLoadPersistedDatasAndItFailed = false;

			if (crudMockAnnoation != null) {

				switch (crudMockAnnoation.persistenceType()) {
					case FILE_SYSTEM:
						assert (triedToLoadPersistedDatas);
						try {
							String resource;
							try {
							resource = getMaybeEvaluatedString(crudMockAnnoation, crudMockAnnoation.fileSystemResourcePathPatten());
							} catch (SimpleELException e) {
								triedToLoadPersistedDatasAndItFailed = true;
								break;
							}
							unfreezeContent(IOUtil.getResourceInputStream(resource));
						} catch (FileNotFoundException e) {
							triedToLoadPersistedDatasAndItFailed = true;
							break;
						}
						break;
					case CLASS_PATH:
						assert (triedToLoadPersistedDatas);
						try {
						String resource;
						try {
							resource = getMaybeEvaluatedString(crudMockAnnoation, crudMockAnnoation.classpathResourceNamePattern());
						} catch (SimpleELException e) {
							triedToLoadPersistedDatasAndItFailed = true;
							break;
						}
							unfreezeContent(IOUtil.getResourceInputStream(resource));
						} catch (FileNotFoundException e) {
							triedToLoadPersistedDatasAndItFailed = true;
							break;
						}
						break;
					case NONE:
						assert (!triedToLoadPersistedDatas);
						triedToLoadPersistedDatasAndItFailed = false;
						break;
					default:
						throw new IllegalStateException("Internal error : unhandled case of " + crudMockAnnoation.persistenceType() + " ( in "
							+ Service.PersistenceType.class.getName() + ")");
				}
			}


			// -- fill the Crud with user mock datas using abstract methode 'populate'

			final boolean shouldPopulateWithUserData;
			
			if (crudMockAnnoation == null) {
				shouldPopulateWithUserData = false;
			} else {
				final Service.PopulationStategy populationStategy = crudMockAnnoation.populationStategy();
				shouldPopulateWithUserData =
				/* Have no choice but to populate */
				populationStategy == Service.PopulationStategy.ALWAYS
				/* Only when no load from persisted datas was asked */
				|| ((triedToLoadPersistedDatas && triedToLoadPersistedDatasAndItFailed) && populationStategy == Service.PopulationStategy.IF_PERSISTENCE_FAILED);
				/* Only when no load from persisted datas was asked */
				
			}

			if (shouldPopulateWithUserData) {

				actualInstance.populate(actualInstance.objects, seed);

				// Check user didn't messed datas up by insert objects without
				// id
				int countObjectsWithoutId = 0;
				for (MDL object : actualInstance.objects) {
					if (getIdFromModel(object) == null) {
						countObjectsWithoutId++;
					}
				}
				if (countObjectsWithoutId > 0) {
					throw new IllegalStateException("Illegal population of Mock Data : " + countObjectsWithoutId + " object(s) had no id.");
				}

			}
		}
		return actualInstance;
	}


	public static abstract class FreezableCrudService<ID extends Serializable, MDL> extends MockCrud<ID, MDL> {
		
		protected final MockCrud<ID, MDL> userInstance;

		public FreezableCrudService(Class<MDL> modelClass, MockCrud<ID, MDL> userInstance, Object seed) {
			super(modelClass, seed);
			this.userInstance = userInstance;
		}

		/**
		 * list contenant toutes les instances gérée internalement par ce mock
		 * de Crud
		 */
		protected final List<MDL> objects = new ArrayList<MDL>();
		
		@SuppressWarnings("unchecked")
		protected <ANY_MDL> ANY_MDL instanciateClone(final ANY_MDL src)
		throws CloneNotSupportedException {
			if (src == null) {
				return null;
			}
			return (ANY_MDL) instanciateClone(src.getClass());
		}
		protected <ANY_MDL> ANY_MDL instanciateClone(final Class<ANY_MDL> srcClass)
		throws CloneNotSupportedException {
			ANY_MDL clone;
			try {
				clone = srcClass.newInstance();
			} catch (final InstantiationException e) {
				throw new CloneNotSupportedException("Unable to clone object '" + srcClass.getName() + "'");
			} catch (final IllegalAccessException e) {
				throw new CloneNotSupportedException("Unable to clone object '" + srcClass.getName() + "'");
			}
			return clone;
		}
		
		@SuppressWarnings("unchecked")
		protected MDL makeClone(final MDL mdl) {
			if (mdl == null) {
				return null;
			}
			final MDL clone;
			try {
				clone = (MDL) instanciateClone(mdl.getClass());
			} catch (final CloneNotSupportedException roex) {
				throw new IllegalStateException("Unable to clone object '" + mdl.getClass().getName() + "'", roex);
			}
			copy(mdl, clone);
			return clone;
		}
		
		protected final void copyAllButId(final MDL src, final MDL dest) {
			final ID id = getIdFromModel(dest);
			copy(src, dest);
			setIdToModel(id, dest);
		}
		
		/**
		 * Save mock content into stream
		 * 
		 * @param output destination file the mock state is to be saved into
		 * @throws IOException if any error append while while writing
		 */
		public void freeze(final OutputStream output)
		throws IOException {
			MarshalingUtil.freeze(objects, output);
		}
		
		/**
		 * Load mock content from stream
		 * 
		 * @param input source file the mock state is to be loaded from
		 * @throws IOException if any error append while while reading... ( including a wrapped ClassNotFoundException if one or more class(es) coudn't be found
		 *         )
		 */
		public void unfreeze(final InputStream input)
		throws IOException {
			final List<MDL> newObjects = MarshalingUtil.unfreeze(input);
			objects.clear();
			objects.addAll(newObjects);
		}
		
		@Override
		public synchronized ID create(final MDL model) throws ElementAlreadyExistsException {
			@SuppressWarnings("unchecked")
			final Class<MDL> modelClass = (Class<MDL>) model.getClass();
			final MDL result = createModel(modelClass);
			copy(model, result);
			ID id = getIdFromModel(model);
			if (id == null)
				id = generateId();
			checkNonNullId(id);
			checkUnusedId(id, modelClass);
			setIdToModel(id, result);
			objects.add(result);
			return id;
		}
		
		@Override
		public synchronized void create(final ID id, final MDL model)
		throws ElementAlreadyExistsException {
			@SuppressWarnings("unchecked")
			final Class<MDL> modelClass = (Class<MDL>) model.getClass();
			checkNonNullId(id);
			checkUnusedId(id, modelClass);
			final MDL result = createModel(modelClass);
			copy(model, result);
			setIdToModel(id, result);
			objects.add(result);
		}
		
		@Override
		public synchronized MDL retrieve(final ID id) throws NoSuchElementException {
			checkNonNullId(id);
			final MDL o = grab(id);
			if (o == null) {
				throw new NoSuchElementException(null, "No such element '" + id.toString() + "'");
			}
			@SuppressWarnings("unchecked")
			final MDL result = createModel((Class<MDL>) o.getClass());
			copy(o, result);
			return result;
		}

		@Override
		public synchronized void update(final MDL model)
		throws NoSuchElementException {
			final ID id = getIdFromModel(model);
			checkNonNullId(id);
			final MDL o = grab(id);
			if (o == null) {
				throw new NoSuchElementException(model.getClass(), "No such element '" + id.toString() + "'");
			}
			copyAllButId(model, o);
			
		}
		
		@Override
		public synchronized void delete(final ID id)
		throws NoSuchElementException {
			checkNonNullId(id);
			final MDL o = grab(id);
			if (o == null) {
				throw new NoSuchElementException(null, "No such element '" + id.toString() + "'");
			}
			objects.remove(o);
		}
		
		@Override
		public List<MDL> retrieveAll() throws CrudException {

			final List<MDL> all = new ArrayList<MDL>();
			for (final MDL object : objects) {
				@SuppressWarnings("unchecked")
				MDL newObject = createModel((Class<MDL>) object.getClass());
				copy(object, newObject);
				all.add(newObject);
			}
			return all;
		}

		private MDL grab(final ID id) {
			for (final MDL o : objects) {
				if (id.equals(getIdFromModel(o))) {
					return o;
				}
			}
			return null;
		}
		
		private void checkNonNullId(final ID id) {
			if (id == null) {
				throw new IllegalArgumentException("ID cannot be null");
			}
		}

		private void checkUnusedId(final ID id, final Class<MDL> modelClass) throws ElementAlreadyExistsException {
			if (id != null) {
				for (MDL object : objects) {
					if (id.equals(getIdFromModel(object)))
						throw new ElementAlreadyExistsException(modelClass, "ID " + id + " already used");
				}
			}

		}
		
	}

	@Override
	public ID create(MDL model) throws CrudException {
		try {
			return instance().create(model);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	@Override
	public void create(ID id, MDL model) throws CrudException {
		try {
			instance().create(id, model);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	@Override
	public MDL retrieve(ID id) throws CrudException {
		try {
			return instance().retrieve(id);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	@Override
	public void update(MDL model) throws CrudException {
		try {
			instance().update(model);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	@Override
	public void delete(ID id) throws CrudException {
		try {
			instance().delete(id);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	@Override
	public List<MDL> retrieveAll() throws CrudException {
		try {
			return instance().retrieveAll();
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	public final int count() throws CrudException {
		return retrieveAll().size();
	}

	public final void freezeContent(OutputStream output) {

	}

	public final void unfreezeContent(InputStream output) {

	}

	/**
	 * Create content into mock
	 * 
	 * @param seed
	 *            a user argument whose purpose is to act as a seed<br/>
	 *            Can be null<br/>
	 *            Given a same <code>seed</code>, an implementation
	 *            could/should? produce the same content.
	 */
	protected abstract void populate(List<MDL> models, Object seed);

	protected abstract ID generateId();

	protected abstract ID getIdFromModel(MDL model);

	protected abstract void setIdToModel(ID id, MDL model);

	protected abstract void copy(MDL src, MDL dest);

	/**
	 * Override this class in order to provide a small factory utility for the
	 * object<br/>
	 * Default implemention try to invoke the constructor with given arguments,
	 * if any
	 * 
	 * @param clazz
	 *            the clazz we need a instance of
	 * @param constructorArguments
	 *            will be passed to the constructor
	 * @return <MDL> a new instance of the Model used within the crud
	 */
	protected MDL createModel(final Class<MDL> clazz, Object... constructorArguments) {
		return ReflectionUtil.intanciateObject(clazz, constructorArguments);
	}

	private CrudException wrapped(Exception exception) {
		return new CrudException(null, exception.getMessage(), exception);
	}

	private String getMaybeEvaluatedString(final Service serviceAnnoation, String string) throws SimpleELException {
		if (!serviceAnnoation.useEL()) {
			return string;
		}

		final SimpleELContext elContext = createELContext();

		return EnvVarsLikeParser.expandVar(string, new EnvVarsLikeParser.ValueResolver() {

			final

			@Override
			public String resolve(String key) {
				try {
					return SimpleEL.evaluateExpressionAndConvertToString(elContext, key);
				} catch (SimpleELException ex) {
					// TODO Type this exception
					throw new RuntimeException("Exception while lookuping key '" + key + "' : " + ex.getMessage(), ex);
				}
			}
		});
	}
	private SimpleELContext createELContext() {
		SimpleELContext context = new SimpleELContext();

		context.getObjects().put("class", modelClass);

		return context;
	}

}
