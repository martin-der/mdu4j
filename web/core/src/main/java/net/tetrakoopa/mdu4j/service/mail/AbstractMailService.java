package net.tetrakoopa.mdu4j.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

public abstract class AbstractMailService {
	 
	protected abstract MailSender getMailSender();

	public void sendHtmlMail(String from, String to, String subject, String msg) throws MailException {
		sendMail(from, new String[] {to}, subject, msg, true);
	}
	public void sendMail(String from, String to, String subject, String msg) throws MailException {
		sendMail(from, new String[] {to}, subject, msg, false);
	}

	private void sendMail(String from, String to[], String subject, String msg, boolean asHtml) throws MailException {
		final SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(msg);

		try {
			getMailSender().send(message);
		} catch(Exception ex) {
			throw new MailException(ex.getMessage(), ex);
		}
	}

}
