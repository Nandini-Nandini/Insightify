package data_analysis.sentiment_analyzer;

public class sentiment_classification {
   
    protected sentiment_classification() {
    }

    private double  positive;
    private double neutral;
    private double negative;
    private float finalScore;

    protected double getPositive() {
        return positive;
    }

    protected void setPositive(double positive) {
        this.positive = positive;
    }

    protected double getNeutral() {
        return neutral;
    }

    protected void setNeutral(double neutral) {
        this.neutral = neutral;
    }

    protected double getNegative() {
        return negative;
    }

    protected void setNegative(double negative) {
        this.negative = negative;
    }

    protected float getFinalScore(){
        this.finalScore = Float.parseFloat(((-1*negative+positive)/100)+"");
        this.finalScore = this.finalScore - (this.finalScore%0.0001f);
        return finalScore;
    }
}


