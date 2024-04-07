package data_management;

import java.util.*;
import data_extractor.Amazon_scraper;

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

    public boolean preprocess_and_import_reviews(List<String> asin, List<Integer> review_id, List<String> review_title, List<String> review_text, List<Integer> review_star, List<String> user_profile_link) {
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

    private static List<Object> preprocess_reviews(List<String> asin, List<Integer> review_id, List<String> review_title, List<String> review_text, List<Integer> review_star, List<String> user_profile_link) {
        
        List<Object> processedData = new ArrayList<>();
        
        for (int i = 0; i < asin.size(); i++) {
            Map<String, Object> reviewInfo = new HashMap<>();
            String asin_text = asin.get(i); 
            int review_id_text = review_id.get(i);
            String review_title_text = review_title.get(i);
            String review_text_text = review_text.get(i);
            int review_star_text = review_star.get(i);
            String user_profile_link_text = user_profile_link.get(i);
        
            // Preprocess here
            Map<String, Object> review_info = new HashMap<>();
            reviewInfo.put("asin", asin_text);
            reviewInfo.put("review_id", review_id_text);
            reviewInfo.put("review_title", review_title_text);
            reviewInfo.put("review_text", review_text_text);
            reviewInfo.put("review_star", review_star_text);
            reviewInfo.put("user_profile_link", user_profile_link_text);
            processedData.add(review_info);
        }
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
            
                flag = flag && data_handler.add_product_info(asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price);

            } 
            else {
                util.Static_utils.log("Product in List<Object> is not of type Map", "add_product_to_database");
                flag = flag && false;
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
                int review_id = (int) review_map.get("review_id");
                String review_title = (String) review_map.get("review_title");
                String review_text = (String) review_map.get("review_text");
                int review_star = (int) review_map.get("review_star");
                String user_profile_link = (String) review_map.get("user_profile_link");

                flag = flag && data_handler.add_review_info(asin, review_id,review_title,review_text,review_star,user_profile_link);
           } 
           else {
               util.Static_utils.log("Product in List<Object> is not of type Map", "add_product_to_database");
               flag = flag && false;
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
                flag = flag && data_handler.add_product_category(asins.get(i), category_of_one_product.get(j));
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

        List<String> asins_review = new ArrayList<>();
        List<Integer> review_id = new ArrayList<>();
        List<String> review_title = new ArrayList<>();
        List<String> review_text = new ArrayList<>();
        List<Integer> review_star = new ArrayList<>();
        List<String> user_profile_link = new ArrayList<>();

        for(Object review_info_obj : review_infos){
            Map<String, Object> review_info = (Map<String, Object>) review_info_obj;
            asins_review.add((String) review_info.get("asins"));
            review_id.add((Integer) review_info.get("review_ids"));
            review_title.add((String) review_info.get("review_titles"));
            review_text.add((String) review_info.get("review_texts"));
            review_star.add((Integer) review_info.get("review_star"));
            user_profile_link.add((String) review_info.get("user_profile_link"));
        }
        boolean flag = true;
        flag = flag && preprocess_and_import_products(asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price, categories);
        flag = flag && preprocess_and_import_reviews(asins_review, review_id, review_title, review_text, review_star, user_profile_link);
        return flag;
    }

    public static void main(String[] args) {
        Data_preprocessor preprocessor = new Data_preprocessor();
        List<String> to_check_asins = new ArrayList<>();
        List<List<String>> to_check_categories = new ArrayList<>();
        List<String> categories_1 = new ArrayList<>();
        List<String> categories_2 = new ArrayList<>();
        to_check_asins.add("B0BZCR6TNK");
        to_check_asins.add("B07WHSR1NR");
        categories_1.add("Mobiles");
        categories_2.add("Mobiles");
        to_check_categories.add(categories_1);
        to_check_categories.add(categories_2);
        preprocessor.extract_and_preprocess(null, null);
    }
}
