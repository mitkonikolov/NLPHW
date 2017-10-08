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
    private HashMap<Integer, List<String>> wordsByLine;
    private HashMap<String, Integer> individualCounts;
    private HashMap<String, HashMap<String, Integer>> trigramCounts;

    public LanguageModel() {
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trainingSet = "train_set.csv";
        this.wordsByLine = new HashMap<>();
    }

    public LanguageModel(String trainingSetName) {
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trainingSet = trainingSetName;
        this.wordsByLine = new HashMap<>();
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
            List<String> words = this.wordsByLine.get(i);

            for(int p=0; p<words.size(); p++) {
                String word = words.get(p);
                Character firstChar = word.charAt(0);

                switch(firstChar) {
                    case '{':
                    case '}':
                    case '[':
                    case ']':
                    case '+':
                    case '#':
                    case '-':
                        words.remove(p);
                        // we have to look at the character that is now
                        // at position p so we need to go back one step
                        p -= 1;
                        break;
                    case '(':
                    case ')':
                        if(word.length()==2) {
                            words.remove(p);
                            p -= 1;
                        } // in case we have "(( )),"
                        else if(word.length()==3 && endsWithPunctuationMark(word)) {
                            words.set(p, ""+word.charAt(word.length()-1));
                        }
                        break;
                    default:
                        break;
                }

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
        }
    }

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

}
