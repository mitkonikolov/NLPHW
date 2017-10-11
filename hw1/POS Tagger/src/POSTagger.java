import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Mitko on 10/10/17.
 */
public class POSTagger {

    private String fileName; // the name of the file to be parsed
    private HashMap<String, Integer> tagCount; // how many times total have I seen the tag X

    // how many times total the word X occurs.
    // It is used to generate the class RARE
    private HashMap<String, Integer> wordCount;

    // how many times have I seen a tag assigned to a specific word 
    // word -> (tag -> # instances of the word-tag combo)
    // example: "Korea -> (NOUN -> 10)
    private HashMap<String, HashMap<String, Integer>> wordTagCount;

    // emission probabilities stored in format "USA -> (Noun, 0.84)
    private HashMap<String, HashMap<String, Double>> emissionProbs;


    private HashMap<String, Integer> unigramCount; // how many times have I seen the unigram X
    private HashMap<String, HashMap<String, Integer>> bigramTagCount; // how many times have I seen tag X after tag Y 

    // how many times have I seen tag X after tags Y  Z
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> trigramTagCount;

    public POSTagger() {
        this.wordCount = new HashMap<>();
        this.tagCount = new HashMap<>();
        this.unigramCount = new HashMap<>();
        this.wordTagCount = new HashMap<>();
        this.emissionProbs = new HashMap<>();
        this.bigramTagCount = new HashMap<>();
        this.trigramTagCount = new HashMap<>();
        this.fileName = "train.counts";
    }

    public POSTagger(String fName) {
        this.wordCount = new HashMap<>();
        this.tagCount = new HashMap<>();
        this.unigramCount = new HashMap<>();
        this.wordTagCount = new HashMap<>();
        this.emissionProbs = new HashMap<>();
        this.bigramTagCount = new HashMap<>();
        this.trigramTagCount = new HashMap<>();
        this.fileName = fName;
    }


