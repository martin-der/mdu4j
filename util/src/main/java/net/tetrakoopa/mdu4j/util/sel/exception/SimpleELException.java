package net.tetrakoopa.mdu4j.util.sel.exception;

public abstract class SimpleELException extends Exception {

	private static final long serialVersionUID = 4569096016901015031L;

	public SimpleELException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimpleELException(String message) {
		super(message);
	}

	public SimpleELException(Throwable cause) {
		super(cause);
	}

	public SimpleELException() {
		super();
	}

}
