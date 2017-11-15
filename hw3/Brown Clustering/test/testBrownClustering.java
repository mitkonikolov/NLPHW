import org.junit.Test;

import java.util.*;

/**
 * Created by Mitko on 10/18/17.
 */
public class testBrownClustering {

    @Test
    public void testParsing() {
        BrownCluster lm = new BrownCluster();

        lm.parse();
        //lm.calcProbs();

        HashMap<String, Integer> uniCount = lm.getUnigramCount();

        Iterator<String> iter = uniCount.keySet().iterator();
        String w;


/*        HashMap<String, HashMap<String, Double>> bigramCount = lm.getTrigramProbs();

        iter = bigramCount.keySet().iterator();
        Iterator<String> innerIter;*/
/*
        HashMap<String, Double> wordsAfter;
        while(iter.hasNext()) {
            w = iter.next();
            System.out.println(w + " : " + uniCount.get(w));

            //System.out.println(bigramCount.size());


*//*            wordsAfter = bigramCount.get(w);
            //System.out.println(wordsAfter.size());


            innerIter = wordsAfter.keySet().iterator();

            System.out.println("After:");
            while(innerIter.hasNext()) {
                w = innerIter.next();
                System.out.println(w + " " + wordsAfter.get(w));
            }
            System.out.println("\n\n");*//*

        }*/



        SortedSet<Tuple> sortedUniCount = lm.getSortedUnigramCount();
        Iterator<Tuple> iter2 = sortedUniCount.iterator();
        Tuple temp;

        System.out.println("\n\n\n\n");

        while(iter2.hasNext()) {
            temp = iter2.next();
            System.out.println(temp.getWord() + " : " + temp.getNumOccurences());
        }






/*        HashMap<String, Double> uniProb = lm.getUnigProb();

        iter = uniProb.keySet().iterator();

        while(iter.hasNext()) {
            w = iter.next();
            System.out.println(uniProb.get(w));
        }*/
    }
}
