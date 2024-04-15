package com.insightify.insightify_comparator.data_analysis.sentiment_analyzer;


import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class Sentiment_analyzer {

    static Properties props;
    static StanfordCoreNLP pipeline;

    protected Sentiment_analyzer(){

    }

    protected void initialize() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and sentiment
        props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit,pos,lemma, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    protected Sentiment_classification getSentimentResult(String text) {

        
        Sentiment_classification sentimentClass = new Sentiment_classification();

        if (text != null && text.length() > 0) {

            // run all Annotators on the text
            Annotation annotation = pipeline.process(text);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                // this is the parse tree of the current sentence
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
            
                // Find the category with the highest probability
                double[] probabilities = new double[] {
                    sm.get(3) + sm.get(4),  // Positive
                    sm.get(1) + sm.get(0),  // Negative
                    sm.get(2)  // Neutral
                };
                
				sentimentClass.setPositive((double)(probabilities[0] * 100d));
				sentimentClass.setNeutral((double)(probabilities[2] * 100d));
				sentimentClass.setNegative((double)(probabilities[1] * 100d));
            }

        }

        return sentimentClass;
    }
}
