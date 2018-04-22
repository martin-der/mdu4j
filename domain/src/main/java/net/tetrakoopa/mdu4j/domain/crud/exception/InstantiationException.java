package net.tetrakoopa.mdu4j.domain.crud.exception;


public class InstantiationException extends RuntimeCRUDException {

	
	private static final long serialVersionUID = 4067738892215241914L;

	public InstantiationException(Class<?> model, String message, Throwable cause) {
		super(model, message, cause);
	}
	public InstantiationException(Class<?> model, String message) {
		super(model, message);
	}
	public InstantiationException(Class<?> model, Throwable cause) {
		super(model, cause);
	}
	public InstantiationException(Class<?> model) {
		super(model);
	}

	public InstantiationException(String message, Throwable cause) {
		super(message, cause);
	}
	public InstantiationException(String message) {
		super(message);
	}
	public InstantiationException(Throwable cause) {
		super(cause);
	}
	public InstantiationException() {
		super();
	}
	
}
