package core;


import com.sun.mail.smtp.SMTPTransport;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * This class implements the ability of sending email.
 * It is only capable of sending text and cannot send attachments
 * Provides the ability to set the header and body
 */

public class Email {

    /**
     * The smtp server
     */
    private final String SMTP_SERVER = "smtp.googlemail.com";

    /**
     * password for the account from which email will be sent
     */
    private final String PASSWORD = "hopefullyThisWorks4Ever";

    /**
     * email from which email will be sent
     */
    private String from = "mycs370class@gmail.com";

    /**
     * To store the recipient
     */
    private String to = "";

    /**
     * To store the subject
     */
    private String subject = "";

    /**
     * To store the body text of the email
     */
    private String body = "";

    /**
     * Set the recipient
     * @param recipient The email address of the recipient
     */
    public void setRecipient(String recipient){
        to = recipient;
    }

    /**
     * Set the header text of the email
     * @param header header text
     */
    public void setHeader(String header){
        subject = header;
    }

    /**
     * To append data to the body of the email
     * @param line
     */
    public void addLineToBody(String line){
        body += line + "\n";
    }


    /**
     * Specifically to add an ArrayList of Item type as the body of the email
     * @param items An ArrayList containing Item objects
     * @param userName
     */
    public  void setEmailBody(ArrayList<Item> items, String userName){
        if( items == null ){
            return;
        }
        if( !userName.isEmpty() ) {
            body += "Username : " + userName;
        }

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

    /**
     * Create a session to request a new email transfer
     * @return ture if it was sent false otherwise
     */
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

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Override the toString
     * @return A string representation of this object
     */
    @Override
    public String toString() {
        return "Email{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}