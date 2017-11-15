import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Mitko on 10/25/17.
 */
public class SentimentAnalyzer {
    private int numPositiveDocs;
    private int numNegativeDocs;
    private HashMap<String, Double> probsForClasses;
    private HashMap<String, Integer> bagOfWordsPositive;
    private HashMap<String, Integer> bagOfWordsNegative;
    private Set<String> allDistinctWords;
    private int allPositiveWords;
    private int allNegativeWords;
    private BigDecimal probPositive;
    private BigDecimal probNegative;

    private int truePositive;
    private int falsePositive;
    private int trueNegative;
    private int falseNegative;

    private double precision;
    private double recall;
    private double f1;


    public SentimentAnalyzer() {
        this.numPositiveDocs = 0;
        this.numNegativeDocs = 0;
        this.probsForClasses = new HashMap<>();
        this.bagOfWordsPositive = new HashMap<>();
        this.bagOfWordsNegative = new HashMap<>();
        this.allDistinctWords = new HashSet<>();
        this.allPositiveWords = 0;
        this.allNegativeWords = 0;
        this.probPositive = BigDecimal.valueOf(0);
        this.probNegative = BigDecimal.valueOf(0);

        this.truePositive = 0;
        this.falsePositive = 0;
        this.trueNegative = 0;
        this.falseNegative = 0;

        this.precision = 0.0;
        this.recall = 0.0;
        this.f1 = 0.0;
    }

    public void parse() {
        parsePositiveClass();
        parseNegativeClass();
        this.probsForClasses.put("pos", (((double) numPositiveDocs) / numNegativeDocs));
        this.probsForClasses.put("neg", (((double) numNegativeDocs) / numPositiveDocs));
    }


    private void parseClass(int classType) {
        File f;
        File[] fileNames;
        if(classType>0) {
            f = new File("files/train/pos");
            //f = new File("files/mytest/pos");
            fileNames = f.listFiles();
            if(fileNames!=null) {
                this.numPositiveDocs = fileNames.length;
            }
            else {
                throw new RuntimeException("there are no positive files to read");
            }
        }
        else {
            f = new File("files/train/neg");
            //f = new File("files/mytest/neg");
            fileNames = f.listFiles();
            if(fileNames!=null) {
                this.numNegativeDocs = fileNames.length;
            }
            else {
                throw new RuntimeException("there are no negative files to read");
            }
        }

        for (int i = 0; i < fileNames.length; i++) {
            try {
                Scanner s = new Scanner(fileNames[i]);

                String line;
                Scanner lineReader;
                String word;

                while(s.hasNextLine()) {
                    line = s.nextLine();
                    lineReader = new Scanner(line);
                    while(lineReader.hasNext()) {
                        word = lineReader.next();
                        word = word.toLowerCase();

                        this.allDistinctWords.add(word.toLowerCase());
                        if (classType > 0) {
                            allPositiveWords += 1;
                        } else {
                            allNegativeWords += 1;
                        }
                        putInBag(word, classType);

                    }
                }
            }
            catch (FileNotFoundException e) {
                System.out.println("File was not found");
            }
        }

    }


    private void parsePositiveClass() {
        this.parseClass(1);
    }


    private void parseNegativeClass() {
        this.parseClass(-1);
    }


    /**
     * Updates the number of times {@param word} has been seen.
     * It updates a positive bag of words or a negative one depending on
     * whether {@param mode} was positive or negative.
     *
     * @param word the {@code String} whose value needs to be updated
     * @param mode indicates whether the positive or the negative bag of
     *             words needs to be updated
     */
    private void putInBag(String word, int mode) {
        // update positive bag of words
        if(mode>0) {
            Integer currNum = this.bagOfWordsPositive.get(word);
            if(currNum == null) {
                currNum = 0;
            }

            this.bagOfWordsPositive.put(word, (currNum+1));
        }
        // update negative bag of words
        else {
            Integer currNum = this.bagOfWordsNegative.get(word);
            if(currNum == null) {
                currNum = 0;
            }

            this.bagOfWordsNegative.put(word, (currNum+1));
        }
    }


