package net.tetrakoopa.mdu.service.crud;

import java.io.Serializable;
import java.util.List;

import net.tetrakoopa.mdu.service.crud.exception.CrudException;

public interface Crud<ID extends Serializable, MDL> {
	
	ID create(MDL model) throws CrudException;

	void create(ID id, MDL model) throws CrudException;

	MDL retrieve(ID id) throws CrudException;
	
	void update(MDL model) throws CrudException;
	
	void delete(ID id) throws CrudException;
	
	List<MDL> retrieveAll() throws CrudException;

}
