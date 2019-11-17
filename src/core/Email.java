package core;


import com.sun.mail.smtp.SMTPTransport;

import javax.activation.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class Email {

    // for example, smtp.mailgun.org
    private final String SMTP_SERVER = "smtp.googlemail.com";
    private final String PASSWORD = "hopefullyThisWorks4Ever";

    private String from = "mycs370class@gmail.com";
    private String to = "";

    private String subject = "";
    private String body = "";

    public void setRecipient(String recipient){
        to = recipient;
    }

    public void setHeader(String header){
        subject = header;
    }

    public  void setEmailBody(ArrayList<Item> items, String userName){
        if( items == null ){
            return;
        }
        body += "Username : "  + userName;
        body += "\n\n";
        for( Item item : items ){
            body += "\nDescription:   " + item.getDescription();
            body += "\nCategory:      " + item.getCategory();
            body += "\nPrice:         " + item.getPrice();
            body += "\nTimestamp:     " + item.getTimeStamp();
            body += "\nImg url:       " + item.getImgScr();
            body += "\n\n";
        }
    }

    public Boolean sendEmail(){
        Properties prop = System.getProperties();
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "25"); // default port 25

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            // Set sender
            msg.setFrom(new InternetAddress(from));
            // set recipient
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            //set the subject subject
            msg.setSubject(subject);
            // set the body text
            msg.setText(body);
            msg.setSentDate(new Date());
            // Get SMTPTransport
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            // connect
            t.connect(SMTP_SERVER, from, PASSWORD);
            // send
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();

        } catch (MessagingException e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public static void main(String[] args) {





    }
}