package data_analysis.SentimentAnalyzer;


import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import data_analysis.model.SentimentClassification;
import data_analysis.model.SentimentClassification.SentimentCategory;
import data_analysis.model.SentimentResult;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

    static Properties props;
    static StanfordCoreNLP pipeline;

    public void initialize() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and sentiment
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public SentimentResult getSentimentResult(String text) {

        SentimentResult sentimentResult = new SentimentResult();
        SentimentClassification sentimentClass = new SentimentClassification();

        if (text != null && text.length() > 0) {

            // run all Annotators on the text
            Annotation annotation = pipeline.process(text);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                // this is the parse tree of the current sentence
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
                String sentimentType = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

                // Find the category with the highest probability
                double[] probabilities = new double[] {
                    sm.get(3) + sm.get(4),  // Positive
                    sm.get(1) + sm.get(0),  // Negative
                    sm.get(2)  // Neutral
                };

                // Get the index of the category with the highest probability
                int maxIndex = 0;
                for (int i = 1; i < probabilities.length; i++) {
                    if (probabilities[i] > probabilities[maxIndex]) {
                        maxIndex = i;
                    }
                }

                // Set the sentiment category
                switch (maxIndex) {
                    case 0:
                        sentimentClass.setPositive(SentimentCategory.POSITIVE);
                        break;
                    case 1:
                        sentimentClass.setNegative(SentimentCategory.NEGATIVE);
                        break;
                    case 2:
                        sentimentClass.setNeutral(SentimentCategory.NEUTRAL);
                        break;
                }

                sentimentResult.setSentimentType(sentimentType);
                sentimentResult.setSentimentClass(sentimentClass);
            }

        }

        return sentimentResult;
    }
}
