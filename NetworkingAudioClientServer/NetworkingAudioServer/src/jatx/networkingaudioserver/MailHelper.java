package jatx.networkingaudioserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailHelper {
	static final boolean USE_SCRIPT = false;
	
	static final String FROM = "service@tabatsky.ru";
	static final String SERVER_URL = "http://tabatsky.ru/networkingaudio";
	
	static final String USER = "service@tabatsky.ru";
	static final String PASS = "";
	static final String SMTP_HOST = "smtp.yandex.ru";
	static final String SMTP_PORT = "465";
	
	public static String sendMail(String email, String confirm) 
			throws IOException, InterruptedException, MessagingException {
		if (USE_SCRIPT) {
			return sendWithScript(email, confirm);
		} else {
			return sendWithJavaMail(email, confirm);
		}
	}
	
	private static String sendWithScript(String email, String confirm) 
			throws IOException, InterruptedException {
		Runtime runtime = Runtime.getRuntime();
		String[] command = {"/common_scripts/sendConfirm",email,confirm};
		Process process = runtime.exec(command);
		int exitCode = process.waitFor();
		if (exitCode==0) {
			return "Check your email for confirmation";
		} else {
			return "Error sending email: script";
		}
	}
	
	private static String sendWithJavaMail(String email, String confirm) 
			throws UnsupportedEncodingException, MessagingException {
		Properties props = new Properties();
		props.put("mail.transport.protocol","smtp");
		props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.ssl.enable", "true");
		
        Session session = Session.getDefaultInstance(props, null);

        Transport tr = session.getTransport("smtp");
        tr.connect(SMTP_HOST, USER, PASS);
        
        String msgBody = SERVER_URL+"/confirm?confirm="+confirm;
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, "Networking Audio Server"));
        msg.addRecipient(Message.RecipientType.TO,
                         new InternetAddress(email));
        msg.setSubject("Networking Audio e-mail confirmation");
        msg.setText(msgBody);

        msg.saveChanges();
        tr.sendMessage(msg, msg.getAllRecipients());
        tr.close();
           
        return "Check your email for confirmation";
	}
}
