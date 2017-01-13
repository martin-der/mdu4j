package net.tetrakoopa.mdu4j.util.sel.exception;

public class UnknownObjectException extends UnknownElementException {

	private static final long serialVersionUID = 4324360877030801652L;

	public UnknownObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownObjectException(String message) {
		super(message);
	}

	public UnknownObjectException(Throwable cause) {
		super(cause);
	}

	public UnknownObjectException() {
		super();
	}

}
