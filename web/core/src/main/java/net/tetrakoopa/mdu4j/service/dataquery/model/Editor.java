package net.tetrakoopa.mdu4j.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Editor {

	private String id;
	private String restype; // ex "Unknown"
	private Type type;
	
	@SerializedName("sql")
	private String sqlRequest;
	
	private Values values;

	public static enum Type {
		LIST, @SerializedName("SQLLIST")
		SQL_LIST, @SerializedName("CUSTOMLIST")
		CUSTOM_LIST, EDIT, @SerializedName("DATETIME")
		DATE_TIME
	}

	public static class Values {
		@SerializedName("value")
		private final List<Value> values = new ArrayList<Value>();

		public List<Value> getValues() {
			return values;
		}
	}
	public static class Value {
		
		private String id;
		private String text;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}

	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getRestype() {
		return restype;
	}


	public void setRestype(String restype) {
		this.restype = restype;
	}


	public Values getValues() {
		return values;
	}


	public String getSqlRequest() {
		return sqlRequest;
	}


	public void setSqlRequest(String sqlRequest) {
		this.sqlRequest = sqlRequest;
	}

}