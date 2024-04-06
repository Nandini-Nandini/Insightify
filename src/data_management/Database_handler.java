package data_management;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import java.util.List;

import util.Static_utils;



public class Database_handler {
    private Connection connection;

    // Default Constructor
    public Database_handler() {
        // Default database username and password
        String username = "root"; // Change this to your MySQL username
        String password = "admin123"; // Change this to your MySQL password
        String hostname = "localhost"; // Change this if your MySQL server is hosted elsewhere
        
        establishConnection(hostname, username, password);
    }

    // Constructor with Parameters
    public Database_handler(String hostname, String username, String password) {
        establishConnection(hostname, username, password);
    }

    private void establishConnection(String hostname, String username, String password) {
        try {
            // Establishing connection with the database
            String url = "jdbc:mysql://" + hostname + ":3306/?useSSL=false&allowPublicKeyRetrieval=true";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
 
            // Check if the database exists, if not, create it
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + "Insightify");

            // Connect to the specified database
            url = "jdbc:mysql://" + hostname + ":3306/" + "Insightify" + "?useSSL=false&allowPublicKeyRetrieval=true";
            connection = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            Static_utils.log("Connection to database failed. Error: " + e.getMessage(), "establishConnection");
        } catch (Exception e) {
            Static_utils.log("JDBC Class not found error :"+e.getMessage(), "establishConnection");
        }
    }


    // Create Function for prodcuts
    public void create_table_products() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS products (" +
            "prod_id INT AUTO_INCREMENT UNIQUE ," +
            "asin VARCHAR(255) PRIMARY KEY," +
            "prod_link VARCHAR(255)," +
            "name VARCHAR(255)," +
            "prod_overall_rating DOUBLE," +
            "prod_brand VARCHAR(255)," +
            "no_rating INT," +
            "no_reviews INT," +
            "prod_price DOUBLE)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            Static_utils.log("Error creating Table1: " + e.getMessage(),"create_table_products");
        }
    }

    // Create Function for reviews table
    public boolean create_table_reviews() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INT AUTO_INCREMENT UNIQUE ," +
                    "asin VARCHAR(255)," +
                    "review_id INT PRIMARY KEY," +
                    "review_title VARCHAR(255)," +
                    "review_text TEXT," +
                    "review_star INT," +
                    "user_profile_link VARCHAR(255)," +
                    "FOREIGN KEY (asin) REFERENCES products(asin))";
            stmt.executeUpdate(sql);
            return true;
            
        }
        
        catch (SQLException e) {
            Static_utils.log("Error creating reviews table: " + e.getMessage(),"create_table_reviews");
            return false;

        }
    }


    //create function for product_category

    public boolean create_table_product_category() {
        try {
            Statement stmt = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS product_category (" +
                    "asin VARCHAR(255) NOT NULL," +
                    "category VARCHAR(255) NOT NULL," +
                    "PRIMARY KEY (asin, category)," +
                    "FOREIGN KEY (asin) REFERENCES products(asin) ON DELETE CASCADE)";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error creating product_category table: " + e.getMessage(), "create_table_product_category");
            return false;
        }
    }


    // CRUD Functions for products
    // Create
    public boolean  add_product_info(String asin, String prod_link, String name, double prod_overall_rating, String prod_brand, int no_rating, int no_reviews,double prod_price ) {
        try {
            create_table_products();
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO products (asin, prod_link, name, prod_overall_rating, prod_brand, no_rating, no_reviews, prod_price) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pstmt.setString(1, asin);
            pstmt.setString(2, prod_link);
            pstmt.setString(3, name);
            pstmt.setDouble(4, prod_overall_rating);
            pstmt.setString(5, prod_brand);
            pstmt.setInt(6, no_rating);
            pstmt.setInt(7, no_reviews);
            pstmt.setDouble(8, prod_price);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error adding product information: " + e.getMessage(),"add_product_info");
            return false;
        }
    }

    

    // Read
    public boolean get_prod_info() {
        List<Map<String, Object>> productsList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while (rs.next()) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("Product ID", rs.getInt("prod_id"));
                productMap.put("ASIN", rs.getString("asin"));
                productMap.put("Product Link", rs.getString("prod_link"));
                productMap.put("Name", rs.getString("name"));
                productMap.put("Overall Rating", rs.getDouble("prod_overall_rating"));
                productMap.put("Category", rs.getString("prod_brand"));
                productMap.put("Number of Ratings", rs.getInt("no_rating"));
                productMap.put("Number of Reviews", rs.getInt("no_reviews"));
                productMap.put("Price", rs.getDouble("prod_price"));

                productsList.add(productMap);
            }
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error reading products table: " + e.getMessage(), "get_prod_info");
            return false;
        }
    }
    
    public boolean get_prod_info(String asin) {
        List<Map<String, Object>> productsList = new ArrayList<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT * FROM products WHERE asin = ?"
            );
            pstmt.setString(1, asin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("Product ID", rs.getInt("prod_id"));
                productMap.put("ASIN", rs.getString("asin"));
                productMap.put("Product Link", rs.getString("prod_link"));
                productMap.put("Name", rs.getString("name"));
                productMap.put("Overall Rating", rs.getDouble("prod_overall_rating"));
                productMap.put("Category", rs.getString("prod_brand"));
                productMap.put("Number of Ratings", rs.getInt("no_rating"));
                productMap.put("Number of Reviews", rs.getInt("no_reviews"));
                productMap.put("Price", rs.getDouble("prod_price"));

                productsList.add(productMap);
            }
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error reading products table: " + e.getMessage(), "get_prod_info");
            return false;
        }
    }
    
    
    // Update
    public boolean update_prod_info(String old_asin, String new_asin, String new_prod_link, String new_name, double new_prod_rating,
                              String new_prod_brand, int new_overall_rating, int new_overall_reviews, double new_prod_price) {
    try {
        PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE products SET asin = ?, prod_link = ?, name = ?, prod_overall_rating = ?, prod_brand = ?, no_rating = ?, no_reviews = ?, prod_price = ? " +
                        "WHERE asin = ?"
        );
        pstmt.setString(1, new_asin);
        pstmt.setString(2, new_prod_link);
        pstmt.setString(3, new_name);
        pstmt.setDouble(4, new_prod_rating);
        pstmt.setString(5, new_prod_brand);
        pstmt.setInt(6, new_overall_rating);
        pstmt.setInt(7, new_overall_reviews);
        pstmt.setDouble(8, new_prod_price);
        pstmt.setString(9,old_asin);
        pstmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        Static_utils.log("Error updating product information: " + e.getMessage(),"update_prod_info");
        return false;
    }
}




    // Delete
    public boolean delete_prod_info(String asin) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM products WHERE asin = ?");
            pstmt.setString(1, asin);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error deleting record : " + e.getMessage(),"delete_prod_info");
            return false;
        }
    }

    

    // CRUD for reviews
    public boolean add_review_info(String asin, int review_id, String review_title, String review_text, int review_star, String user_profile_link) {
        try {
            create_table_reviews();
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO reviews (asin, review_id, review_title, review_text, review_star, user_profile_link) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );
            pstmt.setString(1, asin);
            pstmt.setInt(2, review_id);
            pstmt.setString(3, review_title);
            pstmt.setString(4, review_text);
            pstmt.setInt(5, review_star);
            pstmt.setString(6, user_profile_link);
            pstmt.executeUpdate();
           return true;
        } catch (SQLException e) {
           Static_utils.log("Error adding review information: " + e.getMessage(),"add_review_info");
           return false;
        }
    }
    
     
    // Read
    public boolean get_review_info() {
        List<Map<String, Object>> reviewsList = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reviews");
            while (rs.next()) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("Review ID", rs.getInt("id"));
                reviewMap.put("ASIN", rs.getString("asin"));
                reviewMap.put("Review ID", rs.getInt("review_id"));
                reviewMap.put("Review Title", rs.getString("review_title"));
                reviewMap.put("Review Text", rs.getString("review_text"));
                reviewMap.put("Review Star", rs.getInt("review_star"));
                reviewMap.put("User Profile Link", rs.getString("user_profile_link"));

                reviewsList.add(reviewMap);
            }
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error reading reviews table: " + e.getMessage(), "get_review_info");
            return false;
        }
    }
    public List<Map<String, Object>> get_review_info(String column_name, String column_value) {
        try {
            // Validating the column_name
            if (!isValidColumnName(column_name)) {
                Static_utils.log("Invalid column name. Allowed values are 'asin', 'review_id', or 'user_profile_link'.","get_review_info");
                return null;
            }

            // Building the SQL query
            String query = "SELECT * FROM reviews WHERE " + column_name + " = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, column_value);

            ResultSet rs = pstmt.executeQuery();
            List<Map<String, Object>> reviewsList = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("Review ID", rs.getInt("id"));
                reviewMap.put("ASIN", rs.getString("asin"));
                reviewMap.put("Review ID", rs.getInt("review_id"));
                reviewMap.put("Review Title", rs.getString("review_title"));
                reviewMap.put("Review Text", rs.getString("review_text"));
                reviewMap.put("Review Star", rs.getInt("review_star"));
                reviewMap.put("User Profile Link", rs.getString("user_profile_link"));

                reviewsList.add(reviewMap);
            }
        return reviewsList;
        } catch (SQLException e) {
           Static_utils.log("Error reading reviews table: " + e.getMessage(),"get_review_info");
           return null;
        }
    }

    // Helper method to validate column_name
    private boolean isValidColumnName(String column_name) {
        return column_name.equals("asin") || column_name.equals("review_id") || column_name.equals("user_profile_link");
    }



    // Update
    public boolean update_review_info(int old_review_id ,String asin, int new_review_id, String review_title, String review_text, int review_star, String user_profile_link) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE reviews SET review_title = ?, review_text = ?, review_star = ?, user_profile_link = ? " +
                            "WHERE asin= ? AND review_id = ?"
            );
            pstmt.setString(1, review_title);
            pstmt.setString(2, review_text);
            pstmt.setInt(3, review_star);
            pstmt.setString(4, user_profile_link);
            pstmt.setString(5, asin);
            pstmt.setInt(6, old_review_id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error updating review information: " + e.getMessage(),"update_review_info");
            return false;
        }
    }


     // Delete
     public boolean delete_review_info(String asin, int review_id) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM reviews WHERE asin = ? AND review_id = ?");
            pstmt.setString(1, asin);
            pstmt.setInt(2, review_id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error deleting review: " + e.getMessage(),"delete_review_info");
            return false;
        }
    }

        //CRUD for product_category

        //create

        public boolean add_product_category(String asin, String category) {
            try {
                create_table_product_category();
                PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT INTO product_category (asin, category) VALUES (?, ?)"
                );
                pstmt.setString(1, asin);
                pstmt.setString(2, category);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                Static_utils.log("Error adding product category: " + e.getMessage(), "add_product_category");
                return false;
            }
        }

        //Read

        public List<Map<String, Object>> get_all_product_categories() {
            List<Map<String, Object>> categoriesList = new ArrayList<>();
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM product_category");
                while (rs.next()) {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("ASIN", rs.getString("asin"));
                    categoryMap.put("Category", rs.getString("category"));
                    categoriesList.add(categoryMap);
                }
            } catch (SQLException e) {
                Static_utils.log("Error reading product categories: " + e.getMessage(), "get_all_product_categories");
            }
            return categoriesList;
        }

        public List<String> get_categories_from_asin(String asin) {
            List<String> categoriesList = new ArrayList<>();
            try {
                String query = "SELECT category FROM product_category WHERE asin = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, asin);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String category = rs.getString("category");
                    categoriesList.add(category);
                }
                return categoriesList;
            } catch (SQLException e) {
                Static_utils.log("Error getting categories for ASIN " + asin + ": " + e.getMessage(), "get_categories_from_asin");
                return null;
            }
            
        }

        public List<String> get_asin_from_categories(String category) {
            List<String> asinList = new ArrayList<>();
            try {
                String query = "SELECT asin FROM product_category WHERE category = ?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setString(1, category);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String asin = rs.getString("asin");
                    asinList.add(asin);
                }
                return asinList;
            } catch (SQLException e) {
                Static_utils.log("Error getting ASINs for category " + category + ": " + e.getMessage(), "get_asin_from_categories");
                return null;
            }
            
        }

        public List<Map<String, Integer>> get_category_count() {
            List<Map<String, Integer>> categoryCountList = new ArrayList<>();
            try {
                String query = "SELECT category, COUNT(*) as count FROM product_category GROUP BY category";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    String category = rs.getString("category");
                    int count = rs.getInt("count");
                    Map<String, Integer> categoryCount = new HashMap<>();
                    categoryCount.put(category, count);
                    categoryCountList.add(categoryCount);
                }
                return categoryCountList;
            } catch (SQLException e) {
                Static_utils.log("Error getting category count: " + e.getMessage(), "get_category_count");
                return null;
            }
            
        }
        
        


    // Close the Connection
    public boolean closeConnection() {
        try {
            if (connection != null) {
                connection.close();    
            }
            return true;
        } catch (SQLException e) {
            Static_utils.log("Error closing connection: " + e.getMessage(),"closeConnection");
            return false;
        }
    }

    

    
    public static void main(String[] args) {
        // Create an instance of DatabaseHandler
        Database_handler dbHandler = new Database_handler();
    
        // Test adding product information
        dbHandler.add_product_info("B12345", "https://example.com/product1", "Product 1", 4.5, "Brand A", 100, 50, 99.99);
        dbHandler.add_product_info("B67890", "https://example.com/product2", "Product 2", 3.8, "Brand B", 80, 30, 49.99);
    
        // Test getting product information
        dbHandler.get_prod_info(); // Print all products
        dbHandler.get_prod_info("B12345"); // Print product with ASIN "B12345"
    
        // Test updating product information
        dbHandler.update_prod_info("B12345", "B12345", "https://example.com/product1-new", "Product 1 Updated", 4.7, "Brand A", 110, 60, 109.99);
    
        // Test deleting product information
        dbHandler.delete_prod_info("B67890");
    
        // Test adding review information
        dbHandler.add_review_info("B12345", 1, "Review 1", "This is review 1", 5, "https://example.com/user1");
        dbHandler.add_review_info("B12345", 2, "Review 2", "This is review 2", 4, "https://example.com/user2");
    
        // Test getting review information
        dbHandler.get_review_info(); // Print all reviews
    
        // Test updating review information
        dbHandler.update_review_info(1, "B12345", 1, "Review 1 Updated", "This is updated review 1", 4, "https://example.com/user1-updated");
    
        // Test deleting review information
        dbHandler.delete_review_info("B12345", 2);
    
        // Test adding product categories
        dbHandler.add_product_category("B12345", "Electronics");
        dbHandler.add_product_category("B12345", "Gadgets");
        dbHandler.add_product_category("B67890", "Clothing");
        dbHandler.add_product_category("B67890", "Accessories");
    
        // Test getting all product categories
        List<Map<String, Object>> categoriesList = dbHandler.get_all_product_categories();
        for (Map<String, Object> categoryMap : categoriesList) {
            for (Map.Entry<String, Object> entry : categoryMap.entrySet()) {
                System.out.println("ASIN: " + entry.getKey() + ", Category: " + entry.getValue());
            }
        }
    
        // Test getting categories for a specific ASIN
        List<String> categoriesForASIN = dbHandler.get_categories_from_asin("B12345");
        System.out.println("Categories for ASIN B12345: " + categoriesForASIN);
    
        // Test getting ASINs for a specific category
        List<String> asinsForCategory = dbHandler.get_asin_from_categories("Electronics");
        System.out.println("ASINs for category Electronics: " + asinsForCategory);
    
        // Test getting category count
        List<Map<String, Integer>> categoryCountList = dbHandler.get_category_count();
        for (Map<String, Integer> categoryCount : categoryCountList) {
            for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
                System.out.println("Category: " + entry.getKey() + ", Count: " + entry.getValue());
            }
        }
    
        // Close the connection
        dbHandler.closeConnection();
    }
    
    }
    
