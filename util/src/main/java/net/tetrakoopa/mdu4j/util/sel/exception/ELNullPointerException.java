package net.tetrakoopa.mdu4j.util.sel.exception;

public class ELNullPointerException extends SimpleELException {

	private static final long serialVersionUID = 250945234406885891L;

	public ELNullPointerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ELNullPointerException(String message) {
		super(message);
	}

	public ELNullPointerException(Throwable cause) {
		super(cause);
	}

	public ELNullPointerException() {
		super();
	}

}
