package net.tetrakoopa.mdu4j.service.dataquery.message;

import net.tetrakoopa.mdu4j.service.dataquery.model.Attribute;

public class BuildQueryRequest {

	private Attribute root;

	public Attribute getRoot() {
		return root;
	}

	public void setRoot(Attribute root) {
		this.root = root;
	}
}