    /**
     * It opens the file named {@param fileName} and calculates the probability
     * that it is of type pos or neg if {@param classType} is a positive or
     * negative number respectively.
     *
     * @param classType if it is a positive number, then the probability of
     *                  {@param fileName} being positive is calculated, otherwise
     *                  the method calculates the probability of {@param fileName}
     *                  being negative
     * @param fileName the files named in this string needs to be classified
     * @param fileLoc this helps for parsing the files - if it is positive, then
     *                the file is located in pos and if it's negative, it is in
     *                neg
     */
    private void calcProb(int classType, String fileName, int fileLoc) {
        File f;
        if(fileLoc>0) {
            f = new File("files/test/pos/" + fileName);
        }
        else {
            f = new File("files/test/neg/" + fileName);
        }
        HashMap<String, Integer> bag;
        BigDecimal prob;
        int distincWordsCount = this.allDistinctWords.size();
        int allWordsInClass;
        try {
            if (classType > 0) {
                prob = BigDecimal.valueOf((double) numPositiveDocs /
                        (numPositiveDocs + numNegativeDocs));
                bag = this.bagOfWordsPositive;
                allWordsInClass = this.allPositiveWords;
            } else {
                prob = BigDecimal.valueOf((double) numNegativeDocs /
                        (numPositiveDocs + numNegativeDocs));
                bag = this.bagOfWordsNegative;
                allWordsInClass = this.allNegativeWords;
            }


            Scanner s = new Scanner(f);
            String nextLine;
            Scanner lineReader;
            String word;
            int occurences;
            while (s.hasNextLine()) {
                nextLine = s.nextLine();
                lineReader = new Scanner(nextLine);
                while (lineReader.hasNext()) {
                    word = lineReader.next();
                    word = word.toLowerCase();

                    if (bag.containsKey(word)) {
                        // times the word occurs in the class
                        occurences = bag.get(word);
                    } else {
                        occurences = 0;
                    }


                    Double immres = (double) (occurences + 1) / (allWordsInClass + distincWordsCount);

                    prob = prob.multiply(BigDecimal.valueOf((double) (immres)));
                    // how many words are there in the class allPositiveWords, allNegativeWords
                    // how many are the total distinct words allDistinctWords.size


                }
            }

            if (classType > 0) {
                this.probPositive = prob;
            } else {
                this.probNegative = prob;
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File was not found");
        }
    }


    public void calcProbPos(String fileName, int loc) {
        calcProb(1, fileName, loc);
    }


    public void calcProbNeg(String fileName, int loc) {
        calcProb(-1, fileName, loc);
    }


    /**
     * This is my main evaluation code in my Naive Bayes Classifier. Based on
     * whether the number {@param classType} is positive or negative it goes
     * through the directory files/test/pos or files/test/neg. It calls the
     * method {@code calcProbPos(String fileName, int location)} or the method
     * {@code calcProbNeg(String fileName, int location)} in order to calculate
     * the probability that a certain files is in the positive or negative class
     * respectively.
     *
     * @param classType indicates whether to analyze the files in
     *                  test/pos or test/neg
     */
    public void analyze(int classType) {
        File f;
        int loc;
        PrintWriter outputFile;

        try {
            if (classType > 0) {
                f = new File("files/test/pos");
                loc = 1;
                outputFile = new PrintWriter("pos_predictions.txt", "UTF-8");
                outputFile.println("Predictions for the files in files/test/pos\n");
            } else {
                f = new File("files/test/neg");
                loc = -1;
                outputFile = new PrintWriter("neg_predictions.txt", "UTF-8");
                outputFile.println("Predictions for the files in files/test/neg\n");
            }

            File[] files = f.listFiles();

            for (int i = 0; i < files.length; i++) {
                // calculate the probability of files[i] being positive
                calcProbPos(files[i].getName(), loc);
                // calculate the probability of files[i] being positive
                calcProbNeg(files[i].getName(), loc);
                if (probPositive.compareTo(probNegative) > 0) {
                    outputFile.println(files[i].getName() + " is evaluated to be pos.");
                    // true positive
                    if (classType > 0) {
                        this.truePositive += 1;
                    }
                    // false positive
                    else {
                        this.falsePositive += 1;
                    }
                } else {
                    outputFile.println(files[i].getName() + " is evaluated to be neg.");
                    // false negative
                    if (classType > 0) {
                        this.falseNegative += 1;
                    }
                    // true negative
                    else {
                        this.trueNegative += 1;
                    }
                }
            }
            outputFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Calculates precision, recall and f1 for pos or neg depending on whether
     * {@param posOrNeg} is a positive or a negative number. It stores them
     * along with tp, fp, tn, and fn in a file named starting with pos or neg
     * depending again on the value of {@param posOrNeg}.
     *
     * @param posOrNeg indicates whether the metrics for pos or neg need to be
     *                 calculated
     */
    public void analyzeCalcMetrics(int posOrNeg) {

        // analyze pos
        analyze(1);
        // analyze neg
        analyze(-1);



        if(posOrNeg>0) {
            this.precision = (double) this.truePositive / (this.truePositive + this.falsePositive);
            this.recall = (double) this.truePositive / (this.truePositive + this.falseNegative);
        }
        else {
            this.precision = (double) this.trueNegative / (this.trueNegative + this.falseNegative);
            this.recall = (double) this.trueNegative / (this.trueNegative + this.falsePositive);
        }

        this.f1 = 2 * this.precision * this.recall /
                (this.precision + this.recall);


        try {
            PrintWriter writer;
            if(posOrNeg>0) {
                writer = new PrintWriter("pos_predictions_data.txt", "UTF-8");
            }
            else {
                writer = new PrintWriter("neg_predictions_data.txt", "UTF-8");
            }
            writer.println("True positive is: " + this.truePositive);
            writer.println("False positive is: " + this.falsePositive);
            writer.println("True negative is: " + this.trueNegative);
            writer.println("False negative is: " + this.falseNegative);
            writer.println("Precision is: " + this.precision);
            writer.println("Recall is: " + this.recall);
            writer.println("F1 is: " + this.f1);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public HashMap<String, Integer> getBagOfWordsPositive() {
        return this.bagOfWordsPositive;
    }


    public HashMap<String, Integer> getBagOfWordsNegative() {
        return this.bagOfWordsNegative;
    }


    public BigDecimal getProbPositive() {
        return this.probPositive;
    }


    public BigDecimal getProbNegative() {
        return this.probNegative;
    }

    public double getPrecision() {
        return this.precision;
    }

    public double getRecall() {
        return this.recall;
    }

    public double getF1() {
        return this.f1;
    }
}
