package net.tetrakoopa.mdu.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service("mdu.mailService")
public class MailService {
	 
	@Autowired
	private MailSender mailSender;
	 
	public void sendMail(String from, String to, String subject, String msg) throws MailException {
 
		SimpleMailMessage message = new SimpleMailMessage();
 
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(msg);
		
		try {
			mailSender.send(message);
		} catch(Exception ex) {
			throw new MailException(ex.getMessage(), ex);
		}
	}
	 
}
