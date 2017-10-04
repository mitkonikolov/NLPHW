import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Mitko on 10/3/17.
 */
public class LanguageModel {

    private HashMap<String, Integer> individualCounts;
    private HashMap<String, HashMap<String, Integer>> trigramCounts;

    public LanguageModel() {
        this.individualCounts = new HashMap<>();
        this.trigramCounts = new HashMap<>();
    }


    /**
     *  Parses the given training corpus. It extracts the data from a csv file
     *  and calls another method in order to record the information.
     */
    public void parseTrainingSet() {
        try {
            CSVReader reader = new CSVReader(new FileReader("train_set.csv"));

            String[] nextLine;

            try {
                // eliminate the first field that says "text"
                reader.readNext();
                reader.readNext();
                while ((nextLine = reader.readNext()) != null) {
                    System.out.println(nextLine[8]);
                    // ignore the next line because it is empty
                    reader.readNext();
                }
            }
            catch (IOException e) {
                e.getMessage();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File was not found. Please check if the csv" +
                    " file was in the described directory");
            e.printStackTrace();
        }
    }
}
