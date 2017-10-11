/**
 * Created by Mitko on 10/11/17.
 */
public class Solution {

    public static void main(String[] args) {
        POSTagger posTagger = new POSTagger("train.counts", "test.words");

        // parses the file train.counts and calculates the efficiency
        // and transition probabilities necessary for POS tagging
        posTagger.parseFile();

        posTagger.calcSetEmissionProb();
        posTagger.calcSetTransProb();

        // reads the file test.words, generates the POS tags for all
        // words and saves them into a file called test.tags
        posTagger.generateAllPOSTags();
    }
}
