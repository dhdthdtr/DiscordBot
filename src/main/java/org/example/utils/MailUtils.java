package org.example.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtils {
    public static void sendMail(String from, String to, String subject, String body){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", true);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.defaultEncoding", "UTF-8");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(StringUtils.EMAIL_SERVER, StringUtils.PWD_SERVER);
            }
        });
        MimeMessage msg = new MimeMessage(session);
        try{
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, to);
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            Transport.send(msg);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
