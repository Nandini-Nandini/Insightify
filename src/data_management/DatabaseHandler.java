package data_management;
import java.sql.*;

public class DatabaseHandler {
    private Connection connection;

    // Default Constructor
    public DatabaseHandler() {
        // Default database username and password
        String username = "root"; // Change this to your MySQL username
        String password = "admin123"; // Change this to your MySQL password
        String hostname = "localhost"; // Change this if your MySQL server is hosted elsewhere

        establishConnection(hostname, username, password);
    }

    // Constructor with Parameters
    public DatabaseHandler(String hostname, String username, String password) {
        establishConnection(hostname, username, password);
    }

    private void establishConnection(String hostname, String username, String password) {
        try {
            // Establishing connection with the database
            String url = "jdbc:mysql://" + hostname + ":3306/?useSSL=false&allowPublicKeyRetrieval=true";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            System.out.println("Connected to the database.");   
            // Check if the database exists, if not, create it
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + "Insightify");

            // Connect to the specified database
            url = "jdbc:mysql://" + hostname + ":3306/" + "Insightify" + "?useSSL=false&allowPublicKeyRetrieval=true";
            connection = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            System.out.println("Connection to database failed. Error: " + e.getMessage());
        } catch (Exception e){
            System.out.println("JDBC Class not found error :"+e.getMessage());
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
            "prod_category VARCHAR(255)," +
            "no_rating INT," +
            "no_reviews INT," +
            "prod_price DOUBLE)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error creating Table1: " + e.getMessage());
        }
    }

    // CRUD Functions for products
    // Create
    public void add_product_info(String asin, String prod_link, String name, double prod_overall_rating, String prod_category, int no_rating, int no_reviews,double prod_price ) {
        try {
            create_table_products();
            PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO products (asin, prod_link, name, prod_overall_rating, prod_category, no_rating, no_reviews, prod_price) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            pstmt.setString(1, asin);
            pstmt.setString(2, prod_link);
            pstmt.setString(3, name);
            pstmt.setDouble(4, prod_overall_rating);
            pstmt.setString(5, prod_category);
            pstmt.setInt(6, no_rating);
            pstmt.setInt(7, no_reviews);
            pstmt.setDouble(8, prod_price);
            pstmt.executeUpdate();
            System.out.println("Product information added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding product information: " + e.getMessage());
        }
    }

    // Read
    public void get_prod_info() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while (rs.next()) {
                System.out.println("Product ID: " + rs.getInt("prod_id") +
                        ", ASIN: " + rs.getString("asin") +
                        ", Product Link: " + rs.getString("prod_link") +
                        ", Name: " + rs.getString("name") +
                        ", Overall Rating: " + rs.getDouble("prod_overall_rating") +
                        ", Category: " + rs.getString("prod_category") +
                        ", Number of Ratings: " + rs.getInt("no_rating") +
                        ", Number of Reviews: " + rs.getInt("no_reviews") +
                        ", Price: " + rs.getDouble("prod_price"));
            }
        } catch (SQLException e) {
            System.out.println("Error reading products table: " + e.getMessage());
        }
    }
    
    public void get_prod_info(String asin) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "SELECT * FROM products WHERE asin = ?"
            );
            pstmt.setString(1, asin);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Product ID: " + rs.getInt("prod_id") +
                        ", ASIN: " + rs.getString("asin") +
                        ", Product Link: " + rs.getString("prod_link") +
                        ", Name: " + rs.getString("name") +
                        ", Overall Rating: " + rs.getDouble("prod_overall_rating") +
                        ", Category: " + rs.getString("prod_category") +
                        ", Number of Ratings: " + rs.getInt("no_rating") +
                        ", Number of Reviews: " + rs.getInt("no_reviews") +
                        ", Price: " + rs.getDouble("prod_price"));
            }
        } catch (SQLException e) {
            System.out.println("Error reading products table: " + e.getMessage());
        }
    }
    
    // Update
    public void update_prod_info(String old_asin, String new_asin, String new_prod_link, String new_name, double new_prod_rating,
                              String new_prod_category, int new_overall_rating, int new_overall_reviews, double new_prod_price) {
    try {
        PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE products SET asin = ?, prod_link = ?, name = ?, prod_overall_rating = ?, prod_category = ?, no_rating = ?, no_reviews = ?, prod_price = ? " +
                        "WHERE asin = ?"
        );
        pstmt.setString(1, new_asin);
        pstmt.setString(2, new_prod_link);
        pstmt.setString(3, new_name);
        pstmt.setDouble(4, new_prod_rating);
        pstmt.setString(5, new_prod_category);
        pstmt.setInt(6, new_overall_rating);
        pstmt.setInt(7, new_overall_reviews);
        pstmt.setDouble(8, new_prod_price);
        pstmt.setString(9,old_asin);
        pstmt.executeUpdate();
        System.out.println("Product information updated successfully.");
    } catch (SQLException e) {
        System.out.println("Error updating product information: " + e.getMessage());
    }
}


    // Delete
    public void delete_prod_info(String asin) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM products WHERE asin = ?");
            pstmt.setString(1, asin);
            pstmt.executeUpdate();
            System.out.println("Record deleted  successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting record : " + e.getMessage());
        }
    }

    // Close the Connection
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection to database closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
    
        public static void main(String[] args) {
            // Creating an instance of DatabaseHandler
            DatabaseHandler dbHandler = new DatabaseHandler();
    
            // Test create_table_products()
            // dbHandler.create_table_products();
    
            // Test add_product_info()
            dbHandler.add_product_info("B01M8L5Z3Y", "https://www.example.com/product1", "Product 1", 4.5, "Electronics", 100, 50, 199.99);
            dbHandler.add_product_info("B07C5B4W8P", "https://www.example.com/product2", "Product 2", 4.2, "Home & Kitchen", 80, 30, 149.99);
            dbHandler.add_product_info("B0869G3JQQ", "https://www.example.com/product3", "Product 3", 4.8, "Tools & Home Improvement", 120, 70, 299.99);
    
            // Test get_prod_info()
            System.out.println("\n--- All Products ---");
            dbHandler.get_prod_info();
    
            // Test get_prod_info(asin)
            System.out.println("\n--- Product with ASIN B01M8L5Z3Y ---");
            dbHandler.get_prod_info("B01M8L5Z3Y");
    
            // Test update_prod_info()
            dbHandler.update_prod_info("B01M8L5Z3Y", "B01M8L5Z3Y", "https://www.example.com/updated_product1", "Updated Product 1", 4.6, "Electronics", 120, 60, 219.99);
    
            // Test get_prod_info() after update
            System.out.println("\n--- All Products after Update ---");
            dbHandler.get_prod_info();
    
            // Test delete_prod_info()
            dbHandler.delete_prod_info("B07C5B4W8P");
    
            // Test get_prod_info() after deletion
            System.out.println("\n--- All Products after Deletion ---");
            dbHandler.get_prod_info();
    
            // Closing the connection
            dbHandler.closeConnection();
        }
    }
    
