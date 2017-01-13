package net.tetrakoopa.mdu.service.dataquery;

import java.util.List;

import net.tetrakoopa.mdu.service.dataquery.model.AggregateFunction;
import net.tetrakoopa.mdu.service.dataquery.model.Operator;
import net.tetrakoopa.mdu.service.dataquery.model.Query;
import net.tetrakoopa.mdu.service.dataquery.model.ResultSet;
import net.tetrakoopa.mdu.service.dataquery.model.Schema;


public interface DataQueryService {

	ResultSet select(Query query);

	List<Operator> createOperators();

	Schema buildSchema(String name, Class<?>... entityClasses);

	String convertToSQLStatement(Query query);

	List<AggregateFunction> createAggregateFunctions();

}
