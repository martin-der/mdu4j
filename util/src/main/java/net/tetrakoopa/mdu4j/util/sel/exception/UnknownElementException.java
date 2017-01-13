package net.tetrakoopa.mdu4j.util.sel.exception;

public abstract class UnknownElementException extends SimpleELException {

	private static final long serialVersionUID = 250945234406885891L;

	public UnknownElementException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownElementException(String message) {
		super(message);
	}

	public UnknownElementException(Throwable cause) {
		super(cause);
	}

	public UnknownElementException() {
		super();
	}

}
