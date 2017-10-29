import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Mitko on 10/25/17.
 */
public class testAnalyzer {

    @Test
    public void testParsing() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        analyzer.parse();
        HashMap<String, Integer> positiveBag = analyzer.getBagOfWordsPositive();

        Iterator<String> iter = positiveBag.keySet().iterator();

        while(iter.hasNext()) {
            String w = iter.next();

            System.out.println(w + " : " + positiveBag.get(w));
        }
    }


    @Test
    public void testProbPos() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        analyzer.parse();
        analyzer.calcProbPos("", 1);

        System.out.println(analyzer.getProbPositive());
    }

    @Test
    public void testProbNeg() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        analyzer.parse();
        analyzer.calcProbNeg("", 1);

        System.out.println(analyzer.getProbNegative());
    }


    @Test
    public void testAnalyze() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();
        analyzer.analyze(1);
    }

    @Test
    public void testAnalyzeCalcMetrics() {
        SentimentAnalyzer analyzer = new SentimentAnalyzer();

        analyzer.analyzeCalcMetrics();
        System.out.println(analyzer.getPrecision());
        System.out.println(analyzer.getRecall());
        System.out.println(analyzer.getF1());
    }
}
