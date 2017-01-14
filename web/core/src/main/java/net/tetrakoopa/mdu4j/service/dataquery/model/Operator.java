package net.tetrakoopa.mdu4j.service.dataquery.model;

public class Operator {

	private String id;

	private String caption;
	private String displayFormat;

	private int paramCount;

	private String valueKind;
	private String exprType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public String getValueKind() {
		return valueKind;
	}

	public void setValueKind(String valueKind) {
		this.valueKind = valueKind;
	}

	public String getExprType() {
		return exprType;
	}

	public void setExprType(String exprType) {
		this.exprType = exprType;
	}
}
