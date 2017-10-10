import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Mitko on 10/10/17.
 */
public class POSTagger {

    // how many times total have I seen the tag X
    private HashMap<String, Integer> tagCount;

    // how many times total have I seen word X
    private HashMap<String, Integer> wordCount;

    // how many times have I seen the unigram X
    private HashMap<String, Integer> unigramCount;

    // how many times have I seen tag X after tag Y 
    private HashMap<String, HashMap<String, Integer>> bigramTagCount;

    // how many times have I seen tag X after tags Y  Z
    private HashMap<String, HashMap<String, HashMap<String, Integer>>> trigramTagCount;

    // how many times have I seen a tag assigned to a specific word 
    // word -> (tag -> # instances of the word-tag combo)
    // example: "Korea -> (NOUN -> 10)
    private HashMap<String, HashMap<String, Integer>> wordTagCount;

    // the name of the file to be parsed
    private String fileName;

    public POSTagger() {
        this.wordCount = new HashMap<>();
        this.tagCount = new HashMap<>();
        this.unigramCount = new HashMap<>();
        this.wordTagCount = new HashMap<>();
        this.bigramTagCount = new HashMap<>();
        this.trigramTagCount = new HashMap<>();
        this.fileName = "train.counts";
    }

    public POSTagger(String fName) {
        this.wordCount = new HashMap<>();
        this.tagCount = new HashMap<>();
        this.unigramCount = new HashMap<>();
        this.wordTagCount = new HashMap<>();
        this.bigramTagCount = new HashMap<>();
        this.trigramTagCount = new HashMap<>();
        this.fileName = fName;
    }

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
    }


    private void parseWordTag(Scanner s, int numOccurences) {
        String tag = s.next();
        String word = s.next();

        // Update tagCount
        // tagCount contains tag
        if(this.tagCount.containsKey(tag)) {
            this.tagCount.put(tag, (this.tagCount.get(tag) + numOccurences));
        }
        // tag is seen for the first time
        else {
            this.tagCount.put(tag, numOccurences);
        }


        // Update wordCount
        updateWordCount(word);



        // update the tags for this word
        if(this.wordTagCount.containsKey(word)) {
            HashMap<String, Integer> tags = this.wordTagCount.get(word);

            // the tag was already in this map
            if(tags.containsKey(tag)) {
                tags.put(tag, (tags.get(tag) + numOccurences));
            }
            // the tag is new to this map
            else {
                tags.put(tag, numOccurences);
            }

            // update the tags for this word
            this.wordTagCount.put(word, tags);
        }
        // add the first tag for this word
        else {
            HashMap<String, Integer> tags = new HashMap<>();
            tags.put(tag, numOccurences);

            this.wordTagCount.put(word, tags);
        }
    }


    private void updateWordCount(String word) {
        // wordCount contains word
        if(this.wordCount.containsKey(word)) {
            int wordOccurences = this.wordCount.get(word);

            // remove from class RARE
            if(wordOccurences == 4) {
                wordOccurences += 1;
                this.wordCount.put(word, wordOccurences);
                this.wordCount.put("RARE", (this.wordCount.get("RARE") - 4));
            }
            // the word should stay in class RARE for now
            else if(wordOccurences < 4) {
                wordOccurences += 1;
                this.wordCount.put(word, wordOccurences);
                this.wordCount.put("RARE", (this.wordCount.get("RARE") + 1));
            }
            // the word occurs 5 or more times and has already been removed
            // from the RARE class
            else {
                this.wordCount.put(word, this.wordCount.get(word)+1);
            }
        }
        // the word is not present so it will be counted both as
        // itself and as RARE for now
        else {
            // count the word as it is too
            this.wordCount.put(word, 1);

            // increment the RARE class
            if(this.wordCount.containsKey("RARE")) {
                this.wordCount.put("RARE", (this.wordCount.get("RARE")+1));
            }
            // start the RARE class
            else {
                this.wordCount.put("RARE", 1);
            }
        }
    }


    public HashMap<String, HashMap<String, Integer>> getWordTagCount() {
        return this.wordTagCount;
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





    public HashMap<String, Integer> getUnigramCount() {
        return this.unigramCount;
    }


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


    public HashMap<String, HashMap<String, Integer>> getBigramTagCount() {
        return this.bigramTagCount;
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


    public HashMap<String, HashMap<String, HashMap<String, Integer>>> getTrigramCount() {

        return this.trigramTagCount;
    }


    /**
     * Calculates the probability that {@param word} is marked with {@param tag}
     * @param word
     * @param tag
     * @return
     */
    public double calculateEmissionProbability(String word, String tag) {
        int countTagForWord = 0;

        // this word has been seen
        if(this.wordTagCount.containsKey(word)) {
            HashMap<String, Integer> tagsForWord = this.wordTagCount.get(word);

            // this tag has been seen after the word
            if(tagsForWord.containsKey(tag)) {
                countTagForWord = tagsForWord.get(tag);
            }
        }
        else {
            // treat the word as RARE and see how many times RARE
            // has been see
            if(this.wordCount.containsKey("RARE")) {
                countTagForWord = this.wordCount.get("RARE");
            }
        }

        int countTagOccurence = 0;

        // this tag has been seen
        if(this.tagCount.containsKey(tag)) {
            countTagOccurence = this.tagCount.get(tag);
        }

        if(countTagOccurence!=0) {
            double res = (double)countTagForWord;
            res = res / countTagOccurence;
            return res;
        }
        else {
            return 0;
        }
    }


    public HashMap<String, Integer> getTagCount() {
        return this.tagCount;
    }


    /**
     * Calculates the probability that {@param tag2} comes after {@param tag1}.
     *
     * @param tag2
     * @param tag1
     * @return
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

        int occurenceTag1 = 0;

        occurenceTag1 = calculateBigramTagOccurence(tag1);

        if(occurenceTag1!=0) {
            double res = occurencesOfT1T2;
            res = res / occurenceTag1;
            return res;
        }
        else {
            return 0;
        }
    }



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


}
