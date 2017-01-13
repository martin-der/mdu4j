package net.tetrakoopa.mdu.service.crud;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.ApplicationContextAware;

public abstract class AbstractPersistenceContextAwareCrud<ID extends Serializable, MDL> extends AbstractCrud<ID, MDL> implements Crud<ID, MDL>,
		ApplicationContextAware {

	private EntityManager em;

	@PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

	public AbstractPersistenceContextAwareCrud(Class<MDL> modelClass) {
		super(modelClass);
	}
}
