package com.insightify.insightify_comparator.data_management;

import java.util.*;


import com.insightify.insightify_comparator.data_extractor.Amazon_scraper;
import com.insightify.insightify_comparator.util.*;
public class Data_preprocessor {

     // Global variable to store the DataHandler object
     private Database_handler data_handler;
     

     public Data_preprocessor(){
        // Initialize DataHandler object
     data_handler = new Database_handler();
     }
     

    public boolean preprocess_and_import_products(List<String> asin, List<String> prod_link, List<String> name, List<Float> prod_overall_rating, List<String> prod_brand, List<Integer> no_rating, List<Integer> no_reviews, List<Float> prod_price,List<List<String>> categories) {
        List<Object> processedData = preprocess_products(asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price);
        boolean res1 = add_product_to_database(processedData);
        boolean res2 = add_category_to_database(asin, categories);
        return res1 && res2;
    }

    public boolean preprocess_and_import_reviews(List<String> asin, List<String> review_id, List<String> review_title, List<String> review_text, List<String> review_star, List<String> user_profile_link) {
        List<Object> processedData = preprocess_reviews(asin, review_id, review_title, review_text, review_star, user_profile_link);
        return add_review_to_database(processedData);
    }
    
    private static List<Object> preprocess_products(List<String> asin, List<String> prod_link, List<String> name, List<Float> prod_overall_rating, List<String> prod_brand, List<Integer> no_rating, List<Integer> no_reviews, List<Float> prod_price) {
        
        List<Object> processedData = new ArrayList<>();
        for (int i = 0; i < asin.size(); i++) {
            Map<String, Object> productInfo = new HashMap<>();
            String asin_text = asin.get(i);  
            String prod_link_text = prod_link.get(i);
            String name_text = name.get(i);
            Float prod_overall_rating_text = prod_overall_rating.get(i);
            String prod_brand_text = prod_brand.get(i);
            int no_rating_text = no_rating.get(i);
            int no_reviews_text = no_reviews.get(i);
            Float prod_price_text = prod_price.get(i);


            // preprocess here for all these variables
            productInfo.put("asin", asin_text);
            productInfo.put("prod_link", prod_link_text); 
            productInfo.put("name", name_text);    // string 
            productInfo.put("prod_overall_rating", prod_overall_rating_text); // float
            productInfo.put("brand", prod_brand_text);
            productInfo.put("no_rating", no_rating_text);  // int
            productInfo.put("no_reviews", no_reviews_text); // int
            productInfo.put("prod_price", prod_price_text);  //float
            
            processedData.add(productInfo);
        }
        return processedData;
    }

    private static List<Object> preprocess_reviews(List<String> asin, List<String> review_id, List<String> review_title, List<String> review_text, List<String> review_star, List<String> user_profile_link) {
        
        List<Object> processedData = new ArrayList<>();
        
        for (int i = 0; i < asin.size(); i++) {
            String asin_text = asin.get(i); 
            String review_id_text = review_id.get(i);
            String review_title_text = review_title.get(i);
            String review_text_text = review_text.get(i);
            int review_star_text = -1;
            try{
                review_star_text = Integer.parseInt(review_star.get(i).split("\\.")[0]);
            }catch(Exception e){
                Static_utils.log(e.toString() + " value of asin: "+asin_text+" value of user_profile"+user_profile_link.get(i), "preprocess reviews");
            }
            
            String user_profile_link_text = user_profile_link.get(i);
            try{
                review_title_text = review_title_text.split("stars ")[1].replaceAll("[^\\p{L}\\d\\s\\p{Punct}]", "");
            }catch(Exception e){
                Static_utils.log(e.toString()+ "value of asin"+ asin_text+" value of user_profile"+user_profile_link.get(i), "preprocess reviews");
            }

            try{
                review_text_text = review_text_text.replaceAll("[^\\p{L}\\d\\s\\p{Punct}]", "");
            }catch(Exception e){
                Static_utils.log(e.toString()+ "value of asin"+ asin_text+" value of user_profile"+user_profile_link.get(i), "preprocess reviews");
            }
            
            
        
            // Preprocess here
            Map<String, Object> review_info = new HashMap<>();
            review_info.put("asin", asin_text);
            review_info.put("review_id", review_id_text);
            review_info.put("review_title", review_title_text);
            review_info.put("review_text", review_text_text);
            review_info.put("review_star", review_star_text);
            review_info.put("user_profile_link", user_profile_link_text);
            processedData.add(review_info);
        }
        System.out.println("size of reviews: "+processedData.size());
        return processedData;
    }
    
