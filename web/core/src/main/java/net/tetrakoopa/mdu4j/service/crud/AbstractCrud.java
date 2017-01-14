package net.tetrakoopa.mdu4j.service.crud;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.springframework.context.ApplicationContextAware;

@MappedSuperclass
public abstract class AbstractCrud<ID extends Serializable, MDL> implements Crud<ID, MDL>, ApplicationContextAware {

	private final Class<MDL> modelClass;

	public AbstractCrud(Class<MDL> modelClass) {
		this.modelClass = modelClass;
	}

	public Class<MDL> getModelClass() {
		return modelClass;
	}

}
