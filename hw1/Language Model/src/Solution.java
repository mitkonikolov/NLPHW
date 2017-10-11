import java.util.Scanner;

/**
 * Created by Mitko on 10/11/17.
 */
public class Solution {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the number of the questions you want to test.");
        System.out.println("1. perplexity for add 1 smoothing (takes about 10 seconds)");
        System.out.println("2. perplexity for interpolation (takes about 10 seconds)");
        System.out.println("3. generating sentences");

        int choice = s.nextInt();

        LanguageModel lm;
        String testSet = "test_set.csv";

        switch(choice) {
            case 1:
                lm = new LanguageModel("train_set.csv");

                lm.parseTrainingSet();
                lm.remExtraChars();
                lm.finishProcessingWords();
                lm.learn();
                lm.add1Smoothing();
                lm.calculateProbabilitiesAdd1();

                System.out.println("Perplexity for " + testSet + ": " + lm.calculatePerplexity(testSet));
                break;
            case 2:
                lm = new LanguageModel("train_set.csv");

                lm.parseTrainingSet();
                lm.remExtraChars();
                lm.finishProcessingWords();
                lm.learn();
                lm.calculateProbabilitiesWithInterpolation();

                System.out.println("Perplexity for " + testSet + ": " + lm.calculatePerplexity(testSet));
                break;
            case 3:
                lm = new LanguageModel("train_set.csv");

                lm.parseTrainingSet();
                lm.remExtraChars();
                lm.finishProcessingWords();
                lm.learn();
                lm.calculateProbabilitiesWithInterpolation();

                lm.printNSentences(20);
                break;
            default:
                System.out.println("Please enter a number between 1 and 3");
        }

    }
}
