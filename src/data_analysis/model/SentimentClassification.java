package data_analysis.model;

public class SentimentClassification {
    public enum SentimentCategory {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }

    private SentimentCategory positive;
    private SentimentCategory neutral;
    private SentimentCategory negative;

    public SentimentCategory getPositive() {
        return positive;
    }

    public void setPositive(SentimentCategory positive) {
        this.positive = positive;
    }

    public SentimentCategory getNeutral() {
        return neutral;
    }

    public void setNeutral(SentimentCategory neutral) {
        this.neutral = neutral;
    }

    public SentimentCategory getNegative() {
        return negative;
    }

    public void setNegative(SentimentCategory negative) {
        this.negative = negative;
    }
}


