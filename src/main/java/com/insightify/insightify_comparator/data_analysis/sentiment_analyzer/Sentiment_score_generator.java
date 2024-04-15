package com.insightify.insightify_comparator.data_analysis.sentiment_analyzer;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.insightify.insightify_comparator.data_management.Database_handler;

public class Sentiment_score_generator {
    private Sentiment_analyzer analyzer;
    private Sentiment_classification result;

    public Sentiment_score_generator(){
        this.analyzer = new Sentiment_analyzer();
        this.analyzer.initialize();
    }
    public float get_sentiment_score(String text){

        
        this.result = this.analyzer.getSentimentResult(text);
        // System.out.println("Positive: " + sentimentResult.getPositive()+"%");
        // System.out.println("Neutral: " + sentimentResult.getNeutral()+"%");
        // System.out.println("Negative: " + sentimentResult.getNegative()+"%");
        // System.out.println("Final Score: "+sentimentResult.getFinalScore());
        return this.result.getFinalScore();
    }

    public static String formatTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy: HH:mm:ss");
        return sdf.format(new Date(millis));
    }
    public static void process_sentiments_of_unanalyzed_reviews(){
        
        Database_handler handler = new Database_handler();
        Sentiment_score_generator generator = new Sentiment_score_generator();
        List<Map<String, String>> unanalzyed_reviews = handler.get_unanalyzed_review_texts();
        int counter = 0;
        System.out.println("Total reviews processing: "+unanalzyed_reviews.size());
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        double loopsPerSecond = 0;
        long remainingTime = 0;
        long estimatedCompletionTime = 0;
        double totalReviews = 98468;
        
        for(Map<String, String> review: unanalzyed_reviews){
            System.gc();
            String review_id = review.get("review_id");
            String review_text = review.get("review_text");
            float review_sentiment = generator.get_sentiment_score(review_text);
            handler.add_review_sentiment(review_id, review_sentiment);
    

            if (counter % 100 == 0 && counter > 0) {
                elapsedTime = System.currentTimeMillis() - startTime;
                loopsPerSecond = counter / (elapsedTime / 1000.0);
                remainingTime = (long) ((unanalzyed_reviews.size() - counter) / loopsPerSecond) * 1000;
                estimatedCompletionTime = System.currentTimeMillis() + remainingTime;
                System.out.println("Processed " + ((totalReviews-unanalzyed_reviews.size())+counter) + " reviews ("+ ((float)(totalReviews-unanalzyed_reviews.size()+counter)/totalReviews)*100 +"%). Estimated Completion time: "+formatTime(estimatedCompletionTime));
            }
            counter++;
        }
    }

    

    public static void main(String[] args) {
        // Sentiment_score_generator generator = new Sentiment_score_generator();
        // System.out.println(generator.get_sentiment_score("I love you"));
        // System.out.println(generator.get_sentiment_score("I do not hate you"));
        // System.out.println(generator.get_sentiment_score("I do not love you"));
        // System.out.println(generator.get_sentiment_score("It is very extremely salty for a detox tea. If you are a bp prone person like me, please do not buy this product. It taste like a salty water. I would not recommend to anyone who has bp or tendency to bp. Stay out of it."));
        // System.out.println(generator.get_sentiment_score("Good picture quality but the sound is muddy and lacking clarity."));
        // System.out.println(generator.get_sentiment_score("este es un muy buen lugar para vivir"));
        try{
            System.out.println("Trial 1");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 2");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 3");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 4");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 5");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 6");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 7");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 8");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 9");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 10");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 11");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            System.out.println("Trial 12");
            process_sentiments_of_unanalyzed_reviews();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}