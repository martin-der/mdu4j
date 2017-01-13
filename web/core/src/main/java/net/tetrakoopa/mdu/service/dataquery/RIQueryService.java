package net.tetrakoopa.mdu.service.dataquery;

import java.io.IOException;
import java.util.List;

import net.tetrakoopa.mdu.service.dataquery.model.AggregateFunction;
import net.tetrakoopa.mdu.service.dataquery.model.Column;
import net.tetrakoopa.mdu.service.dataquery.model.Entity;
import net.tetrakoopa.mdu.service.dataquery.model.Operator;
import net.tetrakoopa.mdu.service.dataquery.model.Query;
import net.tetrakoopa.mdu.service.dataquery.model.ResultSet;
import net.tetrakoopa.mdu.service.dataquery.model.Schema;
import net.tetrakoopa.mdu4j.util.IOUtil;
import net.tetrakoopa.mdu4j.util.StringUtil;

import com.google.gson.Gson;


public class RIQueryService implements DataQueryService {

	private final static String SERIALIZED_RESOURCE_OPERATORS = "net/tetrakoopa/mdu/service/dataquery/operators.json";

	@Override
	public ResultSet select(Query query) {
		ResultSet resultSet = new ResultSet();
		for (Column column : query.getColumns()) {
			ResultSet.Column rsColumn = new ResultSet.Column();
			rsColumn.setLabel(column.getCaption());
			rsColumn.setId(column.getExpression().getId());
			rsColumn.setType(column.getExpression().getDataType());
			resultSet.getColums().add(rsColumn);
		}
		return resultSet;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Operator> createOperators() {
		try {
			return new Gson().fromJson(IOUtil.readString(SERIALIZED_RESOURCE_OPERATORS), List.class);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to load default operators " + e.getMessage(), e);
		}
	}

	@Override
	public String convertToSQLStatement(Query query) {

		final StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");

		StringUtil.concatCollection(sql, query.getColumns(), ", ", new StringUtil.QuickWriter<Column>() {

			@Override
			public void write(Column column, StringBuffer buffer) {
				sql.append(column.getExpression().getId());
				sql.append(" as \"");
				// FIXME Sql Injection
				sql.append(column.getCaption());
				sql.append('"');
			}
		});

		sql.append(" FROM ");

		return sql.toString();
	}

	@Override
	public Schema buildSchema(String name, Class<?>... entityClasses) {

		boolean fakeItAnymway = true;

		final Schema schema = new Schema();

		Entity rootEntity = new Entity();
		schema.setRootEntity(rootEntity);

		rootEntity.setName(name);

		for (Class<?> entityClass : entityClasses) {

			Entity entity = new Entity();

			entity.setName(entityClass.getSimpleName());

			rootEntity.getSubEntities().add(entity);

		}

		if (fakeItAnymway) {
			try {
				return new Gson().fromJson(IOUtil.readString("net/tetrakoopa/mdu/service/dataquery/schema.json"), Schema.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return schema;
	}

	@Override
	public List<AggregateFunction> createAggregateFunctions() {
		try {
			return new Gson().fromJson(IOUtil.readString("net/tetrakoopa/mdu/service/dataquery/aggregateFunctions.json"), List.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

