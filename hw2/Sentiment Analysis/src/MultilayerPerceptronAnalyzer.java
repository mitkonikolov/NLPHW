import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Mitko on 10/29/17.
 *
 * This file was developed by using guidance from
 * https://weka.wikispaces.com/Text+categorization+with+Weka
 * and the files that it links to.
 *
 * I have also read multiple resources in order to learn Weka. Almost all of them are below:
 * http://weka.8497.n7.nabble.com/Is-it-possible-to-create-a-Precision-Recall-Curve-using-this-data-td32414.html
 * https://stackoverflow.com/questions/14189011/weka-src-and-dest-differ-in-of-attributes-using-java
 * http://jmgomezhidalgo.blogspot.com/2013/04/a-simple-text-classifier-in-java-with.html
 * http://weka.8497.n7.nabble.com/Multi-layer-perception-td2896.html
 * https://floatcode.wordpress.com/2015/06/22/perceptron-neural-network-in-java-using-weka-library/
 */
public class MultilayerPerceptronAnalyzer {
    MultilayerPerceptron mperc;
    int numLayers;

    public MultilayerPerceptronAnalyzer(int i) {
        switch(i) {
            case 1:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("1");
                this.numLayers = 1;
                break;
            case 2:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("2");
                this.numLayers = 2;
                break;
            case 3:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("3");
                this.numLayers = 3;
                break;
            case 4:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.1);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(20);
                mperc.setHiddenLayers("4");
                this.numLayers = 4;
                break;
            default:
                mperc = new MultilayerPerceptron();
                mperc.setLearningRate(0.2);
                mperc.setMomentum(0.2);
                mperc.setTrainingTime(40);
                mperc.setHiddenLayers("4");
                this.numLayers = 4;
        }
    }

    public void parse(int classType) {
        File f;
        File[] files;
        if(classType>0) {
            f = new File("files/train/pos");
            files = f.listFiles();
        }
        else {
            f = new File("files/train/neg");
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


            PrintWriter writer = new PrintWriter(this.numLayers +
                    "-layer perceptron eval.txt", "UTF-8");
            writer.println(eval.toSummaryString());


            // Negative
            System.out.println("\nData for Negative Class:");
            writer.println("\nData for Negative Class:");
            System.out.println("Precision: " + eval.precision(0));
            writer.println("Precision: " + eval.precision(0));
            System.out.println("Recall: " + eval.recall(0));
            writer.println("Recall: " + eval.recall(0));
            System.out.println("F1: " + eval.fMeasure(0));
            writer.println("F1: " + eval.fMeasure(0));

            System.out.println("\n");
            writer.println("\n");

            // Positive
            System.out.println("Data for Positive Class:");
            writer.println("Data for Positive Class:");
            System.out.println("Precision: " + eval.precision(1));
            writer.println("Precision: " + eval.precision(1));
            System.out.println("Recall: " + eval.recall(1));
            writer.println("Recall: " + eval.recall(1));
            System.out.println("F1: " + eval.fMeasure(1));
            writer.println("F1: " + eval.fMeasure(1));

            writer.println("\nPredictions on which the data was calculated.");
            FastVector v = eval.predictions();
            for(int i=0; i< v.size(); i++) {
                writer.println(v.elementAt(i));
            }

            writer.close();


            System.out.println("\n\n\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
