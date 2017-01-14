package net.tetrakoopa.mdu4j.service.crud.exception;


/**
 * Thrown when an element already exists. Most common case is encountered while adding a new element with an id, already used by another element<br/>
 * <ul>
 * <li>Is handled differently by <code>Application.show<i>Severity</i>MessageDialog</code> : cause.message will be prompted to user</li>
 * </ul>
 */
public class ElementAlreadyExistsException extends CrudException {

	private static final long serialVersionUID = -891585268888801160L;

	public ElementAlreadyExistsException(Class<?> model, String message, Throwable cause) {
		super(model, message, cause);
	}
	public ElementAlreadyExistsException(Class<?> model, String message) {
		super(model, message);
	}
	public ElementAlreadyExistsException(Class<?> model, Throwable cause) {
		super(model, cause);
	}
	public ElementAlreadyExistsException(Class<?> model) {
		super(model);
	}

}
