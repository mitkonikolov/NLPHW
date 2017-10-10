import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Mitko on 10/10/17.
 */
public class POSTagger {

    // how many times does the tag X occur
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
        this.unigramCount = new HashMap<>();
        this.wordTagCount = new HashMap<>();
        this.bigramTagCount = new HashMap<>();
        this.trigramTagCount = new HashMap<>();
        this.fileName = "train.counts";
    }

    public POSTagger(String fName) {
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
}
