package net.tetrakoopa.mdu4j.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Attribute extends Thing {

	private String id;

	private String dataType; // ex "WideString"
	private int size;

	private String linkType; // ex : "All"

	private boolean enabled;

	private final List<Condition> conditions = new ArrayList<Condition>();

	private String defaultOperator;

	private final List<String> operators = new ArrayList<String>();

	@SerializedName("defaultEditor")
	private Editor editor;

	private String description;

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public String getDefaultOperator() {
		return defaultOperator;
	}

	public List<String> getOperators() {
		return operators;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDefaultOperator(String defaultOperator) {
		this.defaultOperator = defaultOperator;
	}

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
