package net.tetrakoopa.mdu4j.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResultSet {

	@SerializedName("cols")
	private final List<Column> columns = new ArrayList<Column>();
	private final List<Row> rows = new ArrayList<Row>();

	public static class Column {
		private String id;
		private String label;
		private String type;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public static class Row {

		public static class VHOLDER {
			private String v;

			public String getV() {
				return v;
			}

			public void setV(String v) {
				this.v = v;
			}
		}

		private final VHOLDER c = new VHOLDER();

		public VHOLDER getC() {
			return c;
		}

		public String getValue() {
			return c.getV();
		}

		public void setValue(String value) {
			c.setV(value);
		}
	}

	public List<Column> getColums() {
		return columns;
	}

	public List<Row> getRows() {
		return rows;
	}
}