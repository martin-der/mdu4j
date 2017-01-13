package net.tetrakoopa.mdu4j.util.sel.exception;

public class ELSyntaxException extends SimpleELException {

	private static final long serialVersionUID = 1433285436328035910L;

	private final int position;

	public ELSyntaxException(String message, int position, Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public ELSyntaxException(String message, int position) {
		super(message);
		this.position = position;
	}

	public ELSyntaxException(Throwable cause, int position) {
		super(cause);
		this.position = position;
	}

	public ELSyntaxException(int position) {
		super();
		this.position = position;
	}

	public ELSyntaxException() {
		super();
		this.position = 0;
	}

	public int getPosition() {
		return position;
	}

}
