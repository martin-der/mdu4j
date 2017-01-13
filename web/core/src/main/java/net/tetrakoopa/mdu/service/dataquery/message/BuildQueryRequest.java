package net.tetrakoopa.mdu.service.dataquery.message;

import net.tetrakoopa.mdu.service.dataquery.model.Attribute;

public class BuildQueryRequest {

	private Attribute root;

	public Attribute getRoot() {
		return root;
	}

	public void setRoot(Attribute root) {
		this.root = root;
	}
}
