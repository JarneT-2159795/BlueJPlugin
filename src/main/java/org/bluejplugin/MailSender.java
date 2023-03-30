package org.bluejplugin;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/* Class to demonstrate the use of Gmail Create Draft with attachment API */
public class MailSender
{
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH;
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_COMPOSE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final NetHttpTransport HTTP_TRANSPORT;
    private static final Gmail service;
    private static final String sender;
    public static final String name;

    static
    {
        try
        {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            TOKENS_DIRECTORY_PATH = BlueJManager.getInstance().getBlueJ().getUserConfigDir().getAbsolutePath();
            service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                    .setApplicationName("BlueJPlugin")
                    .build();
            sender = service.users().getProfile("me").execute().getEmailAddress();
            String firstName = sender.split("@")[0].split("\\.")[0];
            String lastName = sender.split("@")[0].split("\\.")[1];
            name = firstName.substring(0, 1).toUpperCase() + firstName.substring(1) + " " +
                    lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Send an email with attachment
     *
     * @param toEmailAddress - Email address of the recipient
     * @param subject - Subject of the email
     * @param bodyText - Body of the email
     * @param file - File to be attached
     *
     * @throws MessagingException - if a wrongly formatted address is encountered
     * @throws IOException - if service account credentials file not found
     */
    public static void sendMail(String toEmailAddress, String subject, String bodyText, File file)
            throws MessagingException, IOException
    {
        // Encode as MIME message
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(sender));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(toEmailAddress));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);
        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());
        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        // Encode and wrap the MIME message into a gmail message
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        // Send the message
        try
        {
            service.users().messages().send("me", message).execute();
        } catch (GoogleJsonResponseException e)
        {
            GoogleJsonError error = e.getDetails();
            System.out.println("Sending message failed.");
            System.out.println("Error code: " + error.getCode());
            System.out.println("Error message: " + error.getMessage());
            System.out.println("Error details: " + error.getErrors());
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return An authorized Credential object.
     *
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials() throws IOException
    {
        // Load client secrets.
        InputStream in = MailSender.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null)
        {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                MailSender.HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}