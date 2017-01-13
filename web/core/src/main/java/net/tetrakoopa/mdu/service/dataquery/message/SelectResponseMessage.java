package net.tetrakoopa.mdu.service.dataquery.message;

import net.tetrakoopa.mdu.service.dataquery.model.ResultSet;

public class SelectResponseMessage {

	private String statement;

	private ResultSet resultSet;

	public SelectResponseMessage() {

	}

	public SelectResponseMessage(String statement, ResultSet resultSet) {
		this.statement = statement;
		this.resultSet = resultSet;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
}
