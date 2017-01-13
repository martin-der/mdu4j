package net.tetrakoopa.mdu.service.crud.exception;

/**
 * Thrown when an element could not be found, either because there no element
 * with a provided id, either because there no element matching some criterias.<br/>
 */
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
