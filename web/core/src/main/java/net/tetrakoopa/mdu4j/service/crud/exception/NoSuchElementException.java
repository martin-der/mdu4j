package net.tetrakoopa.mdu4j.service.crud.exception;


public class NoSuchElementException extends CrudException {

	private static final long serialVersionUID = 5521684082324164021L;

	public NoSuchElementException(Class<?> model, String message, Throwable cause) {
		super(model, message, cause);
	}
	public NoSuchElementException(Class<?> model, String message) {
		super(model, message);
	}
	public NoSuchElementException(Class<?> model, Throwable cause) {
		super(model, cause);
	}
	public NoSuchElementException(Class<?> model) {
		super(model);
	}

}
