import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mitko on 10/3/17.
 */
public class LanguageModel {

    private String trainingSet; // the name of the set that will be used for training
    private HashMap<Integer, List<String>> wordsByLine; // all words on all lines
    private List<String> words; // words on line X that are currently processed
    private HashMap<String, Integer> individualCounts;
    private HashMap<String, HashMap<String, Integer>> trigramCounts;

    public LanguageModel() {
        this.trainingSet = "train_set.csv";
        this.wordsByLine = new HashMap<>();
        this.words = new ArrayList<>();
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
    }

    public LanguageModel(String trainingSetName) {
        this.trainingSet = trainingSetName;
        this.words = new ArrayList<>();
        this.wordsByLine = new HashMap<>();
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
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

                extractWordsFromLongComment(firstChar, p, word);
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
                    int numWordsToRemove = findCommentLength(p, words, '>');

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
                        throw new RuntimeException("unknown ending of a string");
                    }
            }
        }

        return p;
    }


    /**
     * Extracts words from an expression like (( ... )).
     *
     * @param firstChar the first character in {@param word}
     * @param p the number of {@param word} in this.words
     * @param word the {@code String} under number {@param p} in this.words
     */
    private void extractWordsFromLongComment(Character firstChar,
                                            int p,
                                            String word) {
        if(firstChar.equals('(') && word.length()!=2) {
            Character secondChar = word.charAt(1);

            if(secondChar.equals('(')) {
                int numWordsToKeep = this.findCommentLength(p, words, ')');

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
                else if (p==this.words.size()-1) {
                    word = this.words.get(p);

                    // lowercase the word and update it in this.words
                    word = word.toLowerCase();
                    this.words.set(p, word);

                    if(endsWithPunctuationMark(word)) {
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

}
