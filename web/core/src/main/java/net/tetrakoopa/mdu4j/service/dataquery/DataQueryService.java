package net.tetrakoopa.mdu4j.service.dataquery;

import java.util.List;

import net.tetrakoopa.mdu4j.service.dataquery.model.AggregateFunction;
import net.tetrakoopa.mdu4j.service.dataquery.model.Operator;
import net.tetrakoopa.mdu4j.service.dataquery.model.Query;
import net.tetrakoopa.mdu4j.service.dataquery.model.ResultSet;
import net.tetrakoopa.mdu4j.service.dataquery.model.Schema;


public interface DataQueryService {

	ResultSet select(Query query);

	List<Operator> createOperators();

	Schema buildSchema(String name, Class<?>... entityClasses);

	String convertToSQLStatement(Query query);

	List<AggregateFunction> createAggregateFunctions();

}
