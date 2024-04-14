package data_analysis.sentiment_analyzer;


public class sentiment_score_generator {
    private sentiment_analyzer analyzer;
    private sentiment_classification result;

    public sentiment_score_generator(){
        this.analyzer = new sentiment_analyzer();
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

    public static void main(String[] args) {
        sentiment_score_generator generator = new sentiment_score_generator();
        System.out.println(generator.get_sentiment_score("I love you"));
        System.out.println(generator.get_sentiment_score("I do not hate you"));
        System.out.println(generator.get_sentiment_score("I do not love you"));
        System.out.println(generator.get_sentiment_score("It is very extremely salty for a detox tea. If you are a bp prone person like me, please do not buy this product. It taste like a salty water. I would not recommend to anyone who has bp or tendency to bp. Stay out of it."));
        System.out.println(generator.get_sentiment_score("Good picture quality but the sound is muddy and lacking clarity."));
        System.out.println(generator.get_sentiment_score("este es un muy buen lugar para vivir"));
    }


}
