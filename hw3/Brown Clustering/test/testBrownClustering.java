import org.junit.Test;

import java.util.*;

/**
 * Created by Mitko on 10/18/17.
 */
public class testBrownClustering {
    BrownCluster bc;

    private void initAndParse() {
        bc = new BrownCluster();
        bc.parse();
    }

    @Test
    public void testParsing() {
        this.initAndParse();

        HashMap<String, Integer> uniCount = bc.getUnigramCount();

        Iterator<String> iter = uniCount.keySet().iterator();
        String w;

        SortedSet<Tuple> sortedUniCount = bc.getSortedUnigramCount();
        Iterator<Tuple> iter2 = sortedUniCount.iterator();
        Tuple temp;

        System.out.println("\n\n\n\n");

        while(iter2.hasNext()) {
            temp = iter2.next();
            System.out.println(temp.getWord() + " : " + temp.getNumOccurences());
        }
    }

    @Test
    public void testPrintingVocabulary() {
        initAndParse();
        bc.printRankedVocabList();
    }

    @Test
    public void testPrintingBigrams() {
        initAndParse();
        bc.printBigrams();

    }

    @Test
    public void testGenInitCluster() {
        initAndParse();
        bc.generateInitialClusters();
        HashMap<Integer, Cluster> clusters = bc.getAllClusters();
        printClusters(clusters);
    }

    @Test
    public void testAddWordsToClusters() {
        initAndParse();
        bc.generateInitialClusters();
        bc.genAllClusters();
        HashMap<Integer, Cluster> clusters = bc.getAllClusters();
        printClusters(clusters);


    }

    private void printClusters(HashMap<Integer, Cluster> clusters) {
        Cluster c;
        for(int i=1; i<=clusters.size(); i++) {
            c = clusters.get(i);
            Iterator <String> iter = c.getWords().iterator();
            System.out.println("Cluster " + i + ": id is " + c.getId() + " words are " + iter.next() + " p_c is " + c.getP_c());

            while(iter.hasNext()) {
                System.out.println(" " + iter.next());
            }
        }
    }
}
