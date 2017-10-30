import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Mitko on 10/29/17.
 *
 * This file was developed by using guidance from
 * https://weka.wikispaces.com/Text+categorization+with+Weka
 * and the files that it links to.
 */
public class MultilayerPerceptronAnalyzer {
    MultilayerPerceptron mperc;

    public MultilayerPerceptronAnalyzer(int i) {
        switch(i) {
            case 1:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("1");
                break;
            case 2:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("2");
                break;
            case 3:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("3");
                break;
            case 4:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("4");
                break;
            default:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.2);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(40);
                mperc.setHiddenLayers("4");
        }
    }

    public void parse(int classType) {
        File f;
        File[] files;
        if(classType>0) {
            f = new File("files/train/pos");
            //f = new File("files/mytest/pos");
            files = f.listFiles();
        }
        else {
            f = new File("files/train/neg");
            //f = new File("files/mytest/neg");
            files = f.listFiles();
        }

        File trainingDir = new File("files/train");

        try {
            TextDirectoryLoader loader = new TextDirectoryLoader();
            loader.setDirectory(trainingDir);
            Instances dataRaw = loader.getDataSet();

            StringToWordVector filter = new StringToWordVector();
            filter.setInputFormat(dataRaw);
            Instances dataFiltered = Filter.useFilter(dataRaw, filter);

            mperc.buildClassifier(dataFiltered);

            File testingDir = new File("files/test");
            loader = new TextDirectoryLoader();
            loader.setDirectory(testingDir);
            dataRaw = loader.getDataSet();

            dataFiltered = Filter.useFilter(dataRaw, filter);

            Evaluation eval = new Evaluation(dataFiltered);
            eval.evaluateModel(mperc, dataFiltered);

            // Negative
            System.out.println("Data for Negative Class:");
            System.out.println("Precision: " + eval.precision(0));
            System.out.println("Recall: " + eval.recall(0));
            System.out.println("FMeasure: " + eval.fMeasure(0));

            // Positive
            System.out.println("Data for Positive Class:");
            System.out.println("Precision: " + eval.precision(1));
            System.out.println("Recall: " + eval.recall(1));
            System.out.println("FMeasure: " + eval.fMeasure(1));

            System.out.println("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
