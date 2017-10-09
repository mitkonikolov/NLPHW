import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mitko on 10/3/17.
 */
public class LanguageModel {

    private String trainingSet; // the name of the set that will be used for training
    private HashMap<Integer, List<String>> wordsByLine; // all words on all lines
    private List<String> words; // words on line X that are currently processed
    private HashMap<String, Integer> individualCounts;
    // Format: <words> ---seen before---> (word, count)
    private HashMap<String, HashMap<String, Integer>> trigramCounts;
    // trigram count for words currently unknown.
    // Format: <word> ---seen after---> (words, count)
    private HashMap<String, HashMap<String, Integer>> trigramCountsUNK;

    public int getA() {
        return a;
    }

    public void incrementA() {
        this.a += 1;
    }

    private int a;

    public LanguageModel() {
        this.trainingSet = "train_set.csv";
        this.wordsByLine = new HashMap<>();
        this.words = new ArrayList<>();
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trigramCountsUNK = new HashMap<>();
    }

    public LanguageModel(String trainingSetName) {
        a = 1;
        this.trainingSet = trainingSetName;
        this.words = new ArrayList<>();
        this.wordsByLine = new HashMap<>();
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trigramCountsUNK = new HashMap<>();
    }


    /**
     *  Parses the given training corpus. It extracts the data from a csv file
     *  and calls another method in order to record the information.
     */
    public void parseTrainingSet() {

        try {
            CSVReader reader = new CSVReader(new FileReader(this.trainingSet));

            String[] nextLine;
            String[] temp;

            try {
                // eliminate the first field that says "text"
                // and skip the blank line under it
                reader.readNext();
                reader.readNext();

                Pattern whiteSpaces = Pattern.compile("\\s*\\s");

                int lineNumber = 0;

                while ((nextLine = reader.readNext()) != null) {
                    ArrayList<String> words = new ArrayList<>();

                    // split the sentence into separate words
                    temp = whiteSpaces.split(nextLine[8], 0);

                    // accumulate all the words in a list
                    for(int i=0; i<temp.length; i++) {
                        words.add(temp[i]);
                    }

                    // put the list of words in the map organized by line #
                    this.wordsByLine.put(lineNumber, words);
                    lineNumber += 1;

                    // ignore the next line because it is empty
                    reader.readNext();
                }
            }
            catch (IOException e) {
                e.getMessage();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File was not found. Check given directory.");
        }
    }


    /**
     * Returns the field {@code wordsByLine}.
     *
     * @return {@code wordsByLine}
     */
    public HashMap<Integer, List<String>> getWordsByLine() {
        return this.wordsByLine;
    }


    /**
     * Removes the meaningless characters and strings from the
     * list of words.
     */
    public void remExtraChars() {
        for(int i=0; i<this.wordsByLine.size(); i++) {
            this.words = this.wordsByLine.get(i);

            for(int p=0; p<words.size(); p++) {
                String word = this.words.get(p);
                Character firstChar = word.charAt(0);

                p = removeShortComments(firstChar, p, word);

                p = removeLongComments(firstChar, p, word);

                extractWordsFromLongComment(firstChar, p, word, '(',')');
            }
        }
    }


    /**
     * Returns {@code true} if and only if the given {@param word} ends
     * with period, comma, exclamation mark or question mark.
     *
     * @param word the given word to check for its ending
     * @return {@code true} ioi the given word ends with a punctuation mark
     */
    private boolean endsWithPunctuationMark(String word) {
        Character lastChar = word.charAt(word.length()-1);
        if(lastChar.equals('.') ||
                lastChar.equals(',') ||
                lastChar.equals('!') ||
                lastChar.equals('?')) {
            return true;
        }
        else {
            return false;
        }
    }

    // toDO: when I start the counting I have to remove anything
    // toDO: that starts with *[[ and replace it with </s>

    /**
     * Removes comments of the type "{ ... }", "[ ... ]", "+", "-", "--",
     * "-/", "#", and "/".
     *
     * @param firstChar the first character in {@param word}
     * @param p the number of the {@param word} in this.words
     * @param word the {@code String} in this.words under number p
     *
     * @return the number {@param p} representing the number of the currently
     *          investigated {@code String} in this.words
     */
    private int removeShortComments(Character firstChar, int p, String word) {
        switch(firstChar) {
            case '{':
            case '}':
            case '[':
            case ']':
            case '+':
            case '#':
            case '-':
            case '/':
                this.words.remove(p);
                // we have to look at the character that is now
                // at position p so we need to go back one step
                p -= 1;
                break;
            case '(':
            case ')':
                if(word.length()==2) {
                    this.words.remove(p);
                    p -= 1;
                } // in case we have "(( )),"
                else if(word.length()==3 && endsWithPunctuationMark(word)) {
                    this.words.set(p, ""+word.charAt(word.length()-1));
                }
                break;
            default:
                break;
        }

        return p;
    }


    /**
     * It removes long comments of the type <...>, <<...>>, and <+...+>.
     *
     * @param firstChar the first character in {@param word}
     * @param p the number of the word on the line this.words
     * @param word the word with number p in this.words
     * @return the index {@param p}
     */
    private int removeLongComments(Character firstChar, int p, String word) {
        if(firstChar.equals('<')) {
            Character secondChar = word.charAt(1);

            switch(secondChar) {
                case '<':
                case '+':
                    // how many words will I have to remove
                    p = findAndRemoveLongComment(p, '>');

                    //p = removeWords(numWordsToRemove, p);

                    break;
                default:
                    Character lastChar = word.charAt(word.length()-1);
                    if(this.endsWithPunctuationMark(word)) {
                        String newString = "" + lastChar;
                        words.set(p, newString);
                    }
                    else if(lastChar.equals('>')) {
                        words.remove(p);
                        p -= 1;
                    }
                    else {
                        //throw new RuntimeException("unknown ending of a string " + word);
                        p = findAndRemoveLongComment(p, '>');
                        //p = removeWords(numWordsToRemove, p);
                    }
            }
        }

        return p;
    }


    /**
     * Given a beginning of a block (like < ... > or <+ ... +> or ((...)) )
     * through the parameter {@param p}, it starts going through the
     * words in this.words and looks for the end of the block. It defines
     * the end as the occurence of the {@code Character} {@param finish}.
     * As it goes through the block, it deletes words which are part of the
     * comment and preserves punctuation marks if they are at the end of the
     * comment as in for example: <...>,
     *
     * @param p the word number where the block starts
     * @param finish {@code Character} which signifies the end of the block
     * @return an {@code int} the current word that must be considered
     */
    private int findAndRemoveLongComment(int p, Character finish) {

        int wordSize = this.words.size();

        for(int i=p; i<wordSize; i++) {
            String word = this.words.get(p);

            Character lastChar;

            if(endsWithPunctuationMark(word)) {
                lastChar = word.charAt(word.length()-2);

                // this is the last word and there is a punc. mark I
                // need to preserve
                if(lastChar.equals(finish)) {
                    this.words.set(p, word.charAt(word.length()-1)+"");

                    // update p because there might not be p+1 anymore
                    return (p-1);
                }
                // this is not the last word so I just remove it
                else {
                    this.words.remove(p);
                }

            }
            else {
                lastChar = word.charAt(word.length()-1);

                this.words.remove(p);

                // this is the last word and it does not finish with a
                // punctuation mark that I need to preserve
                if(lastChar.equals(finish)) {


                    // update p because there might not be p+1 anymore
                    return (p-1);
                }
            }
        }

        return -1;
    }


    /**
     * Remove as many words as numWordsToRemove says starting at p.
     *
     * @param numWordsToRemove the # of words to remove
     * @param p the word that is currently being considered
     * @return the updated {@param p}
     */
/*    private int removeWords(int numWordsToRemove, int p) {
        for (int wordInd = 0; wordInd < numWordsToRemove; wordInd++) {
            // is this the last word I need to remove
            // and does it finish with a punc. mark?
            if((wordInd == (numWordsToRemove-1)) &&
                    this.endsWithPunctuationMark(words.get(p))) {
                String newString = words.get(p);
                newString = "" + newString.charAt(newString.length()-1);
                words.set(p, newString);
            }
            else {
                // this is not the last word or there is no
                // punctuation mark that I need to preserve
                words.remove(p);
            }
        }
        // p+1 might not exist anymore
        p -= 1;

        return p;
    }*/


    /**
     * Extracts words from an expression like (( ... )).
     *
     * @param firstChar the first character in {@param word}
     * @param p the number of {@param word} in this.words
     * @param word the {@code String} under number {@param p} in this.words
     */
    private void extractWordsFromLongComment(Character firstChar,
                                             int p,
                                             String word,
                                             Character matchBeginning,
                                             Character matchEnding) {
        if(firstChar.equals(matchBeginning) && word.length()!=2) {
            Character secondChar = word.charAt(1);

            if(secondChar.equals(matchBeginning)) {
                int numWordsToKeep = this.findCommentLength(p, words, matchEnding);

                int wordInd = p;

                for (int k = 0; k < numWordsToKeep; k++) {
                    if (wordInd == p) {
                        words.set(wordInd, word.substring(2));
                    }

                    if (k == (numWordsToKeep - 1)) {
                        String temp = words.get(wordInd);

                        if(this.endsWithPunctuationMark(temp)) {
                            Character puncMark = temp.charAt(temp.length()-1);
                            words.set(wordInd, temp.substring(0,
                                    temp.length() - 3));
                            words.add(wordInd+1, ""+puncMark);
                        }
                        else {
                            words.set(wordInd, temp.substring(0, temp.length() - 2));
                        }
                    }

                    wordInd += 1;
                }
            }
        }
    }


    /**
     * Given a beginning of a block (like < ... > or <+ ... +> or ((...)) )
     * through the parameter {@param wordNumber}, it starts going through the
     * words in {@param words} and looks for the end of the block. It defines
     * the end as the occurence of the {@code Character} {@param finish}.
     *
     * @param wordNumber the word number where the block starts
     * @param words {@code list} of words
     * @param finish {@code Character} which signifies the end of the block
     * @return an {@code int} showing the length of the block in separate words
     */
    private int findCommentLength(int wordNumber, List<String> words, Character finish) {
        int numWordsRemoved = 1;
        for(int i=wordNumber; i<words.size(); i++) {
            String word = words.get(i);
            Character lastChar;

            if(endsWithPunctuationMark(word)) {
                lastChar = word.charAt(word.length()-2);
            }
            else {
                lastChar = word.charAt(word.length()-1);
            }

            if(lastChar.equals(finish)) {
                return numWordsRemoved;
            }
            else {
                numWordsRemoved += 1;
            }
        }

        return -1;
    }


    /**
     * It makes all {@code Strings} in wordsByLine lowercase, separates all
     * puncuation marks from words and adds the beginning of a sentence
     * <s><s> and end of sentence </s> strings.
     */
    public void finishProcessingWords() {
        for(int i=0; i<this.wordsByLine.size(); i++) {
            this.words = this.wordsByLine.get(i);

            for(int p=0; p<this.words.size(); p++) {

                String word;
                // this is the first word an <s><s> needs to be added before it
                if(p==0) {
                    this.words.add(0, "<s><s>");
                }
                // this is the last word and </s> needs to be added after it
                else if (p==(this.words.size()-1)) {
                    word = this.words.get(p);

                    // lowercase the word and update it in this.words
                    word = word.toLowerCase();
                    this.words.set(p, word);

                    // split the word into separate parts if it contains
                    // an apostrophy and update the variable word
                    p = this.containsApostrophy(word, p);
                    word = this.words.get(p);

                    if(endsWithPunctuationMark(word) && (word.length() > 1)) {
                        Character puncMark = word.charAt(word.length()-1);

                        // update the current word by removing the punc mark
                        this.words.set(p, word.substring(0, word.length()-1));

                        // add the punctuation mark and </s>
                        this.words.add(puncMark+"");
                        this.words.add("</s>");

                        // make sure that p is equal to the bound so that we
                        // do not spend extra time processing the words that
                        // were just added.
                        p=this.words.size();
                    }
                    else {
                        this.words.add("</s>");
                        p = this.words.size();
                    }
                }
                else {
                    word = this.words.get(p);

                    // lowercase the word and update it in this.words
                    word = word.toLowerCase();
                    this.words.set(p, word);

                    // split the word into separate parts if it contains
                    // an apostrophy and update the variable word
                    p = this.containsApostrophy(word, p);
                    word = this.words.get(p);

                    if(endsWithPunctuationMark(word) && word.length()>1) {
                        Character puncMark = word.charAt(word.length()-1);

                        // update the current word by removing the punc mark
                        this.words.set(p, word.substring(0, word.length()-1));

                        // add the punctuation mark right after the current word
                        this.words.add(p+1, puncMark+"");
                    }
                }

            }

            // update the line in this.wordsByLine
            this.wordsByLine.put(i, this.words);
        }
    }

    /**
     * Checks if the given {@param word} contains an apostrophy and if it does,
     * it splits it. It updates the word at position p to be the substring
     * before the apostrophy and adds after it an apostrophy and a substring
     * of whatever was after the apostrophy.
     *
     * @param word {@code String} to be checked for apostrophy
     * @param p {@code int} representing the position at which the {@param word} is
     * @return the index {@param p}
     */
    private int containsApostrophy(String word, int p) {

        if(word.contains("'")) {
            String[] ws = word.split("'");
            this.words.set(p, ws[0]);
            this.words.add(p+1, "'");

            // in words like kids', there is text only before the apostrophy
            if(ws.length>1) {
                this.words.add(p + 2, ws[1]);
                p += 2;
            }
            else {
                p += 1;
            }

        }

        return p;
    }


    /**
     * It goes through the data and calculates individual and trigram count.
     */
    public void learn() {
        for(int i=0; i<this.wordsByLine.size(); i++) {
            this.words = this.wordsByLine.get(i);

            countIndividualAndTrigram();
        }
    }


    /**
     * Counts individually all the words from the line this.words
     * into this.individualCounts and manages the trigram count using UNK tag.
     */
    private void countIndividualAndTrigram() {
        String word;
        for(int i=0; i<this.words.size(); i++) {
            word = this.words.get(i);

            // the word is already in the map so I increment its value
            if(this.individualCounts.containsKey(word)) {
                individualCounts.put(word,
                        (individualCounts.get(word) + 1));
            }
            // the word had not been seen until now so I need to enter it
            else {
                individualCounts.put(word, 1);
            }

            String previousTwoWords = "";

            switch(i) {
                case 0:
                    break;
                case 1:
                    previousTwoWords = "<s><s>";
                    break;
                case 2:
                    previousTwoWords = "<s> " + this.words.get(1);
                    break;
                default:
                    previousTwoWords = this.words.get(i - 2) + " " + this.words.get(i-1);
                    break;
            }

            // if we are looking at the first word or futher
            if(i>0) {
                int numberOfOccurences = this.individualCounts.get(word);

                // the word was just seen for the fifth time and just became
                // known
                if(numberOfOccurences == 5) {
                    // final increment before change
                    incrementTrigramCounts(previousTwoWords, "UNK");
                    incrementTrigramCountsUNK(previousTwoWords, word);

                    // using the data in this.trigramCountsUNK it changes the
                    // word UNK to known by updating this.trigramCounts
                    changeUnkToKnown(word);
                }
                // the word has already been known
                else if(numberOfOccurences > 5) {
                    incrementTrigramCounts(previousTwoWords, word);
                }
                // the word is unknown
                else {
                    incrementTrigramCounts(previousTwoWords, "UNK");
                    incrementTrigramCountsUNK(previousTwoWords, word);
                }
            }
        }
    }

    public HashMap<String, Integer> getIndividualCounts() {
        return this.individualCounts;
    }

    public HashMap<String, HashMap<String, Integer>> getTrigramCounts() {
        return this.trigramCounts;
    }

    private void incrementTrigramCounts(String previousTwoWords, String word) {
        // have I seen these previousTwoWords already
        if(this.trigramCounts.containsKey(previousTwoWords)) {
            HashMap<String, Integer> wordsAfter = this.trigramCounts.get(previousTwoWords);

            // I have seen this word after these specific words already
            if(wordsAfter.containsKey(word)) {
                // increment the count
                wordsAfter.put(word, (wordsAfter.get(word) + 1));
            }
            // I have not seen this word after this specific words until now
            else {
                wordsAfter.put(word, 1);
            }

            this.trigramCounts.put(previousTwoWords, wordsAfter);
        }
        // I have not seen these previousTwoWords up until now
        else {
            // create a hashmap of strings and integeres for the words after
            HashMap<String, Integer> wordsAfter = new HashMap<>();
            wordsAfter.put(word, 1);

            // add the previousTwoWords to trigramCounts with the wordsAfter
            this.trigramCounts.put(previousTwoWords, wordsAfter);
        }
    }


    private void incrementTrigramCountsUNK(String previousTwoWords, String word) {
        // have I seen this word previously
        if(this.trigramCountsUNK.containsKey(word)) {
            HashMap<String, Integer> wordsBefore = this.trigramCountsUNK.get(word);

            // have I seen these previousTwoWords before this word already
            if(wordsBefore.containsKey(previousTwoWords)) {
                // increment the value
                wordsBefore.put(previousTwoWords,
                        (wordsBefore.get(previousTwoWords) + 1));
            }
            // this is the first time I see the previousTwoWords before this word
            else {
                wordsBefore.put(previousTwoWords, 1);
            }
        }
        // I have not seen this word previously
        else {
            HashMap<String, Integer> wordsBefore = new HashMap<>();
            // this must be the first time I see these previousTwoWords
            wordsBefore.put(previousTwoWords, 1);

            this.trigramCountsUNK.put(word, wordsBefore);
        }
    }


    private void changeUnkToKnown(String word) {
        // words that have been seen before word
        HashMap<String, Integer> wordsBefore = this.trigramCountsUNK.get(word);

        Iterator iterateWordsBefore = wordsBefore.entrySet().iterator();

        while(iterateWordsBefore.hasNext()) {
            String expression = iterateWordsBefore.next().toString();
            String separateWords[] = expression.split("=");

            // words before word
            String words = separateWords[0];
            // how many times have I seen word after these wordsBefore
            int occurences = wordsBefore.get(words);

            // the current wordsAfter which list the word as UNK
            HashMap<String, Integer> wordsAfter = this.trigramCounts.get(words);

            // decrease the number of UNK after wordsBefore
            wordsAfter.put("UNK", (wordsAfter.get("UNK") - occurences));
            // put the number of occurences as for a known word
            wordsAfter.put(word, occurences);

            // finally, update this.trigramCounts
            this.trigramCounts.put(words, wordsAfter);
        }
    }


    /**
     * Performs add1 smoothing by adding 1 to each of the numbers
     * in this.trigramCounts.
     */
    public void add1Smoothing() {

        Set<String> allBigrams = this.trigramCounts.keySet();

        Iterator bigramsIter = allBigrams.iterator();

        String key;

        String innerKey;

        while(bigramsIter.hasNext()) {
            // get the bigram key to update the values for the words after it
            key = bigramsIter.next().toString();

            HashMap<String, Integer> wordsAfter = this.trigramCounts.get(key);

            Set<String> allUnigrams = wordsAfter.keySet();
            Iterator unigramIter = allUnigrams.iterator();

            while(unigramIter.hasNext()) {
                innerKey = unigramIter.next().toString();
                int currentNumberOfOccurences = wordsAfter.get(innerKey);

                // add 1 smoothing
                currentNumberOfOccurences += 1;

                wordsAfter.put(innerKey, currentNumberOfOccurences);
            }

            this.trigramCounts.put(key, wordsAfter);
        }

    }

}
