package net.tetrakoopa.mdu4j.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DataEngine {

	private final List<Operator> operators = new ArrayList<Operator>();
	
	private Entity rootEntity;
	
	@SerializedName("aggrFunctions")
	private final List<AggregateFunction> aggregateFunctions = new ArrayList<AggregateFunction>();


	public List<Operator> getOperators() {
		return operators;
	}

	public List<AggregateFunction> getAggregateFunctions() {
		return aggregateFunctions;
	}

	public Entity getRootEntity() {
		return rootEntity;
	}

	public void setRootEntity(Entity rootEntity) {
		this.rootEntity = rootEntity;
	}

}
