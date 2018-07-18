package com.github.carlhmitchell.failsafealert.email;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends javax.mail.Authenticator {
    static {
        Security.addProvider(new JSSEProvider());
    }

    private final String user;
    private final String password;
    private Session session;

    private MailSender(String user, String password, String mailhost, String auth, String port,
                       String sslport, String fallback, String quitwait) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();

        props.setProperty("mail.transport.protocol", "smtp"); //smtp
        props.setProperty("mail.host", mailhost); //"smtp.gmail.com
        props.setProperty("mail.smtp.auth", auth); //true
        props.setProperty("mail.smtp.port", port); //465
        props.setProperty("mail.smtp.socketFactory.port", sslport); //465
        props.setProperty("mail.smtp.socketFactory.class",
                  "javax.net.ssl.SSLSocketFactory"); //javax.net.ssl.SSLSocketFactory
        props.setProperty("mail.smtp.socketFactory.fallback", fallback); //false
        props.setProperty("mail.smtp.quitwait", quitwait); //false

        session = Session.getDefaultInstance(props, this);
        //Log.d("GmailSender", "Constructor completed with username " + user + " and password " + password);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) {
        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            } else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            }
            Transport.send(message);
            Log.d("MailSender", "Message Sent");
        } catch (Exception e) {
            Log.e("MailSender", e.getMessage(), e);
        }
    }

    public static class MailSenderBuilder {
        private final String nestedUser;
        private final String nestedPassword;
        private String nestedMailhost = "";
        private String nestedAuth = "true";
        private String nestedPort = "465";
        private String nestedSSLPort = "465";
        private String nestedFallback = "false";
        private String nestedQuitwait = "false";

        public MailSenderBuilder(final String newUser, final String newPassword) {
            this.nestedUser = newUser;
            this.nestedPassword = newPassword;
        }

        public MailSenderBuilder mailhost(String newMailhost) {
            this.nestedMailhost = newMailhost;
            return this;
        }

        public MailSenderBuilder auth(String newAuth) {
            this.nestedAuth = newAuth;
            return this;
        }

        public MailSenderBuilder port(String newPort) {
            this.nestedPort = newPort;
            return this;
        }

        public MailSenderBuilder sslPort(String newSSLPort) {
            this.nestedSSLPort = newSSLPort;
            return this;
        }

        public MailSenderBuilder fallback(String newFallback) {
            this.nestedFallback = newFallback;
            return this;
        }

        public MailSenderBuilder quitwait(String newQuitwait) {
            this.nestedQuitwait = newQuitwait;
            return this;
        }

        public MailSender build() {
            return new MailSender(
                    nestedUser, nestedPassword, nestedMailhost, nestedAuth, nestedPort, nestedSSLPort,
                    nestedFallback, nestedQuitwait);
        }
    }

    class ByteArrayDataSource implements DataSource {
        private final byte[] data;
        private String type;

        ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null) {
                return "application/octet-stream";
            } else {
                return type;
            }
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
