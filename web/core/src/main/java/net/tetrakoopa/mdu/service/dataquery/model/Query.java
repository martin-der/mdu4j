package net.tetrakoopa.mdu.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;


public class Query {

	private Attribute root;
	
	private final List<Column> columns = new ArrayList<Column>();

	private final List<Condition> conditions = new ArrayList<Condition>();

	private final List<Float> justsorted = new ArrayList<Float>();

	public Attribute getRoot() {
		return root;
	}

	public void setRoot(Attribute root) {
		this.root = root;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Float> getJustsorted() {
		return justsorted;
	}

	public List<Condition> getConditions() {
		return conditions;
	}
}
