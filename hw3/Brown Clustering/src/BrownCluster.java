
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mitko on 11/15/17.
 */
public class BrownCluster {
    private HashMap<String, Integer> unigramCount;
    private SortedSet<Tuple> sortedUnigramCount;
    // word1 -> (word2 count) how many times (word1 word2) has been seen
    private HashMap<String, HashMap<String, Integer>> bigramCount;
    private HashMap<String, HashMap<String, Integer>> bigramUnkCount;
    // word1 -> (word2 count) how many times (word2 word1) has been seen
    private HashMap<String, HashMap<String, Integer>> predecessorBigram;

    private HashMap<Integer, Cluster> allClusters;
    private int totalNumWords;
    private int totalBigrams;
    private int k;
    // the probability that C1Id is seen before C2Id
    private HashMap<Integer, HashMap<Integer, Double>> P_C1_C2;

    // Cluster - Cluster - Weight (MI(c1 c2) + MI(c2 c1))
    private HashMap<Integer, HashMap<Integer, Double>> graph;
    // Cluster Id - Cluster Id - Change in delta L if they are merged
    private HashMap<Integer, HashMap<Integer, Double>> tableL;



    public BrownCluster() {
        this.unigramCount = new HashMap<>();
        this.sortedUnigramCount = new TreeSet<>();
        this.bigramCount = new HashMap<>();
        this.bigramUnkCount = new HashMap<>();
        this.predecessorBigram = new HashMap<>();

        this.allClusters = new HashMap<>();
        this.totalNumWords = 0;
        this.totalBigrams = 0;
        this.k = 200;

        this.graph = new HashMap<>();
        this.tableL = new HashMap<>();
    }


