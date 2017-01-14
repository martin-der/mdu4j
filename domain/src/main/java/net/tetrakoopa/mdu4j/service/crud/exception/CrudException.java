package net.tetrakoopa.mdu4j.service.crud.exception;

public class CrudException extends Exception {

	private static final long serialVersionUID = 4569096016901015031L;

	private final Class<?> model ;

	public CrudException(Class<?> model, String message, Throwable cause) {
		super(message, cause);
		this.model = model;
	}
	public CrudException(Class<?> model, String message) {
		super(message);
		this.model = model;
	}
	public CrudException(Class<?> model, Throwable cause) {
		super(cause);
		this.model = model;
	}
	public CrudException(Class<?> model) {
		super();
		this.model = model;
	}
	public Class<?> getModel() {
		return model;
	}

}
