package net.tetrakoopa.mdu4j.service.crud.exception;

public class RuntimeCRUDException extends RuntimeException {

	private final Class<?> model;

	private static final long serialVersionUID = -8513061649841629763L;

	public RuntimeCRUDException(Class<?> model, String message, Throwable cause) {
		super(message, cause);
		this.model = model;	
	}
	public RuntimeCRUDException(Class<?> model, String message) {
		super(message);
		this.model = model;	
	}
	public RuntimeCRUDException(Class<?> model, Throwable cause) {
		super(cause);
		this.model = model;	
	}
	public RuntimeCRUDException(Class<?> model) {
		super();
		this.model = model;	
	}
	public RuntimeCRUDException(String message, Throwable cause) {
		super(message, cause);
		this.model = null;	
	}
	public RuntimeCRUDException(String message) {
		super(message);
		this.model = null;	
	}
	public RuntimeCRUDException(Throwable cause) {
		super(cause);
		this.model = null;	
	}
	public RuntimeCRUDException() {
		super();
		this.model = null;	
	}
	public Class<?> getModel() {
		return model;
	}

}