    /**
     * Parses the input files and updates all count maps.
     */
    public void parse() {
        try {
            File filesDir = new File("../brown corpus/brown");
            // ignore hidden files
            FilenameFilter fileFilter = (dir, name) -> {
                Character firstChar = name.charAt(0);
                return !firstChar.equals('.') &&
                        !name.equals("CONTENTS") &&
                        !name.equals("README") &&
                        !name.equals("cats.txt");
            };
            File[] allFiles = filesDir.listFiles(fileFilter);

            for(int i = 0; i < allFiles.length; i++) {
                Scanner s = new Scanner(allFiles[i]);
                Scanner s2;

                String sentence;
                String word;
                String previousWord;

                while (s.hasNextLine()) {
                    previousWord = "START";
                    sentence = s.nextLine();

                    if (sentence.length() != 0) {

                        s2 = new Scanner(sentence);

                        while (s2.hasNext()) {
                            String[] words = s2.next().split("/");

                            word = words[0].toLowerCase();

                            // the word contains an apostrophe and needs to be
                            // parsed in a special way
                            if (word.contains("'") && word.length() > 2) {
                                int ind = word.indexOf("'");
                                String firstWord = word.substring(0, ind);
                                String secondWord = word.substring(ind);

                                updateNGram("", firstWord, 0);
                                updateNGram(previousWord, firstWord, 1);

                                // secondWord is "'s"
                                updateNGram("", secondWord, 0);
                                updateNGram(firstWord, secondWord, 1);

                                previousWord = secondWord;
                            }
                            // the words does not contain an apostrophe so the maps
                            // can directly be updated
                            else {
                                // updateUnigram
                                updateNGram("", word, 0);
                                // updateBigram
                                updateNGram(previousWord, word, 1);

                                previousWord = word;
                            }

                            // this is the last word - add END
                            if(!s2.hasNext()) {
                                updateNGram(previousWord, "END", 1);
                            }
                        }
                    }

                }
            }

            this.sortGenVocabBigram();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the count maps' values for previous -> word by 1.
     * @param previous the expression before {@param word}
     * @param word {@code String} after {@param previous}
     * @param mapUpdate indicates which map needs to be updated
     */
    private void updateNGram(String previous, String word, int mapUpdate) {
        Integer value;
        HashMap<String, Integer> wordAfter;

        Iterator<String> iter;
        switch (mapUpdate) {
            case 0:
                value = this.unigramCount.get(word);
                if(value == null) {
                    value = 0;
                }
                unigramCount.put(word, value + 1);
                break;
            case 1:
                wordAfter = this.bigramCount.get(previous);
                if(wordAfter==null) {
                    wordAfter = new HashMap<>();
                }
                updateInnerMap(wordAfter, word);
                this.bigramCount.put(previous, wordAfter);
                break;
            default:
                throw new InputMismatchException("incorrect choice");
        }
    }


    /**
     * Increments by 1 the number in {@param wordAfter} associated with
     * {@param word}.
     *
     * @param wordAfter {@code HashMap} of numbers and strings that needs to
     *                                 be updated
     * @param word {@code String} whose associated number in {@param wordAfter}
     *                           needs to be incremented
     */
    private void updateInnerMap(HashMap<String, Integer> wordAfter, String word) {
        Integer value;

        if(wordAfter==null) {
            wordAfter = new HashMap<>();
        }

        value = wordAfter.get(word);
        if(value == null) {
            value = 0;
        }
        value += 1;
        wordAfter.put(word, value);
    }


    /**
     * Generates {@code this.sortedUnigramCount}, adds the UNK class, and
     * adds the UNK class to the bigram count.
     */
    private void sortGenVocabBigram() {
        Iterator<String> iter = this.unigramCount.keySet().iterator();
        String word;
        int numOccur;
        int numRareOccur=0;
        Tuple t;

        Set<String> unkWords = new HashSet<>();

        while(iter.hasNext()) {
            word = iter.next();
            numOccur = this.unigramCount.get(word);

            this.totalNumWords += numOccur;

            if(numOccur<=10) {
                numRareOccur += numOccur;
                unkWords.add(word);
            }
            else {
                t = new Tuple(word, numOccur);
                this.sortedUnigramCount.add(t);
            }
        }

        t = new Tuple("UNK", numRareOccur);
        this.sortedUnigramCount.add(t);

        setBigramUnk(unkWords);
    }


    public HashMap<String, Integer> getUnigramCount() {
        return this.unigramCount;
    }

    public SortedSet<Tuple> getSortedUnigramCount() {
        return this.sortedUnigramCount;
    }

    public HashMap<String, HashMap<String, Integer>> getBigramCount() {
        return this.bigramCount;
    }

    public void printRankedVocabList() {

        try {
            FileWriter fw = new FileWriter("sorted.txt");
            Iterator<Tuple> iter2 = this.sortedUnigramCount.iterator();
            Tuple temp;

            while(iter2.hasNext()) {
                temp = iter2.next();
                fw.write(temp.getWord() + " : " + temp.getNumOccurences() + "\n");
            }
            fw.close();
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }

    }

    public void printBigrams(boolean mode) {
        try {
            FileWriter fw;
            Iterator<String> iter2;

            if(mode) {
                fw = new FileWriter("bigrams.txt");
                iter2 = this.bigramUnkCount.keySet().iterator();
            }
            else {
                fw = new FileWriter("bigrams_predecessor.txt");
                iter2 = this.predecessorBigram.keySet().iterator();
            }
            Iterator<String> innerIterator;
            HashMap<String, Integer> innerMap = new HashMap<>();
            String previous;
            String wordAfter;

            while(iter2.hasNext()) {
                previous = iter2.next();

                if(mode) {
                    innerMap = this.bigramUnkCount.get(previous);
                }
                else {
                    innerMap = this.predecessorBigram.get(previous);
                }

                innerIterator = innerMap.keySet().iterator();

                fw.write("\"" + previous + "\" : ");

                while(innerIterator.hasNext()) {
                    wordAfter = innerIterator.next();
                    if(!iter2.hasNext()) {
                        fw.write("\"" + wordAfter + "\" " + innerMap.get(wordAfter));
                    }
                    else {
                        fw.write("\"" + wordAfter + "\" " + innerMap.get(wordAfter) + ", ");
                    }
                }
                fw.write("\n\n");
            }
            fw.close();
        }
        catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
    }


    /**
     * Increments the number of times {@param wordBefore} has been seen before
     * {@param word}.
     *
     * @param word {@code String} seen after {@param wordBefore}
     * @param wordBefore {@code String} seen before {@param word}
     * @param numOccurences the number of times {@param wordBefore} has been seen
     *                      before {@param word}
     */
    private void updatePredecessorBigram(String word, String wordBefore, int numOccurences) {
        HashMap<String, Integer> wordsBefore = this.predecessorBigram.get(word);

        if(wordsBefore==null) {
            wordsBefore = new HashMap<>();
        }

        wordsBefore.put(wordBefore, numOccurences);

        this.predecessorBigram.put(word, wordsBefore);
    }


    /**
     * It goes through the innerMap and sets all unknown words to "UNK".
     * It also updates the predecessorBigram for wordAfter using previous.
     *
     * @param innerMap the current {@code Map}
     * @param newInnerMap the new {@code Map}
     * @param unkWords all {@code String} objects seen less than 10 times
     * @param previous the word seen before {@param innerMap} and
     *                 {@param newInnerMap}
     *
     * @return the number of unknown words seen in {@param innerMap}
     */
    private int setUNKInnerMap(HashMap<String, Integer> innerMap,
                               HashMap<String, Integer> newInnerMap,
                               Set<String> unkWords,
                               String previous) {

        Iterator<String> innerIter = innerMap.keySet().iterator();
        String wordAfter;
        int numOccurences;
        int numUnkOccurences = 0;

        while(innerIter.hasNext()) {
            wordAfter = innerIter.next();
            numOccurences = innerMap.get(wordAfter);

            // if the wordAfter is unknown according to vocabulary
            if(unkWords.contains(wordAfter)) {
                // accumulate all UNK occurrences to add at the end
                numUnkOccurences += numOccurences;
                wordAfter = "UNK";
            }
            else {
                this.totalBigrams += numOccurences;

                newInnerMap.put(wordAfter, numOccurences);
            }

            updatePredecessorBigram(wordAfter, previous, numOccurences);
        }

        return numUnkOccurences;
    }


    /**
     * It updates the "UNK" entry in this.bigramUnkCount using the
     * {@param newInnerMap} and the current {@code Map} associated with "UNK".
     *
     * @param newInnerMap
     */
    private void updateUNKBigrEntry(HashMap<String, Integer> newInnerMap) {
        HashMap<String, Integer> currMap = this.bigramUnkCount.get("UNK");
        Integer currNumOccur;

        if(currMap!=null) {
            for (String word : currMap.keySet()) {
                currNumOccur = newInnerMap.get(word);

                if (currNumOccur == null) {
                    currNumOccur = 0;
                }

                currNumOccur += currMap.get(word);

                newInnerMap.put(word, currNumOccur);
            }
        }

        this.bigramUnkCount.put("UNK", newInnerMap);
    }


    /**
     * Using {@param unkWords} it replaces all words which have been
     * seen less than 10 times with the string "UNK"
     *
     * @param unkWords
     */
    private void setBigramUnk(Set<String> unkWords) {
        Iterator<String> outerIter = this.bigramCount.keySet().iterator();
        String previous;
        int numUnkOccurences;
        HashMap<String, Integer> innerMap;
        // the new map that will be used
        HashMap<String, Integer> newInnerMap;

        while(outerIter.hasNext()) {
            newInnerMap = new HashMap<>();
            previous = outerIter.next();

            innerMap = this.bigramCount.get(previous);

            if(unkWords.contains(previous)) {
                previous = "UNK";
            }

            numUnkOccurences = setUNKInnerMap(innerMap, newInnerMap, unkWords, previous);

            if(numUnkOccurences>0) {
                this.totalBigrams += numUnkOccurences;
                newInnerMap.put("UNK", numUnkOccurences);
            }

            if(previous.equals("UNK")) {
                updateUNKBigrEntry(newInnerMap);
            }
            else {
                this.bigramUnkCount.put(previous, newInnerMap);
            }

        }
    }


    /**
     * It goes through {@param map} and substitutes all instances of {@param word}
     * with "CLUSTER-" + {@param clusterNumber}. It first checks if the map already
     * contains this cluster name and if it does, it retains this information and
     * updates it by adding the occurences of {@param word}.
     *
     * @param map {@code HashMap} to be checked for containing {@param word}
     * @param word {@code String} to be substituted with the cluster name
     * @param clusterNumber {@code int} representing the number of the cluster
     */
    private void subWordWithCluster(HashMap<String, Integer> map, String word, int clusterNumber) {
        String clusterName = "CLUSTER-" + clusterNumber;
        if(map!=null) {
            Integer currClusterNumOccurences = map.get(clusterName);

            if (currClusterNumOccurences == null) {
                currClusterNumOccurences = 0;
            }

            Integer currWordNumOccur = map.get(word);
            if (currWordNumOccur == null) {
                currWordNumOccur = 0;
            }

            map.remove(word);

            map.put(clusterName, (currClusterNumOccurences + currWordNumOccur));
        }

    }


    /**
     * It substitutes the instances of {@param word} with the name
     * "CLUSTER-" + {@param clusterNumber}.
     *
     * @param word {@code String} to be substituted
     * @param clusterNumber {@code int} representing what is the number of the
     *                      cluster in which {@param word} is merged
     */
    private void subClusterForWordInBigrams(String word, int clusterNumber) {
        String clusterName = "CLUSTER-" + clusterNumber;
        HashMap<String, Integer> after = this.bigramUnkCount.get(word);
        HashMap<String, Integer> temp;

        for(String clusterAfter : after.keySet()) {
            // update the predecessorBigram for this cluster so later when
            // it is read, it will correctly reflect the predecessor's name
            temp = this.predecessorBigram.get(clusterAfter);
            subWordWithCluster(temp, word, clusterNumber);
        }
        // if word is followed by itself, its name needs to be updated too
        subWordWithCluster(after, word, clusterNumber);


        HashMap<String, Integer> before = this.predecessorBigram.get(word);
        for(String clusterBefore : before.keySet()) {
            // update the bigramUnkCount so it knows that word has been
            // changed to a cluster and later when it's checked, it will know
            temp = this.bigramUnkCount.get(clusterBefore);
            subWordWithCluster(temp, word, clusterNumber);
        }
        // if word is preceded by itself, its name needs to be updated too
        subWordWithCluster(before, word, clusterNumber);

        after = this.bigramUnkCount.get(word);
        this.bigramUnkCount.remove(word);
        this.bigramUnkCount.put(clusterName, after);

        before = this.predecessorBigram.get(word);
        this.predecessorBigram.remove(word);
        this.predecessorBigram.put(clusterName, before);
    }


    /**
     * It goes through {@code this.sortedUnigramCount} and using the counts,
     * it generates clusters for the 200 most popular words and puts them into
     * {@code this.allClusters}
     *
     * Time complexity: O(k)
     *
     */
    void generateInitialClusters() {
        Cluster cluster;
        int i = 1;

        Iterator<Tuple> iter = this.sortedUnigramCount.iterator();
        Tuple temp;

        while(iter.hasNext() && i<=this.k) {
            temp = iter.next();
            // the data has already been extracted
            iter.remove();
            cluster = new Cluster(i, temp.getWord(), temp.getNumOccurences(), this.totalNumWords);

            this.allClusters.put(i, cluster);

            subClusterForWordInBigrams(temp.getWord(), i);
            i++;
        }
    }


    /**
     * Given {@code Cluster} ID {@param c1} and another {@code Cluster} ID
     * {@param c2}, it returns n(c1, c2).
     *
     * @param c1 first {@code Cluster} ID
     * @param c2 second {@code Cluster} ID
     * @return n(c1, c2)
     */
    private int getN_c1_c2(int c1, int c2) {
        if(c2 == (this.k+2)) {
            HashMap<String, Integer> clustersBefore =
                    this.predecessorBigram.get("CLUSTER-" + c2);
            if(clustersBefore==null) {
                clustersBefore = new HashMap<>();
            }
            Integer n_c1_c2 = clustersBefore.get("CLUSTER-" + c1);
            if (n_c1_c2 == null) {
                return 0;
            } else {
                return n_c1_c2;
            }
        }
        else {
            HashMap<String, Integer> clustersAfter = this.bigramUnkCount.get("CLUSTER-" + c1);
            if(clustersAfter==null) {
                clustersAfter = new HashMap<>();
            }
            Integer n_c1_c2 = clustersAfter.get("CLUSTER-" + c2);
            if (n_c1_c2 == null) {
                return 0;
            } else {
                return n_c1_c2;
            }
        }
    }


    /**
     * Given two clusters it calculates the quality of
     * {@param c} {@param newCluster}. It returns the value of
     * P(c newCluster) * log(P(c newCluster) / (P(c) * P(newCluster))).
     *
     * @param c {@code Cluster} ID before {@param newCluster}
     * @param newCluster {@code Cluster} ID after {@param c}
     * @return the MI of (c newCluster)
     */
    private double getMIOf(int c, int newCluster) {
        int N_c1_c2;

        N_c1_c2 = getN_c1_c2(c, newCluster);

        Cluster c1 = this.allClusters.get(c);

        return c1.checkQuality(this.allClusters.get(newCluster),
                N_c1_c2, this.totalBigrams);
    }


    /**
     * Given a {@code Cluster} id {@param c1} and a {@code Cluster} id
     * {@param c2}, it calculates the weight of the edge between the two
     * {@code Cluster} nodes in the graph.
     *
     * @param c1 the id of the first {@code Cluster}
     * @param c2 the id of the second {@code Cluster}
     * @return the weight that should be put on the graph between the Clusters
     */
    private double getWeight(int c1, int c2) {
        if(c1==c2) {
            return this.getMIOf(c1, c1);
        }
        else {
            double weight;
            weight = this.getMIOf(c1, c2);
            weight += this.getMIOf(c2, c1);

            return weight;
        }
    }


    /**
     * Given {@code Cluster} id {@param c1} and {@code Cluster} id {@param c2}
     * and the weight equal to the MI(c1 c2) + MI(c2 c1), it puts the data into
     * {@code this.graph}.
     *
     * @param c1 the first {@code Cluster} id
     * @param c2 the second {@code Cluster} id
     * @param weight MI(c1 c2) + MI(c2 c1)
     */
    private void putIntoGraph(int c1, int c2, double weight) {
        if(c1<c2) {
            // current edges from c1 to other nodes
            HashMap<Integer, Double> innerMap = this.graph.get(c1);
            // no edges from c1 to other nodes so far
            if(innerMap==null) {
                innerMap = new HashMap<>();
            }
            innerMap.put(c2, weight);

            this.graph.put(c1, innerMap);
        }
        else {
            HashMap<Integer, Double> innerMap = this.graph.get(c2);
            if(innerMap==null) {
                innerMap = new HashMap<>();
            }
            innerMap.put(c1, weight);

            this.graph.put(c2, innerMap);
        }
    }


    /**
     * Initializes the graph between clusters and sets the values
     * of the nodes
     */
    void generateGraph() {
        this.graph = new HashMap<>();
        double weight;

        for(int clusterId =1; clusterId<=this.allClusters.size(); clusterId++) {

            for(int i=clusterId; i<=this.k; i++) {
                weight = this.getWeight(clusterId, i);
                this.putIntoGraph(clusterId, i, weight);
                this.putIntoGraph(i, clusterId, weight);
            }
        }
    }


    /**
     * It goes through the values in {@param currMap} associated with
     * {@param c1} and inserts them in {@param currMap} under
     * "CLUSTER-" + this.k+2. It checks whether the names "CLUSTER-"+c1 or
     * "CLUSTER-" + c2 is amongst the mapped values to {@param c1}.
     * If such values are indeed there, it changes them to "CLUSTER-" +
     * this.k+2. Otherwise, it just copies the cluster names or word names
     * as they are in the new {@code Map} associated with "CLUSTER-"+this.k+2.
     *
     * @param currMap {@code Map} to be traversed
     * @param c1 {@code Cluster} ID whose values will be evaluated
     * @param c2 the name of the second {@code Cluster} that is being merged
     */
    private void imaginaryMapCounts(HashMap<String, HashMap<String, Integer>> currMap,
                                    int c1, int c2) {
        Integer currNumOccurences;
        HashMap<String, Integer> values = currMap.get("CLUSTER-" + c1);
        if(values!=null) {
            HashMap<String, Integer> newMap = currMap.get("CLUSTER-" + this.k + 2);
            if (newMap == null) {
                newMap = new HashMap<>();
            }
            for (String cluster : values.keySet()) {
                // if c1 or c2 is among the entries, switch them
                if (cluster.equals("CLUSTER-" + c1) ||
                        cluster.equals(("CLUSTER-" + c2))) {
                    // preserve the old value
                    currNumOccurences = newMap.get("CLUSTER-" + (this.k + 2));
                    if (currNumOccurences == null) {
                        currNumOccurences = 0;
                    }
                    currNumOccurences += values.get(cluster);
                    newMap.put("CLUSTER-" + (this.k + 2), currNumOccurences);
                }
                // regular entries
                else {
                    // preserve the old value
                    currNumOccurences = newMap.get(cluster);
                    if (currNumOccurences == null) {
                        currNumOccurences = 0;
                    }
                    currNumOccurences += values.get(cluster);
                    newMap.put(cluster + "", currNumOccurences);
                }
            }

            currMap.put("CLUSTER-" + (this.k + 2), newMap);
        }

    }



    /**
     * Using the {@code Cluster} IDs {@param c1} and {@param c2}, it calculates
     * what would be the resulting Cluster if they were to be merged.
     * It looks at what follows {@code Cluster} {@param c1} and what is before
     * it, it looks at the same data for {@param c2} and thus updates the maps
     * without affecting the real data.
     * The imaginary new {@code Cluster} is stored at position this.k+2 in
     * this.allClusters.
     *
     * @param c1 {@code Cluster} ID
     * @param c2 {@code Cluster} ID
     */
    private void createNewCluster(int c1, int c2) {

        Cluster imaginaryCluster = Cluster.mergeTwoClusters(
                this.allClusters.get(c1),
                this.allClusters.get(c2), this.k+2);

        this.allClusters.put(this.k + 2, imaginaryCluster);


        this.imaginaryMapCounts(this.bigramUnkCount, c1, c2);
        this.imaginaryMapCounts(this.bigramUnkCount, c2, c1);
        this.imaginaryMapCounts(this.predecessorBigram, c1, c2);
        this.imaginaryMapCounts(this.predecessorBigram, c2, c1);

    }


    /**
     * Given two {@code Cluster} IDs, it calculates what would be the change
     * in L if {@param c1} and {@param c2} were to be merged. The imaginary
     * new {@code Cluster} is temporarily stored i this.k+2.
     *
     * @param c1 first {@code Cluster} ID
     * @param c2 second {@code Cluster} ID
     * @return the possible resultant change in L
     */
    private double calculateDeltaL(int c1, int c2) {
        this.createNewCluster(c1, c2);

        double deltaL = 0;

        // changes by adding the new cluster which is
        // temporarily stored at this.k+2
        for(int i=1; i<=this.k; i++) {
            if(i!=c1 && i!=c2) {
                deltaL += getWeight(i, this.k+2);
            }
        }

        //removing the old clusters
        HashMap<Integer, Double> innerMap = this.graph.get(c1);
        for(int id : innerMap.keySet()) {
            deltaL -= innerMap.get(id);
        }

        // removing the old clusters
        innerMap = this.graph.get(c2);
        for(int id : innerMap.keySet()) {
            deltaL -= innerMap.get(id);
        }

        return deltaL;
    }


    /**
     * Updates the table {@code deltaL} with the weight {@param deltaL}
     * for the {@code Cluster} objects with IDs {@param c1} and {@param c2}.
     *
     * Time complexity: O(1)
     *
     * @param c1 first {@code Cluster} ID
     * @param c2 second {@code Cluster} ID
     * @param deltaL weight between {@param c1} and {@param c2}
     */
    private void updateDeltaL(int c1, int c2, double deltaL) {
        HashMap<Integer, Double> innerMap = this.tableL.get(c1);
        if(innerMap==null) {
            innerMap = new HashMap<>();
        }
        innerMap.put(c2, deltaL);
        this.tableL.put(c1, innerMap);

        innerMap = this.tableL.get(c2);
        if(innerMap==null) {
            innerMap = new HashMap<>();
        }
        innerMap.put(c1, deltaL);
        this.tableL.put(c2, innerMap);
    }


    /**
     * Initializes the table L for the first this.k {@code Cluster}.
     */
    void initTableL() {
        double deltaL;

        for(int c1=1; c1<=this.k; c1++) {
            for(int c2=c1+1; c2<=this.k; c2++) {
                deltaL = this.calculateDeltaL(c1, c2);
                this.updateDeltaL(c1, c2, deltaL);
            }
        }
    }


    /**
     * Adds a new {@code Cluster} at position this.k+1. It updates the
     * values in this.graph and this.tableL.
     */
    private void addK1Cluster() {
        Iterator<Tuple> iter = this.sortedUnigramCount.iterator();
        // get the next most popular word
        Tuple t = iter.next();
        iter.remove();

        // create a cluster from the next most popular word
        Cluster c = new Cluster(this.k+1, t.getWord(), t.getNumOccurences(), this.totalNumWords);
        this.allClusters.put(this.k+1, c);

        // update the graph
        double weight;
        for(int i=1; i<=this.k+1; i++) {
            weight = this.getWeight(this.k+1, i);
            this.putIntoGraph(this.k+1, i, weight);
            this.putIntoGraph(i, this.k+1, weight);
        }

        // update tableL
        double deltaL;
        for(int c1=1; c1<=this.k; c1++) {
            deltaL = this.calculateDeltaL(c1, this.k+1);
            this.updateDeltaL(c1, this.k+1, deltaL);
        }
    }


    /**
     * Increments the number of times {@param c1} has been seen in {@param map}
     * by {@param incrementalValue}.
     *
     * @param map
     * @param c1
     * @param incrementalValue
     */
    private void incrMapOccurences(HashMap<String, Integer> map, int c1,
                                   int incrementalValue) {
        // if c1 has already been seen in this map, preserve the value
        Integer currNumOccur = map.get("CLUSTER-" + c1);
        if(currNumOccur==null) {
            currNumOccur = 0;
        }

        currNumOccur += incrementalValue;

        map.put("CLUSTER-"+c1, currNumOccur);
    }


    /**
     * It looks at the values in {@param extractMap} associated with {@param c2}
     * and updates {@param updateMap} by incrementing the number of times
     * {@param c1} has been seen in it by the number of times {@param c2} had
     * previously been seen in it. After that it removes {@param c2} from
     * {@param updateMap}.
     *
     * Example.
     * For example through bigramUnkCount we know what is followed by
     * "CLUSTER-"+c2 so we know which clusters in predecessorBigram's values
     * need to be updated as they are now coming after c1 not after c2.
     *
     * @param c1
     * @param c2
     * @param extractMap
     * @param updateMap
     */
    private void updateClusterReferences(int c1, int c2,
                                         HashMap<String, HashMap<String, Integer>> extractMap,
                                         HashMap<String, HashMap<String, Integer>> updateMap) {
        HashMap<String, Integer> extractClusters = extractMap.get("CLUSTER-"+c2);
        HashMap<String, Integer> innerMap;

        if(extractClusters!=null) {
            for (String cluster : extractClusters.keySet()) {
                // increments the value for c1 before/after cluster
                // by the number of times c2 has been seen before cluster
                innerMap = updateMap.get(cluster);
                incrMapOccurences(innerMap, c1, extractClusters.get(cluster));
                // remove "CLUSTER-"+c2 as it is already contained in "CLUSTER-"+c1
                innerMap.remove("CLUSTER-" + c2);
            }
        }
    }

    /**
     * Takes the values from {@param oldMap} and merges them into
     * {@param newMap} either by icrementing already present values in
     * {@param newMap} or by adding a new value.
     *
     * @param oldMap
     * @param newMap
     */
    private void mergeMaps(HashMap<String, Integer> oldMap,
                           HashMap<String, Integer> newMap) {
        if(oldMap!=null) {
            for (String cluster : oldMap.keySet()) {
                // is c1 already followed by the cluster that c2 is
                Integer currNumOccur = newMap.get(cluster);
                if (currNumOccur == null) {
                    currNumOccur = 0;
                }

                // add the number of times cluster has been seen after
                // c2
                currNumOccur += oldMap.get(cluster);
                newMap.put(cluster, currNumOccur);
            }
        }
    }



    private void updateMapCounts(int c1, int c2, Cluster newCluster) {
        // update other clusters' references to c2 to refere to c1 instead
        // update the values too
        updateClusterReferences(c1, c2, this.bigramUnkCount, this.predecessorBigram);
        updateClusterReferences(c1, c2, this.predecessorBigram, this.bigramUnkCount);

        HashMap<String, Integer> clustersAfterC2 = this.bigramUnkCount.get("CLUSTER-"+c2);
        HashMap<String, Integer> clustersBeforeC2 = this.predecessorBigram.get("CLUSTER-"+c2);
        HashMap<String, Integer> clustersAfterC1 = this.bigramUnkCount.get("CLUSTER-"+c1);
        HashMap<String, Integer> clustersBeforeC1 = this.predecessorBigram.get("CLUSTER-"+c1);

        // update all clusters before/after c1 to now include all clusters that
        // were before/after c2
        this.mergeMaps(clustersAfterC2, clustersAfterC1);
        this.mergeMaps(clustersBeforeC2, clustersBeforeC1);

        this.allClusters.put(c1, newCluster);
        // substitute the removed one
        if(c2!=(this.k+1)) {
            this.allClusters.put(c2, this.allClusters.remove(this.k+1));
        }
    }


    private void updateGraphWeights(int c) {
        double weight;
        // update the edges from c1 to any other edge
        for(int i=1; i<=this.k; i++) {
            if(i<c) {
                weight = this.getWeight(i, c);
            }
            else {
                weight = this.getWeight(c, i);
            }
            this.putIntoGraph(i, c, weight);
        }
    }



    private void updateGraph(int c1, int c2) {
        int keptCluster;
        int removedCluster;
        if(c1<c2) {
            keptCluster = c1;
            removedCluster = c2;
        }
        else {
            keptCluster = c2;
            removedCluster = c1;
        }

        // drop the data from c2
        if(removedCluster!=(this.k+1)) {
            this.graph.put(removedCluster, this.graph.remove(this.k+1));
            // update the graph weights for the cluster that took the place
            // of the removed cluster
            updateGraphWeights(removedCluster);
        }

        // update the graph weights for the newly merged cluster
        updateGraphWeights(keptCluster);
    }



    private void updateLForCluster(int c) {
        double deltaL;

        for(int i=1; i<=this.k; i++) {
            if(i<c) {
                deltaL = this.calculateDeltaL(i, c);
                this.updateDeltaL(i, c, deltaL);
            }
            else {
                deltaL = this.calculateDeltaL(c, i);
                this.updateDeltaL(c, i, deltaL);
            }
        }
    }



    private void updateDeltaLAfterMerge(int c1, int c2) {
        int keptCluster;
        int removedCluster;
        if(c1<c2) {
            keptCluster = c1;
            removedCluster = c2;
        }
        else {
            keptCluster = c2;
            removedCluster = c1;
        }

        if(removedCluster!=(this.k+1)) {
            this.tableL.put(removedCluster, this.tableL.remove((this.k + 1)));
            updateLForCluster(removedCluster);
        }

        updateLForCluster(keptCluster);
    }


    /**
     * Given two {@code Cluster} IDs, it merges the two new clusters.
     * It updates the maps responsible for tracking which clusters are followed
     * by which other clusters to reflect that c1 and c2 are now the same.
     * It also updates the word bigrams to reflect that c1 and c2 are now the same.
     *
     * @param c1 first {@code Cluster} ID
     * @param c2 second {@code Cluster} ID
     */
    private void merge(int c1, int c2) {
        Cluster newCluster = Cluster.mergeTwoClusters(this.allClusters.get(c1),
                this.allClusters.get(c2), c1);

        this.updateMapCounts(c1, c2, newCluster);
        this.updateGraph(c1, c2);
        this.updateDeltaLAfterMerge(c1, c2);
    }


    /**
     * Goes through {@code this.tableL} and finds the best pair of clusters to
     * be merged.
     */
    private void mergeBest() {
        double currDeltaL;
        double bestDeltaL = 0;
        int bestLeft=0;
        int bestRight=0;
        HashMap<Integer, Double> innerMap;

        for(int c1=1; c1<=this.k; c1++) {
            innerMap = this.tableL.get(c1);

            for(int c2=c1+1; c2<=this.k+1; c2++) {
                currDeltaL = innerMap.get(c2);
                if(currDeltaL>bestDeltaL) {
                    bestDeltaL = currDeltaL;
                    bestLeft = c1;
                    bestRight = c2;
                }
            }
        }

        merge(bestLeft, bestRight);
    }


    /**
     * It initializes the tableL and then merges clusters into new ones by
     * generating a new K+1 cluster and then merging the clusters which lead
     * to the smallest loss in MI.
     */
    public void cluster() {
        generateGraph();
        initTableL();

        for(int i=0; i<=100; i++) {
            this.addK1Cluster();
            this.mergeBest();
        }
    }


/*    public void genAllClusters() {
        Tuple t;
        Iterator<Tuple> iter = this.sortedUnigramCount.iterator();

        int i = 1;

        while(iter.hasNext() && i<this.k+1000) {
            if(i>this.k) {
                t = iter.next();
                mergeCluster(t.getWord(), t.getNumOccurences());
            }
            else {
                iter.next();
            }

            i++;
        }



        // merges all clusters from between 1 and i<=k
        //mergeFinalClusters();
    }*/



    /*
     * Given a {@param word} creates a cluster for it and finds the best
     * place to merge clusters to bring them down to k.
     * @param word
     * @param numOccurences
     */
/*    private void mergeCluster(String word, int numOccurences) {
        Cluster cluster;
        // add the newest cluster to all my clusters
        Cluster newestCluster = new Cluster(this.k+1, word, numOccurences, this.totalNumWords);
        this.allClusters.put(this.k+1, newestCluster);
        // set up variables to use in the loops
        Cluster clusterAfter;
        double maxValue = 0;
        double temp;
        int N_c1_c2 = 0;
        int leftClusterNum=1;
        int rightClusterNum=1;

        for(int i = 1; i<=this.k; i++) {
            // get the left cluster
            cluster = this.allClusters.get(i);

            for(int j = i+1; j<= this.k+1; j++) {
                // get the right cluster
                clusterAfter = this.allClusters.get(j);
                // what would be the change in quality if merging was to happen
                temp = this.checkClusteringQuality(cluster, clusterAfter);
                // if best so far
                if (temp > maxValue) {
                    maxValue = temp;
                    // update best fit clusters' ids
                    leftClusterNum = i;
                    rightClusterNum = j;
                }
            }
        }

        // add to whatever cluster's id it was found to fit best
        cluster = this.allClusters.get(leftClusterNum);
        clusterAfter = this.allClusters.get(rightClusterNum);

        cluster.mergeClusters(clusterAfter);

        this.allClusters.remove(rightClusterNum);

        if(rightClusterNum != (k+1)) {
            this.allClusters.put(rightClusterNum, this.allClusters.get(k+1));
        }
    }*/


    /*
     * Calculates the new quality of {@param leftCluster} and {@param rightCluster}
     * were to be merged.
     * @param leftCluster
     * @param rightCluster
     * @return
     */
/*    private double checkClusteringQuality(Cluster leftCluster, Cluster rightCluster) {
        Cluster newCluster = Cluster.mergeTwoClusters(leftCluster, rightCluster);
        int idLeft = leftCluster.getId();
        int idRight = rightCluster.getId();

        double quality = 0;

        // clusters have been merged so we are back to k clusters
        for(int i=1; i<=k; i++) {
            // the cluster has not yet been added to this.allClusters
            // but we pretend that the old idLeft and the old idRight are
            // gone
            if(i!=idRight && i<idLeft) {
                quality += this.getMIOf(i, newCluster, true);
            }

            if(i>idLeft && i!=idRight) {
                quality += this.getMIOf(i, newCluster, false);
            }
        }

        return quality;

    }*/





    public HashMap<Integer, Cluster> getAllClusters() {
        return this.allClusters;
    }
}
