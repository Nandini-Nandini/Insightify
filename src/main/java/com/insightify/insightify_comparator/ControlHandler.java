package com.insightify.insightify_comparator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.insightify.insightify_comparator.data_management.Data_preprocessor;
import com.insightify.insightify_comparator.data_management.Database_handler;
import com.insightify.insightify_comparator.util.Email_management;
import com.insightify.insightify_comparator.util.Static_utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ControlHandler {

    private final Database_handler handler;
    private final Data_preprocessor preprocessor;
    private final Email_management email_manager;

    public ControlHandler(){
        this.handler = new Database_handler();
        this.preprocessor = new Data_preprocessor();
        this.email_manager = new Email_management();
    }
        
    @RequestMapping("/")
    // @ResponseBody
    public String index(){
        return "index";
    }

    @RequestMapping("/about_us")
    // @ResponseBody
    public String about_us(){
        return "about_contact";
    }

    @PostMapping("/get_result")
    public String calculate_result(@RequestParam("asin[]") String[] asins, Model model) {
        boolean all_exists = true;
        boolean atleast_exist = false;
        boolean f1 = false;

        
        List<String> asins_not_exist = new ArrayList<>();
        List<String> asins_exist = new ArrayList<>();
        List<String> asins_list = new ArrayList<>();

        for (int i = 0; i<asins.length; i++){
            f1 = handler.asin_exists(asins[i]);
            if (f1){
                asins_exist.add(asins[i]);
                atleast_exist = true;
            }
            else{
                asins_not_exist.add(asins[i]);
                all_exists = false;
            }
            asins_list.add(asins[i]);
        }

        List<Map<String, Object>> result = handler.get_product_score_data_for_webpage(asins_exist);
        model.addAttribute("asins_existing", result);
        model.addAttribute("asins_not_existing", asins_not_exist);
        model.addAttribute("all_asins_exist", all_exists);
        model.addAttribute("atleast_asins_exist", atleast_exist);
        model.addAttribute("all_asins", asins_list);
        
        return "result";
    }

    @PostMapping("/send_to_email")
    public String sendToEmail(@RequestParam("email") String email, @RequestParam("asins") String asins_str, Model model) {
        // Assuming you have a method to send email and you want to run it in a new thread
        Thread emailThread = new Thread(() -> {
            boolean f1 = true;
            boolean all_exists = true;
            List<String> asins_not_exist = new ArrayList<>();
            List<String> asins_exist = new ArrayList<>();
            List<String> all_asins_list = new ArrayList<>();
            String[] asins = asins_str.substring(1, asins_str.length() - 1).replace(" ", "").split(",");

            for (int i = 0; i<asins.length; i++){
                f1 = handler.asin_exists(asins[i]);
                if (f1){
                    asins_exist.add(asins[i]);
                }
                else{
                    asins_not_exist.add(asins[i]);
                    all_exists = false;
                }
                all_asins_list.add(asins[i]);
            }

            if (!all_exists){
                List<List<String>> to_add_categories = new ArrayList<>();
                List<String> categories = new ArrayList<>();
                categories.add("Undefined");
                for (int i = 0; i<asins_not_exist.size(); i++){
                    to_add_categories.add(categories);
                }
                System.out.println(asins_not_exist);
                preprocessor.extract_and_preprocess(asins_not_exist, to_add_categories);
                handler.process_sentiments_of_unanalyzed_reviews();
                handler.calculate_product_scores();
            }
            List<Map<String, Object>> result = handler.get_product_score_data_for_webpage(all_asins_list);
            String email_html = "";
            try{
                email_html = email_manager.generate_html_content_for_email(result);
            }catch(Exception e){
                e.printStackTrace();
                Static_utils.log("Cannot get post content from render result "+e.getMessage(), "sendToEmail in ControlHandler");
                
            }
            
            boolean emailSent = email_manager.send_emails(email, email_html);
            System.out.println("Email sending status: "+ emailSent);
            // Optionally, update the model or perform any other actions based on the result
        });
        emailThread.start(); // Start the new thread
    return "index"; // Redirect to the result page
    }


    


}
