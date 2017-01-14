package net.tetrakoopa.mdu4j.domain;

import java.io.Serializable;

//import javax.persistence.Id;
//import javax.persistence.MappedSuperclass;

//@MappedSuperclass
public abstract class Persistable<ID extends Serializable> implements Serializable {

	//@Id
	private ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

}
