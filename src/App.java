import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import data_management.Database_handler;
import data_management.Data_preprocessor;



public class App {

    Database_handler handler;
    Data_preprocessor preprocessor;
    String[] categories;
    public App(){
        handler = new Database_handler();
        preprocessor = new Data_preprocessor();
        categories = new String[]{}; // Jannhavi input category values here
    }
    public static void main(String[] args) throws Exception {
        App app = new App();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of products to compare:");
        int products_to_compare = sc.nextInt();
        List<String> asins_to_compare = new ArrayList<>();
        String asin;
        int category_fit;
        List<String> asins_to_add_to_database = new ArrayList<>();
        List<List<String>> categories_to_add_to_database = new ArrayList<>();
        List<String> categories_input;
        Map<String, Object> result = new HashMap<>();
        
        for(int i=0; i<products_to_compare;i++){
            asin = sc.nextLine();
            asins_to_compare.add(asin);
            if(app.handler.asin_exists(asin)){

                result.put(asin, app.handler.get_result(asin));
            }else{
                System.out.println("Product information does not exist in information. Please provide basic product information\nEnter the number of categories product fits. All options for categories are:");
                System.out.println(app.categories);

                do{
                    System.out.println("Enter the number of categories the product fits in. The value should be between 1 and "+app.categories.length);
                    category_fit = sc.nextInt();
                }while(category_fit==0 || category_fit>app.categories.length);

                categories_input = new ArrayList<>();
                System.out.println("Enter "+app.categories.length+"product categories in each line");
                for(int j =0; j<category_fit; j++){
                    categories_input.add(sc.nextLine());
                }
                asins_to_add_to_database.add(asin);
                categories_to_add_to_database.add(categories_input);
            }
        }

    }
}