    @SuppressWarnings("unchecked")
    private boolean add_product_to_database(List<Object> products) { 
        Map<String, Object> product_map = new HashMap<>();
        boolean flag = true;
        for (Object product : products) {
            if (product instanceof Map) {
                 product_map= (Map<String, Object>) product;
                 String asin = (String) product_map.get("asin");
                String prod_link = (String) product_map.get("prod_link");
                String name = (String) product_map.get("name");
                Float prod_overall_rating = (Float) product_map.get("prod_overall_rating");
                String prod_brand = (String) product_map.get("brand");
                int no_rating = (int) product_map.get("no_rating");
                int no_reviews = (int) product_map.get("no_reviews");
                Float prod_price = (Float) product_map.get("prod_price");
            
                flag = data_handler.add_product_info(asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price) && flag;

            } 
            else {
                Static_utils.log("Product in List<Object> is not of type Map", "add_product_to_database");
                flag = false;
            }    
            
        }
        return flag; 
    }

    @SuppressWarnings("unchecked")
    private boolean add_review_to_database(List<Object> reviews) {
        Map<String, Object> review_map = new HashMap<>();
        boolean flag = true;
        for (Object review : reviews) {
            if (review instanceof Map) {
                review_map= (Map<String, Object>) review;
                String asin = (String) review_map.get("asin");
                String review_id = (String) review_map.get("review_id");
                String review_title = (String) review_map.get("review_title");
                String review_text = (String) review_map.get("review_text");
                int review_star = (int) review_map.get("review_star");
                String user_profile_link = (String) review_map.get("user_profile_link");

                flag = data_handler.add_review_info(asin, review_id,review_title,review_text,review_star,user_profile_link) && flag;
           } 
           else {
               Static_utils.log("Product in List<Object> is not of type Map", "add_product_to_database");
               flag = false;
           }   
            
        }
        return flag; 
    }

    private boolean add_category_to_database(List<String> asins, List<List<String>> categories){
        List<String> category_of_one_product;
        boolean flag = true;
        for(int i = 0; i<asins.size(); i++){
            category_of_one_product = categories.get(i);
            for (int j=0; j<category_of_one_product.size(); j++){
                flag = data_handler.add_product_category(asins.get(i), category_of_one_product.get(j)) && flag;
            }
        }
        return flag;
    }

    @SuppressWarnings("unchecked")
    public boolean extract_and_preprocess(List<String> asins, List<List<String>> categories)
    {
        Amazon_scraper scraper=new Amazon_scraper(asins);
        Map<String, Object> result = scraper.get_result();
        List<Object> product_infos = (List<Object>) result.get("product_info");
        List<Object> review_infos = (List<Object>) result.get("review_info");
        
        List<String> asin = new ArrayList<>();
        List<String> prod_link = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<Float> prod_overall_rating = new ArrayList<>();
        List<String> prod_brand = new ArrayList<>();
        List<Integer> no_rating = new ArrayList<>();
        List<Integer> no_reviews = new ArrayList<>();
        List<Float> prod_price = new ArrayList<>();

        for(Object product_info_obj : product_infos){
            Map<String, Object> product_info = (Map<String, Object>) product_info_obj;
            asin.add((String)product_info.get("asin"));
            prod_link.add((String)product_info.get("prod_link"));
            name.add((String)product_info.get("name"));
            prod_overall_rating.add((Float)product_info.get("prod_overall_rating"));
            prod_brand.add((String)product_info.get("brand"));
            no_rating.add((Integer)product_info.get("no_rating"));
            no_reviews.add((Integer)product_info.get("no_reviews"));
            prod_price.add((Float)product_info.get("prod_price"));
        }
        boolean flag = true;
        flag = preprocess_and_import_products(asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price, categories) && flag;
        
        for(Object review_info_obj : review_infos){
            Map<String, Object> review_info = (Map<String, Object>) review_info_obj;
            flag = preprocess_and_import_reviews((List<String>)review_info.get("asins"), (List<String>)review_info.get("review_ids"), (List<String>)review_info.get("review_titles"), (List<String>)review_info.get("review_texts"), (List<String>)review_info.get("review_star"), (List<String>)review_info.get("user_profile_link")) && flag;
            
        }
        
        
        
        return flag;
    }

