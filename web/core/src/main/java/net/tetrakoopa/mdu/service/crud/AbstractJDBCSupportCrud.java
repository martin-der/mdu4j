package net.tetrakoopa.mdu.service.crud;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import net.tetrakoopa.mdu.service.crud.exception.CrudException;
import net.tetrakoopa.mdu.service.crud.exception.InstantiationException;
import net.tetrakoopa.mdu.service.crud.exception.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public abstract class AbstractJDBCSupportCrud<ID extends Serializable, MDL> extends AbstractCrud<ID, MDL> implements Crud<ID, MDL>, ApplicationContextAware {

    private JdbcTemplate jdbcTemplate;
    
    private String modelName;
    
    private ParameterizedRowMapper<MDL> rowMapper;
    private PojoMapper<MDL> pojoMapper;
    private String tableName;
    private String [] rowNames;
	private String commaSeparatedRowNamesWithId;
	private String commaSeparatedRowNames;
    private String commaSeparatedQuestionMarks;
	private String commaSeparatedQuestionMarksWithId;
    private String rowID;

	private String QUERY_FETCH_ALL;
	private String QUERY_QUERY_ID;
	private String QUERY_UPDATE_ALL_COLUMNS;
	private String QUERY_DELETE_ID;

    public interface PojoMapper<T> {
    	Object mapPojo (T pojo, String name);
    }

	public AbstractJDBCSupportCrud(Class<MDL> modelClass) {
		super(modelClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext context) {
		try {

			// ReflectionUtils.makeAccessible(Field field);
			// Class<MDL> foundModelClass = null;
			//
			// modelClass = foundModelClass;
			modelName = getModelClass().getSimpleName();
			tableName = createTableName().toUpperCase();
			rowID = createIDName().trim();
			rowMapper = createRowMapper();
			pojoMapper = createPojoMapper();
			rowNames = createRowNames();
			commaSeparatedRowNamesWithId = createCommaSeparatedNames(rowID, rowNames);
			commaSeparatedRowNames = createCommaSeparatedNames(null, rowNames);
			commaSeparatedQuestionMarksWithId = commaSeparatedRowNamesWithId.replaceAll("[^,]+", "?");
			commaSeparatedQuestionMarks = commaSeparatedRowNames.replaceAll("[^,]+", "?");
			
			
			QUERY_FETCH_ALL = "SELECT " + commaSeparatedRowNamesWithId + " FROM " + tableName + "";
			QUERY_QUERY_ID = "SELECT " + commaSeparatedRowNamesWithId + " FROM " + tableName + " WHERE " + rowID + " = ?";
			QUERY_UPDATE_ALL_COLUMNS = "UPDATE " + tableName + " ( " + commaSeparatedRowNamesWithId + " ) VALUES (" + commaSeparatedQuestionMarksWithId + ")";
			QUERY_DELETE_ID = "DELETE FROM " + tableName + " WHERE "+rowID+ " = ?";

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InstantiationException(getModelClass(), "JdbcTemplate Mapping Failed : " + ex.getMessage(), ex);
		}

	}

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }

	protected JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	protected abstract ID getModelId(MDL model);
	protected abstract void setModelId(MDL model, ID id);
	protected abstract ID getConvertFromSqlKey(Number key);
	protected abstract ParameterizedRowMapper<MDL> createRowMapper();
	protected abstract PojoMapper<MDL> createPojoMapper();
	protected abstract String createIDName();
	protected abstract String [] createRowNames();

	/**
	 * findOut what is the name of the table <br/>
	 * default : use the <i>simple name</i> of the model class
	 */
	protected String createTableName() {
		return getModelClass().getSimpleName();
	}
	
	@Override
	//@Transactional
	public ID create(MDL model) throws CrudException {

		final String sql = "INSERT INTO " + tableName + " ( " + commaSeparatedRowNames + " ) VALUES (" + commaSeparatedQuestionMarks + ")";
		
		final Object objects[] = createObjectsFromPojo(model, null, rowNames);

		getJdbcTemplate().update ( sql, objects );

		KeyHolder holder = new GeneratedKeyHolder();

		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int i = 0;
				final int m = objects.length;
				for (Object object : objects) {
					ps.setObject(i, objects[i]);
					i++;
				}

				return ps;
			}
		}, holder);

		ID id = getConvertFromSqlKey(holder.getKey());
		setModelId(model, id);
		return id;
	}

	@Override
	public void update(MDL model) throws NoSuchElementException {

		final ID id = getModelId(model);
		final Object objects[] = createObjectsFromPojo(model, id, rowNames);

		try {
			getJdbcTemplate().update(QUERY_UPDATE_ALL_COLUMNS, objects);
		} catch (org.springframework.dao.DataAccessException dex) {
			throw new NoSuchElementException(getModelClass(), "Update failed : No such " + modelName + " width id='" + id + "'", dex);
		}
	}

	@Override
	public MDL retrieve(ID id) throws NoSuchElementException {
		
		try {
			return jdbcTemplate.queryForObject(QUERY_QUERY_ID, new Object[] { id }, rowMapper);
		} catch (org.springframework.dao.EmptyResultDataAccessException erdaEx) {
			throw new NoSuchElementException(getModelClass(), "Retrieve failed : No such " + modelName + " width id='" + id + "'", erdaEx);
		}
	}
	
	@Override
	public void delete(ID id) throws NoSuchElementException {
		if (getJdbcTemplate().update(QUERY_DELETE_ID, new Object[] { id }) < 1)
			throw new NoSuchElementException(getModelClass(), "Delete failed : No such " + modelName + " width id='" + id + "'");
	}

	@Override
	public List<MDL> fetchAll() {
		return jdbcTemplate.query(QUERY_FETCH_ALL, rowMapper);
	}

	private Object[] createObjectsFromPojo(MDL model, ID rowID, String rows[]) {
		final boolean withID = rowID != null;
		Object objects[] = new Object[(withID ? 1 : 0) + rows.length];
		int i;
		if (withID) {
			objects[0] = rowID;
			i = 1;
		} else {
			i = 0;
		}
		for ( String row : rows ) {
			objects[i] = pojoMapper.mapPojo(model, row);
			i ++;
		};
		return objects;
	}
	
	private String createCommaSeparatedNames(String rowID, String[] names) {
		StringBuffer result = new StringBuffer();
		boolean first = true;
		
		if (rowID != null) {
			result.append(rowID);
			first = false;
		}
		
		for (String name : names) {
			if (rowID!=null &&rowID.equals(name))
				continue;
			
			if (first)
				first = false;
			else
				result.append(',');
			
			result.append(name);
		}
		return result.toString();
	}


}
