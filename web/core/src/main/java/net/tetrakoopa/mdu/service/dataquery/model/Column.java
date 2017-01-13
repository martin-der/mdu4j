package net.tetrakoopa.mdu.service.dataquery.model;

import com.google.gson.annotations.SerializedName;

public class Column {
	
	private String caption;
	private String sorting; // ex "None"
    private int sortIndex;

	@SerializedName("expr")
	private Expression expression;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSorting() {
		return sorting;
	}

	public void setSorting(String sorting) {
		this.sorting = sorting;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
}
