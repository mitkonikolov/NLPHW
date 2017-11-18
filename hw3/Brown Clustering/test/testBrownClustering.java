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

        SortedSet<Tuple> sortedUniCount = lm.getSortedUnigramCount();
        Iterator<Tuple> iter2 = sortedUniCount.iterator();
        Tuple temp;

        System.out.println("\n\n\n\n");

        while(iter2.hasNext()) {
            temp = iter2.next();
            System.out.println(temp.getWord() + " : " + temp.getNumOccurences());
        }
    }

    @Test
    public void testPrinting() {
        BrownCluster bc = new BrownCluster();

        bc.parse();
        bc.getSortedUnigramCount();
        bc.printRankedVocabList();
    }
}
