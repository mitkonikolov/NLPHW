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

    private HashMap<String, Integer> individualCounts;
    private HashMap<String, HashMap<String, Integer>> trigramCounts;
    private String trainingSet;

    public LanguageModel() {
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trainingSet = "train_set2.csv";
    }

    public LanguageModel(String trainingSetName) {
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
        this.trainingSet = trainingSetName;
    }


    /**
     *  Parses the given training corpus. It extracts the data from a csv file
     *  and calls another method in order to record the information.
     */
    public HashMap<Integer, List<String>> parseTrainingSet() {
        HashMap<Integer, List<String>> result = new HashMap<>();

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

                    temp = whiteSpaces.split(nextLine[8], 0);

                    for(int i=0; i<temp.length; i++) {
                        // accumulate all the words in a list
                        words.add(temp[i]);
                    }

                    // put the list of words in the map
                    result.put(lineNumber, words);
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
            System.out.println("File was not found. Please check if the csv" +
                    " file was in the entered directory");
            e.printStackTrace();
        }

        return result;
    }
}
