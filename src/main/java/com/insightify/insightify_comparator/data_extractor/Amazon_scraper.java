package com.insightify.insightify_comparator.data_extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.insightify.insightify_comparator.util.Static_utils;


public class Amazon_scraper {

    private List<String> asins;
    private ChromeDriver driver;
    private List<Object> final_product_infos = new ArrayList<>();
    private List<Object> final_reviews = new ArrayList<>();

    public Amazon_scraper(List<String> asins) {
        this.asins = asins;
        ChromeOptions chromeOptions = new ChromeOptions();
        // chromeOptions.addArguments("--headless");
        // Disable images
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-infobars");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-images");
        chromeOptions.addArguments("--blink-settings=imagesEnabled=false");
        this.driver = new ChromeDriver(chromeOptions);
        
        try{
            scrapeData();
        }catch(Exception e){
            Static_utils.log(e.toString(), "Amazon_scraper constructor");
            e.printStackTrace();
        }
    }
    
    private void scrapeData() throws IOException {
        List<Object> product_infos = new ArrayList<>();
        List<Object> reviews = new ArrayList<>();
        for (String asin: asins){
            String dp_url = "https://www.amazon.in/dp/"+asin;
            // this.driver.get(dp_url);
            Document doc;
            System.out.println("Opening URL");
            for(int i =0; i<20;i++){
                try{
                    this.driver.get(dp_url);
                    doc = Jsoup.parse(this.driver.getPageSource());
                    doc.select(".a-size-large.product-title-word-break").get(0);
                    System.out.println("Breaking");
                    break;
                }catch(Exception e){
                    System.out.println("Reloading attempt: "+(i+1));
                    
                }
            }
            

            // try{
            //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".a-size-large.product-title-word-break")));
            // }catch(Exception e){
            //     System.out.println("Getting URL Again");
            //     this.driver.get(dp_url);
            // }
            
            Map<String, Object> product_info = get_product_information(this.driver.getPageSource(),dp_url, asin);
            Map<String, Object> reviews_from_all_page = extract_reviews_all_page(asin);

            product_info.put("no_reviews", reviews_from_all_page.remove("product_review_count"));
            product_infos.add(product_info);
            reviews.add(reviews_from_all_page);
        }
        // driver.close();
        this.driver.quit();
        this.driver.quit();   
        this.final_product_infos = product_infos;
        this.final_reviews = reviews;
    }

    public Map<String, Object> get_result() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("product_info", this.final_product_infos);
        resultMap.put("review_info", this.final_reviews);
        return resultMap;
    }

    private Map<String, Object> get_product_information(String response_content, String url, String asin) {
        Map<String, Object> productInfo = new HashMap<>();
        Document doc = Jsoup.parse(response_content);

        // Extracting product titles
        Elements productTitles = doc.select(".a-size-large.product-title-word-break");
        String prodName = productTitles.get(0).text().trim();
        productInfo.put("asin", asin);
        productInfo.put("prod_link", url);
        productInfo.put("name", prodName);

        // Extracting product prices
        Elements productPrices = doc.select(".a-price-whole");
        float prodPrice = Float.parseFloat(productPrices.get(0).text().trim().replace(",", "").split("\\.")[0]);
        productInfo.put("prod_price", prodPrice);

        // Extracting overall ratings
        Elements ratings = doc.select(".reviewCountTextLinkedHistogram.noUnderline");
        float prodOverallRating = Float.parseFloat(ratings.get(0).attr("title").split(" ")[0]);
        productInfo.put("prod_overall_rating", prodOverallRating);

        // Extracting number of ratings
        Element totalRatings = doc.select("a#acrCustomerReviewLink span.a-size-base").first();
        int prodNoRating = Integer.parseInt(totalRatings.text().split(" ")[0].replace(",", ""));
        productInfo.put("no_rating", prodNoRating);

        // Extracting brand
        try {
            Element brandElement = doc.select("tr.po-brand span.a-size-base.po-break-word").first();
            String prodBrand = brandElement.text().trim();
            productInfo.put("brand", prodBrand);
        } catch (Exception e) {
            productInfo.put("brand", "");
        }

        productInfo.put("no_reviews", null);
        return productInfo;
    }

    private List<List<String>> extract_reviews_one_page(String response_content) {
        Document doc = Jsoup.parse(response_content);
        Elements reviewDivs = doc.select("div[data-hook=review]");

        List<String> reviewIds = new ArrayList<>();
        List<String> reviewTitles = new ArrayList<>();
        List<String> reviewTexts = new ArrayList<>();
        List<String> starRatings = new ArrayList<>();
        List<String> profileLinks = new ArrayList<>();

        for (Element reviewDiv : reviewDivs) {
            // Extracting review ID
            String reviewId = reviewDiv.attr("id");

            // Extracting review title
            String reviewTitle = "";
            try {
                reviewTitle = reviewDiv.select("a[data-hook=review-title]").first().text().trim();
            } catch (Exception ignored) {}

            // Extracting review text
            String reviewText = reviewDiv.select("span[data-hook=review-body]").first().text().trim();

            // Extracting star rating
            String starRating = "";
            try {
                starRating = reviewDiv.select("i[data-hook=review-star-rating] span.a-icon-alt").first().text().split(" ")[0];
            } catch (Exception ignored) {}

            // Extracting profile link
            String profileLink = "";
            try {
                profileLink = reviewDiv.select("a.a-profile").first().attr("href").split("account.")[1].split("/")[0];
            } catch (Exception ignored) {}

            reviewIds.add(reviewId);
            starRatings.add(starRating.split(" ")[0]);
            reviewTitles.add(reviewTitle);
            reviewTexts.add(reviewText);
            profileLinks.add(profileLink);
        }

        List<List<String>> result = new ArrayList<>();
        result.add(reviewIds);
        result.add(reviewTitles);
        result.add(reviewTexts);
        result.add(starRatings);
        result.add(profileLinks);

        return result;
    }

    private Map<String, Object> extract_reviews_all_page(String asin) {
        List<String> finalReviewAsins = new ArrayList<>();
        List<String> finalReviewIds = new ArrayList<>();
        List<String> finalReviewTitles = new ArrayList<>();
        List<String> finalReviewTexts = new ArrayList<>();
        List<String> finalStarRatings = new ArrayList<>();
        List<String> finalProfileLinks = new ArrayList<>();

        String baseUrl = "https://www.amazon.in/product-reviews/" + asin + "/ref=cm_cr_arp_d_viewopt_sr?formatType=all_formats";
    

        for (int i =0; i<20;i++){
            try{
                this.driver.get(baseUrl);
                Document doc = Jsoup.parse(this.driver.getPageSource());
                doc.select("div.a-row.a-spacing-base.a-size-base").get(0);
                System.out.println("breaking review");
                break;
            }catch(Exception e){
                System.out.println("Reloading reviews attempt:"+(i+1));
            }
        }
        Document soup = Jsoup.parse(driver.getPageSource());
        Element reviewCountDiv = soup.selectFirst("div.a-row.a-spacing-base.a-size-base");
        String reviewCountText = reviewCountDiv.text().trim().replaceAll(",","").split("ratings ")[1].split(" ")[0];
        int reviewCountInteger = Integer.parseInt(reviewCountText);



        int counter = 0;

        for (String sort_by : new String[]{"recent", "helpful"}) {
            for (String review_type : new String[]{"avp_only_reviews"}) {
                for (String star : new String[]{"all_star", "five_star", "four_star", "three_star", "two_star", "one_star"}) { //, "five_star", "four_star", "three_star", "two_star", "one_star"
                    for (int pgn = 1; pgn <= 10; pgn++) {
                        if (counter % 20 == 0) {
                            System.out.println("Progress: "+counter+" of 120 for product asin:"+asin);
                        }else if(counter==119){
                            System.out.println("Progress: 120 of 120 for product asin:"+asin);
                        }
                        
                        String url = String.format("%s&sortBy=%s&pageNumber=%d&reviewerType=%s&filterByStar=%s", baseUrl, sort_by, pgn, review_type, star);
                        driver.get(url);
                        List<List<String>> reviews = extract_reviews_one_page(driver.getPageSource());
                        finalReviewIds.addAll(reviews.get(0));
                        finalReviewTitles.addAll(reviews.get(1));
                        finalReviewTexts.addAll(reviews.get(2));
                        finalStarRatings.addAll(reviews.get(3));
                        finalProfileLinks.addAll(reviews.get(4));
                        counter++;
                    }
                }
            }
        }

        finalReviewAsins.addAll(IntStream.range(0, finalReviewIds.size()).mapToObj(i -> asin).collect(Collectors.toList()));

        Map<String, Object> result = new HashMap<>();
        result.put("asins", finalReviewAsins);
        result.put("review_ids", finalReviewIds);
        result.put("review_titles", finalReviewTitles);
        result.put("review_texts", finalReviewTexts);
        result.put("review_star", finalStarRatings);
        result.put("user_profile_link", finalProfileLinks);
        result.put("product_review_count", reviewCountInteger);

        return result;
    }

    // public static void main(String[] args) {
    //     List<String> asins = new ArrayList<>();
    //     asins.add("B0BZCR6TNK");
    //     asins.add("B07WHSR1NR");
    //     // asins.add("B0BRQCJ57Y");
    //     Amazon_scraper scraper = new Amazon_scraper(asins);
        
    //     try {
    //         // FileWriter with BufferedWriter to write data to file
    //         FileWriter fw = new FileWriter("output.txt");
    //         BufferedWriter bw = new BufferedWriter(fw);

    //         // Write variable data to the file
    //         bw.write(scraper.get_result().toString());

    //         // Close the BufferedWriter
    //         bw.close();

    //         System.out.println("Variable data saved to output.txt");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

}
