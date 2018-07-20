package com.Nexterus.TrackShipment.Services;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	private static final Logger log = LoggerFactory.getLogger(MailService.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");

	private String errorEmailFrom;
	private String errorEmailTo;
	private String errorEmailSubject;
	private JavaMailSender mailSender;

	public MailService(JavaMailSender mailSender, @Value("${error.email.from}") String errorEmailFrom,
			@Value("${error.email.to}") String errorEmailTo,
			@Value("${error.email.subject}") String errorEmailSubject) {
		this.mailSender = mailSender;
		this.errorEmailFrom = errorEmailFrom;
		this.errorEmailTo = errorEmailTo;
		this.errorEmailSubject = errorEmailSubject;
	}

	public void sendMail(String from, String to, String subject, String body, String[] attachments,
			ByteArrayResource byteAttachment, String filename, boolean asHtml) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			String[] recipients = to.split("[;,]");
			helper.setTo(recipients);
			helper.setSubject(subject);
			helper.setText(body, asHtml);

			if (attachments != null) {
				for (String attachment : attachments) {
					FileSystemResource file = new FileSystemResource(attachment);
					helper.addAttachment(file.getFilename(), file);
				}
			}

			if (byteAttachment != null) {
				helper.addAttachment(filename, byteAttachment);
			}

			if (log.isInfoEnabled())
				log.info("Sending email to ..." + to);

			mailSender.send(message);

			if (log.isInfoEnabled())
				log.info("Email sent successfully.");

		} catch (MessagingException me) {
			nxtLogger.error("Error creating email message. " + me.getMessage());
			throw new MailParseException(me);
		} catch (MailException ex) {
			nxtLogger.error("Error sending email to ..." + to + ". " + ex.getMessage());
		}
	}

	public void sendMail(String from, String to, String subject, String body, String[] attachment,
			ByteArrayResource byteAttachment, String filename) {
		sendMail(from, to, subject, body, attachment, (ByteArrayResource) byteAttachment, (String) filename, false);
	}

	public void sendMail(String from, String to, String subject, String body, String attachment) {
		String[] attachments = new String[1];
		attachments[0] = attachment;
		sendMail(from, to, subject, body, attachments, (ByteArrayResource) null, (String) null, false);
	}

	public void sendMail(String from, String to, String subject, String body, String[] attachments) {
		sendMail(from, to, subject, body, attachments, (ByteArrayResource) null, (String) null);
	}

	public void sendMail(String from, String to, String subject, String body) {
		sendMail(from, to, subject, body, (String[]) null, (ByteArrayResource) null, (String) null, false);
	}

	public void sendMail(String to, String subject, String message) {
		sendMail(errorEmailFrom, to, subject, message, (String[]) null, (ByteArrayResource) null, (String) null, false);
	}

	public void sendMail(String subject, String message) {
		sendMail(errorEmailFrom, errorEmailTo, subject, message, (String[]) null, (ByteArrayResource) null,
				(String) null, false);
	}

	public void sendMail(String message) {
		sendMail(errorEmailFrom, errorEmailTo, errorEmailSubject, message, (String[]) null, (ByteArrayResource) null,
				(String) null, false);
	}

	public void sendMail(String from, String to, String subject, String body, String attachment, boolean asHtml) {
		String[] attachments = new String[1];
		attachments[0] = attachment;
		sendMail(from, to, subject, body, attachments, (ByteArrayResource) null, (String) null, asHtml);
	}

	public void sendMail(String from, String to, String subject, String body, boolean asHtml) {
		sendMail(from, to, subject, body, (String[]) null, (ByteArrayResource) null, (String) null, asHtml);
	}

	public void sendMail(String to, String subject, String message, boolean asHtml) {
		sendMail(errorEmailFrom, to, subject, message, (String[]) null, (ByteArrayResource) null, (String) null,
				asHtml);
	}

	public void sendMail(String subject, String message, boolean asHtml) {
		sendMail(errorEmailFrom, errorEmailTo, subject, message, (String[]) null, (ByteArrayResource) null,
				(String) null, asHtml);
	}

	public void sendMail(String message, boolean asHtml) {
		sendMail(errorEmailFrom, errorEmailTo, errorEmailSubject, message, (String[]) null, (ByteArrayResource) null,
				(String) null, asHtml);
	}

	public void sendMail(String from, String to, String subject, Throwable t) {
		StringWriter trace = new StringWriter();
		t.printStackTrace(new PrintWriter(trace));

		sendMail(from, to, subject, trace.toString(), (String[]) null, (ByteArrayResource) null, (String) null, false);
	}

	public void sendMail(String to, String subject, Throwable t) {
		sendMail(errorEmailFrom, to, subject, t);
	}

	public void sendMail(String subject, Throwable t) {
		sendMail(errorEmailFrom, errorEmailTo, subject, t);
	}

	public void sendMail(Throwable t) {
		sendMail(errorEmailFrom, errorEmailTo, errorEmailSubject, t);
	}

	public void sendMailWithAttachment(String to, String subject, String message, String[] attachment) {
		sendMail(errorEmailFrom, to, subject, message, attachment);
	}

	public void sendMailWithAttachment(String to, String subject, String message, String attachment) {
		sendMail(errorEmailFrom, to, subject, message, attachment);
	}

	public void sendMailWithAttachment(String subject, String message, String attachment) {
		sendMail(errorEmailFrom, errorEmailTo, subject, message, attachment);
	}

	public void sendMailWithAttachment(String message, String attachment) {
		sendMail(errorEmailFrom, errorEmailTo, errorEmailSubject, message, attachment);
	}
}
