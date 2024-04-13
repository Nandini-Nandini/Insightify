package data_analysis.SentimentAnalyzer;

import java.io.IOException;

import data_analysis.model.SentimentResult;

public class MainApp {

    public static void main(String[] args) throws IOException {

        String text = "i do not like  this facewash .";

        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.initialize();
        SentimentResult sentimentResult = sentimentAnalyzer.getSentimentResult(text);

        System.out.println("Sentiment Type: " + sentimentResult.getSentimentType());
        System.out.println("Sentiment Score: " + sentimentResult.getSentimentScore());
        System.out.println("Positive: " + sentimentResult.getSentimentClass().getPositive()+"%");
        System.out.println("Neutral: " + sentimentResult.getSentimentClass().getNeutral()+"%");
        System.out.println("Negative: " + sentimentResult.getSentimentClass().getNegative()+"%");
    }

}
