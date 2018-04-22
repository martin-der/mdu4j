package net.tetrakoopa.mdu4j.domain.crud;

import java.io.Serializable;
import java.util.List;

import net.tetrakoopa.mdu4j.domain.crud.exception.CrudException;

public interface Crud<ID extends Serializable, MDL> {
	
	ID create(MDL model) throws CrudException;

	void create(ID id, MDL model) throws CrudException;

	MDL retrieve(ID id) throws CrudException;
	
	void update(MDL model) throws CrudException;
	
	void delete(ID id) throws CrudException;
	
	List<MDL> retrieveAll() throws CrudException;

}