    public static void main(String[] args) {
        Data_preprocessor preprocessor = new Data_preprocessor();
        List<String> to_check_asins = new ArrayList<>();
        List<List<String>> to_check_categories = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        to_check_asins = new ArrayList<>();
        to_check_categories = new ArrayList<>();
        categories = new ArrayList<>();
        to_check_asins.add("B0BZCWLJHK");
        categories.add("Mobiles");
        categories.add("Electronics");
        to_check_categories.add(categories);
        preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);
    }
    // public boolean extract_and_preprocess(List<String> asins, List<List<String>> categories)
    // {
    //     return add_category_to_database(asins, categories);
        
    // }



    // public static void main(String[] args) {
    //     Data_preprocessor preprocessor = new Data_preprocessor();
    //     List<String> to_check_asins = new ArrayList<>();
    //     List<List<String>> to_check_categories = new ArrayList<>();
    //     List<String> categories = new ArrayList<>();

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BZCWLJHK");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CGDQ9SN7");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C45N5VPT");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C9JDHZTB");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CBRN65NP");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C788SHHC");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CHRQJQPD");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    
        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C9J97Z2D");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07WGPJPR3");
    //     // categories.add("Mobiles");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // // FOR CATEGORY COMPUTERS(AND ACCESSORIES)

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08N5T6CZ6");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09MT6SWDL");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BDYW3RN3");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00OKAFZHO");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01J0XWYKQ");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B009VCGPSY");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B098JYT4SY");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08X2T2M8G");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07MWBB3J4");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BP2M7CCS");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09R1MMMTH");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C3RF3HT3");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();

    //     // to_check_asins.add("B00ZYLMQH0");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B097TC1F6J");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BV94KBPF");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // // FOR CATEGORY TELEVISIONS(AND ACCESSORIES)

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C4YBQBRV");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C3HDCJ28");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CH31C1BR");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CG5STQFQ");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C82ZHYQ8");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CH31V44H");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C82ZHYQ8");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CN2LTL18");
    //     // categories.add("Computers");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B8YTGC23");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C1H9Z4DC");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C3RXSTD1");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CN2V8JSQ");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C1HCJVT5");
    //     // categories.add("Televisions");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // // FOR CATEGORY Women's Fashion - Clothes

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07J5QYXGY");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08BNDJRPF");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00OLYKA1K");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BVJ1PD38");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07JVPKJYS");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09HMN77PR");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B010FMMZDC");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01BSCPMD8");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01BSCWHUO");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BDFMGWQP");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C7QL99QB");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B097TDWWGF");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08R41F6D5");
    //     // categories.add("Clothes");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // FOR CATEGORY Men's Fashion - Clothes

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B4K95ZLW");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01LCXGEN0");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07YT18PP1");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07KW69JGG");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07N6C6S2R");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08B3Z86WV");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B014213DS8");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01ENC05H8");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00W04OTYM");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07X639KSP");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B071XHWZ8Y");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07X2YL1RJ");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07D4HB1Q9");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BYSVGZDC");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B079VST3GQ");
    //     // categories.add("Clothes");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // // FOR CATEGORY Men's Fashion/ Women's Fashion - Footwear

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07Y5C47JT");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B077N7DDL1");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B083NZ47Y8");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00WM6617Q");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09PVFJ2P4");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07S7MTB5G");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08WQ4M4W4");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08LR87PRN");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08DXL36HK");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07S6FSTX2");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08K4QGW9S");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B4SCX1DK");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07JWJ3462");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01H6ZR0BS");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0819RWQWR");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // FOR CATEGORY Men's Fashion/ Women's Fashion - Footwear

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07Y5C47JT");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B077N7DDL1");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B083NZ47Y8");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00WM6617Q");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09PVFJ2P4");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07S7MTB5G");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08WQ4M4W4");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08LR87PRN");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08DXL36HK");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07S6FSTX2");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08K4QGW9S");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B4SCX1DK");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07JWJ3462");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01H6ZR0BS");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0819RWQWR");
    //     // categories.add("Footwear");
    //     // categories.add("Women's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // FOR CATEGORY SPORTS
        
    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00INTOOMK");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01JUU8ETO");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09ST2ZFPH");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00ICCYF0E");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00ICCYIRO");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07B25N2DF");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01566VOSO");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08DS5SC8H");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08WHD296C");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B000FI8ER8");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00JXRR75C");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07BY89TQY");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0838FSMPM");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07B3VM8YG");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07PJVFH4C");
    //     // categories.add("Sports");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);
        

    //     // Jannhavi
    //     //Kitchen, Home,Pet

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07P5TXZ9V");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00VK5M5E8");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0091X7RVM");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01LWYDEQ7");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07F5QVYN8");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01LYBZX6Y");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);





    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09FSMN8QS");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01M0GLQ4L");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01BVDS1BE");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08J42GB5Q");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B018TQR2CA");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08VWZXTWY");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07T4L7MLJ");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B083P71WKK");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // categories.add("Electronics");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B078X7KLTH");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07JQ1FFYB");
    //     // categories.add("Home");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B086394NY5");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B071173FS8");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0927T6DS6");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01BY6ZOQI");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07951XLHV");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07PXLXTCS");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08DXX97RT");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08GS6YM1Z");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08LYGR8N2");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B088Y6TBJC");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09JGKNV2Q");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07D4T4YT2");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08M5YYG57");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07TJG8Q9W");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B004WNB6FY");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01LTI1KJ6");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0725Z41ZF");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C7BNG8S2");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BY85QGZ7");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07HR916T2");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00LHS8I3A");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07HBFY5VJ");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08FJ655HL");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B005LA8RH2");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07JQJFCS2");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0CKQX6THY");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01IEXX5HE");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00MW8G3YU");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07WDLD91J");
    //     // categories.add("Pets");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B072XW1FSP");
    //     // categories.add("Pet");
    //     // categories.add("Kitchen");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09TPFTJNN");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07K4BFQK1");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B6Y3FNV7");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B089R9SBPJ");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08CSHBPD5");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08QSTTSNH");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B006LXDVTM");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07XFF28KR");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07WSQQ68N");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09FMJ8WWR");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07WSQ97ZK");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BPC8KG73");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07SNHSZGN");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B000LQUA6M");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00ENZT4M8");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0C9HVT1WZ");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07SZ243VZ");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07WGMXX8Y");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00DRE1D1Q");
    //     // categories.add("Personal care");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00RRZ9JJO");
    //     // categories.add("Personal care");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // Health

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07NJK4LMB");
    //     // categories.add("Health");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0757MM647");
    //     // categories.add("Health");
    //     // categories.add("Fitness");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B000QSNYGI");
    //     // categories.add("Health");
    //     // categories.add("Fitness");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00HNQ7MAK");
    //     // categories.add("Health");
    //     // categories.add("Fitness");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01DQV8BIM");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01MS489AE");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09RTHZR26");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00IFWE39Y");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01JCFDX4S");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07FS7C7B8");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01L6ZCV2C");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00I4S8M82");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08C5QWP1T");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07DKJ818C");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0177FCZ2G");
    //     // categories.add("Health");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // Personal care

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07M9XYH9K");
    //     // categories.add("Health");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00HT03SJY");
    //     // categories.add("Beauty");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BXNVPRGS");
    //     // categories.add("Electronics");
    //     // categories.add("Appliances");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0B9MGR6C8");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B09798GDLC");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07S7R4JFK");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B086Q88V9H");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07VGXVZ63");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00S6KDGNE");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07548J9WC");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B087QK1P79");
    //     // categories.add("Personal Care");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // Grocery

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B01CE7D8BY");
    //     // categories.add("Health");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07RZ7RPRD");
    //     // categories.add("Fitness");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00ZX1PO5M");
    //     // categories.add("Health");
    //     // categories.add("Grocery");
    //     // categories.add("Personal Care");
    //     // categories.add("Beauty");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00NYZTGEO");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00TX97G24");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00GX9TS6O");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00QPS8BAW");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B00PNQYFFM");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07533TMV9");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07KXGFGSS");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07B4KQRZG");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07CDY1XJZ");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B074N7VHV4");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07CBKR1L7");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07575FPC3");
    //     // categories.add("Grocery");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BR4176V7");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B08X72GY5Q");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BR3WHPQP");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BSLNLG9J");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BSRVNCYC");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BSDQGYNP");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BSRVP834");
    //     // categories.add("Footwear");
    //     // categories.add("Men's Fashion");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B07R3WXXH5");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BR3V1SQT");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BR3Y5X5G");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     to_check_asins = new ArrayList<>();
    //     to_check_categories = new ArrayList<>();
    //     categories = new ArrayList<>();
    //     to_check_asins.add("B0BSRTWX56");
    //     categories.add("Refrigerator");
    //     categories.add("Electronics");
    //     categories.add("Home");
    //     to_check_categories.add(categories);
    //     preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BSRVXJ8H");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BTHLCK15");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);


    //     // to_check_asins = new ArrayList<>();
    //     // to_check_categories = new ArrayList<>();
    //     // categories = new ArrayList<>();
    //     // to_check_asins.add("B0BV27HC9R");
    //     // categories.add("Refrigerator");
    //     // categories.add("Electronics");
    //     // categories.add("Home");
    //     // to_check_categories.add(categories);
    //     // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);



        // to_check_asins = new ArrayList<>();
        // to_check_categories = new ArrayList<>();
        // categories = new ArrayList<>();
        // to_check_asins.add("B0BR3Z729L");
        // categories.add("Refrigerator");
        // categories.add("Electronics");
        // categories.add("Home");
        // to_check_categories.add(categories);
        // preprocessor.extract_and_preprocess(to_check_asins, to_check_categories);

    //     // Pass the transformer on layer


    // }


}
