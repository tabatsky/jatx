package jatx.onlineboard;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailHelper {
	static final String FROM = "service@tabatsky.ru";
	static final String SERVER_URL = "http://tabatsky.ru/onlineboard";
	
	static final String USER = "service@tabatsky.ru";
	static final String PASS = "dexpp37";
	static final String SMTP_HOST = "smtp.yandex.ru";
	static final String SMTP_PORT = "465";
	
	public static String sendLoginLink(String email, String user_id, String password) 
			throws UnsupportedEncodingException, MessagingException {
		String link = ServerSettings.SERVER_URL
				+ "/menu.jsp?user_id=" + user_id
				+"&password=" + password;
	
		sendMail(email, "Your Online Board login link", link);
		
		return "Login link has sent to your e-mail";
	}
	
	public static String sendInvite(String target_email, String target_user_id, 
			String target_password, String my_username, Integer room_id) 
					throws UnsupportedEncodingException, MessagingException {
		String confirmLink  = ServerSettings.SERVER_URL
				+ "/confirm?user_id=" + target_user_id
				+ "&password=" + target_password
				+ "&room=" + room_id.toString()
				+ "&action=confirm";
		String declineLink  = ServerSettings.SERVER_URL
				+ "/confirm?user_id=" + target_user_id
				+ "&password=" + target_password
				+ "&room=" + room_id.toString()
				+ "&action=decline";
		String msg = "User " + my_username + " invites you to Online Board\n"
				+ "\nConfirm:\n" + confirmLink + "\nDecline:\n" + declineLink;
		sendMail(target_email, "Online Board Invite", msg);
		
		return "Invite has sent to user";
	}
	
	public static String sendConfirmNotify(String my_email, String my_username, 
			String target_email, boolean confirm) 
					throws UnsupportedEncodingException, MessagingException {
		
		String msg = my_username + " (" + my_email + ") has " 
				+ (confirm?"confirmed":"declined") + " your invite";
		sendMail(target_email, "Online Board Confirm Notify", msg);
		
		return "User will be notified by email about your " + (confirm?"confirm":"decline"); 
	}
	
	public static void sendMail(String email, String subject, String message) 
			throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.transport.protocol","smtp");
		props.put("mail.smtp.port", ServerSettings.SMTP_PORT);
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.ssl.enable", "true");
		
        Session session = Session.getDefaultInstance(props, null);

        Transport tr = session.getTransport("smtp");
        tr.connect(ServerSettings.SMTP_HOST, ServerSettings.MAIL_USER, ServerSettings.MAIL_PASSWORD);
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(ServerSettings.MAIL_FROM, "Online Board Server"));
        msg.addRecipient(Message.RecipientType.TO,
                         new InternetAddress(email));
        msg.setSubject(subject);
        msg.setText(message);

        msg.saveChanges();
        tr.sendMessage(msg, msg.getAllRecipients());
        tr.close();
	}
}