    /**
     * Parse the file by this.fileName.
     */
    public void parseFile() {
        File f = new File(this.fileName);

        // how many times have we seen the particular instance
        int numOccurences = 0;
        String typeOfInput;

        try {
            Scanner s = new Scanner(f);
            while(s.hasNext()) {

                numOccurences = s.nextInt();
                typeOfInput = s.next();


                if(typeOfInput.equals("WORDTAG")) {
                    parseWordTag(s, numOccurences);
                }
                // N-gram
                else {
                    Character firstChar = typeOfInput.charAt(0);

                    switch(firstChar) {
                        case '1':
                            parseUnigram(s, numOccurences);
                            break;
                        case '2':
                            parseBigram(s, numOccurences);
                            break;
                        default:
                            parseTrigram(s, numOccurences);
                            break;
                    }
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        generateRAREClass();
    }


    /**
     * It parses wordtags and stores them for emissions probability.
     * @param s {@code Scanner} to be used for parsing the file
     * @param numOccurrences # of times the word-tag comb. has been seen
     */
    private void parseWordTag(Scanner s, int numOccurrences) {
        String tag = s.next();
        String word = s.next();

        // Update tagCount
        incrementTagCount(tag, numOccurrences);

        // Update wordCount
        updateWordCount(word, numOccurrences);

        // Update wordTagCount
        updateWordTagCount(word, tag, numOccurrences);
    }


    /**
     * Increments the total number of occurrences of {@param tag} by
     * {@param numOccurrences}.
     *
     * @param tag tag whose occurrence will be incremented
     * @param numOccurrences # by which the occurrences will be increased
     */
    private void incrementTagCount(String tag, int numOccurrences) {
        if(this.tagCount.containsKey(tag)) {
            this.tagCount.put(tag, (this.tagCount.get(tag) + numOccurrences));
        }
        // tag is seen for the first time
        else {
            this.tagCount.put(tag, numOccurrences);
        }
    }


    /**
     * Update the number of times the {@param word} has been seen
     * by {@param numOccurrences}.
     *
     * @param word word whose occurence will be increased
     * @param numOccurrences # by which the occurrences of {@param word}
     *                       will be increased
     */
    private void updateWordCount(String word, int numOccurrences) {
        // wordCount contains word
        if(this.wordCount.containsKey(word)) {
            this.wordCount.put(word, (this.wordCount.get(word) + numOccurrences));
        }
        // the word is not present
        else {
            this.wordCount.put(word, numOccurrences);
        }
    }


    /**
     * Enters the {@param word} {@param tag} combination and the number of
     * times they have occurred together {@param numOccurrences} into
     * this.wordTagCount.
     *
     * @param word {@code String} that will be POS tagged
     * @param tag the POS tag for {@param word}
     * @param numOccurrences the number of times the combination of
     *                      {@param word} and {@param tag} has occurred
     */
    private void updateWordTagCount(String word, String tag, int numOccurrences) {
        HashMap<String, Integer> tags = new HashMap<>();

        if(this.wordTagCount.containsKey(word)) {
            tags = this.wordTagCount.get(word);
        }

        tags.put(tag, numOccurrences);
        this.wordTagCount.put(word, tags);
    }


    /**
     * Creates an entry called RARE and adds all the tags associated with
     * words that have been rarely seen.
     */
    private void generateRAREClass() {
        Set<String> words = this.wordCount.keySet();
        Iterator<String> wordsIter = words.iterator();
        String word = "";

        while(wordsIter.hasNext()) {
            word = wordsIter.next();

            // word will be added to the class RARE
            if(this.wordCount.get(word)<5) {
                HashMap<String, Integer> tagsForWord = this.wordTagCount.get(word);
                HashMap<String, Integer> tagsForRare = new HashMap<>();

                if(this.wordTagCount.containsKey("RARE")) {
                    tagsForRare = this.wordTagCount.get("RARE");
                }

                tagsForRare = mergeTagsMaps(tagsForRare, tagsForWord);

                this.wordTagCount.put("RARE", tagsForRare);
            }
        }
    }


    /**
     * Merges the {@code HashMaps} {@param tagsForRare} and {@param tagsForWord}.
     *
     * @param tagsForRare tags already associated with the class RARE
     * @param tagsForWord tags associated with the new word that is being
     *                    added to the class RARE
     * @return the merged two {@code HashMap} objects
     */
    private HashMap<String, Integer> mergeTagsMaps(HashMap<String, Integer> tagsForRare,
                                                   HashMap<String, Integer> tagsForWord) {
        Set<String> tags = tagsForWord.keySet();
        Iterator<String> tagsIter = tags.iterator();
        String tag;

        while(tagsIter.hasNext()) {
            tag = tagsIter.next();

            if(tagsForRare.containsKey(tag)) {
                tagsForRare.put(tag, (tagsForWord.get(tag) + tagsForRare.get(tag)));
            }
            else {
                tagsForRare.put(tag, tagsForWord.get(tag));
            }
        }

        return tagsForRare;
    }


    /**
     * Returns how many times the {@param word} has been seen marked
     * with {@param tag}. If the word is unknown, it looks for the
     * # of time the RARE->tag combination occurs.
     *
     * @param word a {@code String} to check POS tagging for
     * @param tag a POS {@code String}
     *
     * @return the number of times the combination {@param word} and
     *         {@param tag}
     */
    private int tagWordOccurences(String word, String tag) {
        int countTagForWord = 0;

        HashMap<String, Integer> tags;

        // this word has been seen
        if(this.wordTagCount.containsKey(word)) {
            tags = this.wordTagCount.get(word);
        }
        else {
            // treat the word as RARE
            tags = this.wordTagCount.get("RARE");
        }

        // this tag has been seen after the word (or after RARE)
        if(tags.containsKey(tag)) {
            countTagForWord = tags.get(tag);
        }

        return countTagForWord;
    }


    /**
     * Calculates the emission probability on the whole train set.
     * It iterates through this.wordTagCount, calculates the emission
     * probabilities for each combination of word and tag, and stores
     * them into this.emissionProbs.
     */
    public void calcSetEmissionProb() {
        Set<String> words = this.wordTagCount.keySet();
        Iterator<String> wordsIter = words.iterator();
        String word;
        double probability;

        while(wordsIter.hasNext()) {
            word = wordsIter.next();

            HashMap<String, Integer> tagsAfterCount = this.wordTagCount.get(word);
            Set<String> tags = tagsAfterCount.keySet();
            Iterator<String> tagsIter = tags.iterator();
            String tag;

            while(tagsIter.hasNext()) {
                tag = tagsIter.next();
                probability = calculateEmissionProbability(word, tag);
                inputProbabilityIntoMap(word, tag, probability);
            }
        }
    }


    /**
     * It inputs {@param probability} into this.emissionProbs for {@param word}
     * being marked with {@param tag}.
     *
     * @param word the word that is being POS tagged
     * @param tag the tag that is applied to {@param word}
     * @param probability the probability of {@param word} being tagged with {@param tag}
     */
    private void inputProbabilityIntoMap(String word, String tag, double probability) {

        HashMap<String, Double> tagsProbs;

        // probabilities for this word need to be updated
        if(this.emissionProbs.containsKey(word)) {
            tagsProbs = this.emissionProbs.get(word);

            // this word-tag probability has already been seen
            // (probably impossible but just for any case)
            if(tagsProbs.containsKey(tag)) {
                tagsProbs.put(tag, (tagsProbs.get(tag) + probability));
            }
            // this tag has never been seen after word
            else {
                tagsProbs.put(tag, probability);
            }
        }
        // probabilities for this word have not been entered at all
        else {
            tagsProbs = new HashMap<>();
            tagsProbs.put(tag, probability);
        }

        this.emissionProbs.put(word, tagsProbs);
    }


    /**
     * Calculates the probability that {@param word} is marked with {@param tag}
     *
     * @param word the {@code String} to calculate the probability of
     * @param tag the tag that the word might be marked as
     *
     * @return the probability for {@param word} to be of the POS {@param tag}
     */
    public double calculateEmissionProbability(String word, String tag) {
        // # times this tag has been associated with the given word (or w/ RARE)
        int countTagForWord = tagWordOccurences(word, tag);

        // times the tag has occurred
        int countTagOccurrence = 0;

        // this tag has been seen
        if(this.tagCount.containsKey(tag)) {
            countTagOccurrence = this.tagCount.get(tag);
        }

        if(countTagOccurrence!=0) {
            double res = (double)countTagForWord;
            res = res / countTagOccurrence;
            return res;
        }
        else {
            return 0;
        }
    }


    /**
     * Enters the two tags into this.bigramTagCount either by updating
     * the tag1-tag2 # in the map or by creating new entry in the map.
     *
     * @param s Scanner to be used for parsing
     * @param numOccurences # times the combination of tag1 and tag2 occurs
     */
    public void parseBigram(Scanner s, int numOccurences) {
        String tag1 = s.next();
        String tag2 = s.next();

        // tag1 has already been seen
        if(this.bigramTagCount.containsKey(tag1)) {
            HashMap<String, Integer> tagsAfter = this.bigramTagCount.get(tag1);

            // tag2 has already been seen
            if(tagsAfter.containsKey(tag2)) {
                tagsAfter.put(tag2, tagsAfter.get(tag2) + numOccurences);
            }
            else {
                tagsAfter.put(tag2, numOccurences);
            }

            // update the tags for tag 1
            this.bigramTagCount.put(tag1, tagsAfter);
        }
        // this is the first time tag1 is seen
        else {
            HashMap<String, Integer> tagsAfter = new HashMap<>();
            tagsAfter.put(tag2, numOccurences);

            this.bigramTagCount.put(tag1, tagsAfter);
        }
    }


    /**
     * Calculates the probability that {@param tag2} comes after {@param tag1}.
     *
     * @param tag2 the second tag
     * @param tag1 the first tag
     * @return the probability of tag2 coming after tag1
     */
    public double calculateTransitionProbability(String tag2, String tag1) {
        int occurencesOfT1T2 = 0;

        // tag1 has been seen
        if(this.bigramTagCount.containsKey(tag1)) {
            HashMap<String, Integer> tagsAfterTag1 = this.bigramTagCount.get(tag1);

            // tag2 has been seen after tag1
            if(tagsAfterTag1.containsKey(tag2)) {
                occurencesOfT1T2 = tagsAfterTag1.get(tag2);
            }
        }

        int occurencesTag1 = calculateBigramTagOccurence(tag1);

        if(occurencesTag1!=0) {
            double res = occurencesOfT1T2;
            res = res / occurencesTag1;
            return res;
        }
        else {
            return 0;
        }
    }


    /**
     * It calculates how many times the {@param tag} has occurred before
     * any other tag in a bigram from the train set.
     *
     * @param tag the tag to calculate total occurrences of
     * @return the total number of times the tag {@param tag} has occurred
     *         before any other tag in a bigram
     */
    private int calculateBigramTagOccurence(String tag) {
        int occurences = 0;

        if(this.bigramTagCount.containsKey(tag)) {
            HashMap<String, Integer> tagsAfter = this.bigramTagCount.get(tag);

            Set<String> tags = tagsAfter.keySet();
            Iterator<String> tagsIter = tags.iterator();
            String tagAfter;

            while(tagsIter.hasNext()) {
                tagAfter = tagsIter.next();

                occurences += tagsAfter.get(tagAfter);
            }

            return occurences;
        }
        else {
            return 0;
        }
    }


    private void parseUnigram(Scanner s, int numOccurences) {
        String tag = s.next();

        // the tag has already been seen
        if(this.unigramCount.containsKey(tag)) {
            this.unigramCount.put(tag, (this.unigramCount.get(tag) + numOccurences));
        }
        // this is the first time this tag is seen
        else {
            this.unigramCount.put(tag, numOccurences);
        }
    }


    private void parseTrigram(Scanner s, int numOccurences) {
        String tag1 = s.next();
        String tag2 = s.next();
        String tag3 = s.next();

        // tag1 has been seen
        if(this.trigramTagCount.containsKey(tag1)) {

            HashMap<String, HashMap<String, Integer>> tagsAfter = this.trigramTagCount.get(tag1);

            // tag2 has been seen after tag1
            if(tagsAfter.containsKey(tag2)) {

                HashMap<String, Integer> innerTagsAfter = tagsAfter.get(tag2);

                // tag3 has been seen after tag2 after tag1
                if(innerTagsAfter.containsKey(tag3)) {
                    innerTagsAfter.put(tag3, innerTagsAfter.get(tag3) + numOccurences);
                }
                // tag3 is seen for the first time
                else {
                    innerTagsAfter.put(tag3, numOccurences);
                }

                // update tagsAfter tag2
                tagsAfter.put(tag2, innerTagsAfter);
            }
            // tag2 is seen for the first time
            else {
                HashMap<String, Integer> innerTagsAfter = new HashMap<>();
                innerTagsAfter.put(tag3, numOccurences);

                tagsAfter.put(tag2, innerTagsAfter);
            }

            // update the tags after tag1
            this.trigramTagCount.put(tag1, tagsAfter);

        }
        // this is the first time tag1 is seen
        else {
            HashMap<String, Integer> innerTagsAfter = new HashMap<>();
            innerTagsAfter.put(tag3, numOccurences);

            HashMap<String, HashMap<String, Integer>> tagsAfter = new HashMap<>();
            tagsAfter.put(tag2, innerTagsAfter);

            this.trigramTagCount.put(tag1, tagsAfter);
        }
    }


    public HashMap<String, Integer> getUnigramCount() {
        return this.unigramCount;
    }


    public HashMap<String, HashMap<String, Integer>> getBigramTagCount() {
        return this.bigramTagCount;
    }


    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getTrigramCount() {

        return this.trigramTagCount;
    }


    public HashMap<String, Integer> getTagCount() {
        return this.tagCount;
    }


    public HashMap<String, Integer> getWordCount() {
        return this.wordCount;
    }


    public HashMap<String, HashMap<String, Integer>> getWordTagCount() {
        return this.wordTagCount;
    }


    public HashMap<String, HashMap<String, Double>> getSetEmissionProbs() {
        return this.emissionProbs;
    }
}
