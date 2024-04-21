package com.insightify.insightify_comparator.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class Email_management {
    private Gmailer gmailer;
    public Email_management() {
        try {
            gmailer = new Gmailer();
        } catch (Exception e) {
            Static_utils.log("Error creating gmailer" + e.getMessage(), "Email_management Constructor");
        }
        
    }
    

    public String generate_html_content_for_email(List<Map<String, Object>> result) {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML); // HTML5 option was deprecated in 3.0.0
        templateEngine.setTemplateResolver(resolver);
        // Prepare data for the template
        Context context = new Context();
        context.setVariable("result", result);
        // Process the template and return HTML as a string
        return templateEngine.process("result_email.html", context);
    }

    public boolean send_emails(String email, String html_content){
        try{
            gmailer.sendMail(email, "Your Amazon Product Comparison Scores are ready!", html_content);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            Static_utils.log("Error sending email" + e.getMessage(), "send_emails in Email_management");
            return false;
        }
    }

    public static String get_image_base64(String abs_path_of_image){
        try {
            // Read the image file into a byte array
            File file = new File(abs_path_of_image);
            FileInputStream fis = new FileInputStream(file);
            byte[] imageBytes = new byte[(int) file.length()];
            fis.read(imageBytes);
            fis.close();

            // Encode the byte array to base64 string
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // Print the base64 encoded string
            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void main(String[] args) {
        System.out.println(Email_management.get_image_base64("src/main/resources/static/logo.png"));
    }
}
