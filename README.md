# Insightify 🚀

Welcome to **Insightify** - your ultimate solution for making informed decisions about Amazon purchases. This project analyzes millions of customer reviews to provide data-driven insights, simplifying the decision-making process for users.

## Collaborators 🤝
- [Mayank Raj](https://github.com/mraj602)
- [Jannhavi Vaidya](https://github.com/JannhaviV)
- [Nandini](https://github.com/Nandini-Nandini)

## About Insightify 📊
### Challenge of Online Shopping 🛒
- **Endless product options** lead to decision fatigue.
- **Difficulty in gauging true customer sentiment** from text reviews.
- **Unreliable star ratings** can be misleading.

### Services 🛠️
- **Product Comparison**: Compare products based on comprehensive data analysis.
- **Composite Scoring**: Aggregate multiple data points into a single, easy-to-understand score.

### Behind The Scenes 🔍
#### Data Extraction 📥
- **Automated Data Extraction** with Jsoup and Selenium.
- **ASIN-Driven Product Details**: Extract data using Amazon Standard Identification Numbers.
- **Review Page Navigation and Parsing**: Navigate through review pages and parse content.
- **Structured Review Data Collection**: Collect structured review data for analysis.
- **Automated Data Preprocessing in Java**: Preprocess data for sentiment analysis.

#### Data Management 💾
- **Web Scraping with Java**: Efficiently scrape web data.
- **Centralized MySQL Database**: Store data in a structured and reliable manner.
- **Structured Schema Design**: Maintain a well-organized database schema.
- **CRUD Operations**: Perform Create, Read, Update, and Delete operations seamlessly.

#### Sentiment Analysis 💡
- **Leveraging Stanford CoreNLP**: Use advanced NLP tools for sentiment analysis.
- **Data Preprocessing**: Clean and prepare data for accurate sentiment classification.
- **Sentiment Classification**: Classify sentiments with a range of -1 to +1.
- **Individual Review Analysis**: Analyze each review to understand its sentiment.
- **Composite Product Score**: Aggregate individual sentiments into a composite score.

### Sentiment Scoring 📈
- **Review Sentiment Scoring**: Score each review based on sentiment.
- **Product Sentiment Score**: Calculate a normalized sentiment score for each product.
- **Normalized Probability Sentiment Score**: Standardize scores for comparability.
- **Normalized Rating Size Score**: Adjust scores based on the number of reviews.
- **Final Score**: Combine all metrics to produce a final, comprehensive sentiment score.

## Conclusion 🎉
Insightify is revolutionizing online shopping by providing:
- **Data-Driven Decisions**: Make informed choices based on comprehensive data analysis.
- **Simplified Shopping Experience**: Reduce decision fatigue with clear, data-driven insights.
- **Enhanced Consumer Confidence**: Trust the products you buy with reliable sentiment scores.

### How It Works ⚙️
1. **Input ASIN**: Users enter the ASIN number of the product(s) on our website.
2. **Automated Processing**: If the ASIN is not in our database, the system automatically scrapes the necessary data.
3. **Sentiment Analysis**: The data is preprocessed and analyzed to calculate the sentiment score.
4. **Final Score Delivery**: The final sentiment score is emailed to the user within minutes.

## Getting Started 🏁

### Prerequisites 📋
- Java 17+
- MySQL
- Spring Boot
- Jsoup
- Selenium
- Stanford CoreNLP

### Installation 🛠️
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/mraj602/insightify.git

2. **Set Up MySQL Database**

    Set up MySQL as per src/main/java/com/insightify/insightify_comparator/data_management/Database_handler.java

3. **Run the Spring-Boot Application**
   
   Run the Maven Spring-Boot Application as per your IDE.

### Note ⚠️
This project is based on data extraction using Selenium. The structure of Amazon pages may change over time, rendering the scraping process ineffective. You may need to update the scraping logic to adapt to any changes in the structure of Amazon pages. Please regularly check and adjust the scraping process as necessary to ensure the continued functionality of the project.

