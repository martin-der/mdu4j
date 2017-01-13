package net.tetrakoopa.mdu.service.dataquery.model;

import java.util.ArrayList;
import java.util.List;

public class Entity extends Thing {

	private String name;

	private final List<Attribute> attributes = new ArrayList<Attribute>();

	private final List<Entity> subEntities = new ArrayList<Entity>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<Entity> getSubEntities() {
		return subEntities;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

}
