package net.tetrakoopa.mdu.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Condition {

	private boolean justAdded;

	private String typeName;

	private boolean enabled;

	@SerializedName("operatorID")
	private String operatorId;

	private final List<Expression> expressions = new ArrayList<Expression>();

	public boolean isJustAdded() {
		return justAdded;
	}

	public void setJustAdded(boolean justAdded) {
		this.justAdded = justAdded;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

}
