package net.tetrakoopa.mdu4j.util.sel.exception;

public class UnknownAttributException extends UnknownElementException {

	private static final long serialVersionUID = 2752345099091264613L;

	public UnknownAttributException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownAttributException(String message) {
		super(message);
	}

	public UnknownAttributException(Throwable cause) {
		super(cause);
	}

	public UnknownAttributException() {
		super();
	}

}
