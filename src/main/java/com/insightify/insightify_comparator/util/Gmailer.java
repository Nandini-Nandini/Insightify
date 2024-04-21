package com.insightify.insightify_comparator.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import static com.google.api.services.gmail.GmailScopes.GMAIL_SEND;
import static jakarta.mail.Message.RecipientType.TO;


public class Gmailer{

    private static final String TEST_EMAIL = "insightify.comparator@gmail.com";
    private final Gmail service;

    public Gmailer() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("Test Mailer")
                .build();
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(Gmailer.class.getResourceAsStream("/client_secret_604185964432-t3a721sa1g8m1dfibv037lknpfa9os45.apps.googleusercontent.com.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void sendMail(String to, String subject, String message) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(TEST_EMAIL));
        email.addRecipient(TO, new InternetAddress(to));
        email.setSubject(subject);

        // Create a MimeMultipart object
        MimeMultipart mimeMultipart = new MimeMultipart();

        // Create a MimeBodyPart for the HTML content
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(message, "text/html; charset=utf-8"); // Set the HTML content and charset

        // Add the HTML MimeBodyPart to the MimeMultipart
        mimeMultipart.addBodyPart(htmlPart);

        // // Attach the logo image
        
        // MimeBodyPart logoPart = new MimeBodyPart();
        // DataSource logoSource = new FileDataSource("src/main/resources/static/logo.png");
        // logoPart.setDataHandler(new DataHandler(logoSource));
        // logoPart.setFileName("logo.png"); // Set the file name for the attachment
        // mimeMultipart.addBodyPart(logoPart);

        // // Attach the icon image
        // MimeBodyPart iconPart = new MimeBodyPart();
        // DataSource iconSource = new FileDataSource("src/main/resources/static/logo.png");
        // iconPart.setDataHandler(new DataHandler(iconSource));
        // iconPart.setFileName("sort-solid.svg"); // Set the file name for the attachment
        // mimeMultipart.addBodyPart(iconPart);

        // Set the content of the email to the MimeMultipart
        email.setContent(mimeMultipart);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message msg = new Message(); // Initialize the msg variable
        msg.setRaw(encodedEmail);

        try {
            // Message msg = new Message(); // Initialize the msg variable
            // msg.setRaw(Base64.encodeBase64URLSafeString(email.getBytes())); // Set the raw content of the email
            msg = service.users().messages().send("me", msg).execute();
            System.out.println("Message id: " + msg.getId());
            System.out.println(msg.toPrettyString());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 403) {
                System.err.println("Unable to send message: " + e.getDetails());
            } else {
                throw e;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Gmailer().sendMail("mraj602@gmail.com","Trial", """
            <!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Email Title</title>
    <style>
        /* Reset CSS */
        body, html {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }
        img {
            border: none;
            display: block;
            max-width: 100%;
            height: auto;
        }
        /* Main Styles */
        .container {
            width: 100%;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            background-color: #f2f2f2;
            padding: 20px;
            text-align: center;
        }
        .header h1 {
            color: #333;
        }
        .content {
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .footer {
            padding: 20px;
            background-color: #f2f2f2;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Your Company Name</h1>
        </div>
        <div class="content">
            <p>Hello,</p>
            <p>This is a sample HTML email with CSS styling and an image.</p>
            <img src="https://fastly.picsum.photos/id/981/536/354.jpg?hmac=0JmjK3D1d7p9SKriBqBRyen95I2ZnKN-CBnKu7iuRAk" alt="Sample Image">
        </div>
        <div class="footer">
            <p>&copy; 2024 Your Company. All rights reserved.</p>
        </div>
    </div>
</body>
</html>

                """);
    }

}