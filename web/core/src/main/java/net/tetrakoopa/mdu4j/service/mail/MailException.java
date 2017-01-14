package net.tetrakoopa.mdu4j.service.mail;

public class MailException extends Exception {

	private static final long serialVersionUID = -4594030714522384267L;
	
	public MailException(String message, Throwable cause) {
		super(message, cause);
	}
	public MailException(String message) {
		super(message);
	}
	public MailException(Throwable cause) {
		super(cause);
	}
	public MailException() {
		super();
	}

}
