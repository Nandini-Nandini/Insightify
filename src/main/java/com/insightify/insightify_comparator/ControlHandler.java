package com.insightify.insightify_comparator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.insightify.insightify_comparator.data_management.Database_handler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class ControlHandler {

    private Database_handler handler;

    public ControlHandler(){
        this.handler = new Database_handler();
    }

    @RequestMapping("/")
    // @ResponseBody
    public String index(){
        return "index";
    }

    @PostMapping("/get_result")
    public String calculate_result(@RequestParam("asin[]") String[] asins, Model model) {
        boolean all_exists = true;
        boolean[] asins_exist = new boolean[asins.length];
        for (int i = 0; i<asins.length; i++){
            asins_exist[i] = handler.asin_exists(asins[i]);
            all_exists = asins_exist[i] && all_exists;
        }

        model.addAttribute("asins", asins);
        model.addAttribute("asins_exist", asins_exist);
        model.addAttribute("all_asins_exist", all_exists);
        
        return "result";
    }
    


}
