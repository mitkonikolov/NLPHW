import org.junit.Test;

/**
 * Created by Mitko on 10/29/17.
 */
public class testPerceptron {

    @Test
    public void testPerceptron() {
        MultilayerPerceptronAnalyzer perceptron = new MultilayerPerceptronAnalyzer(1);
        System.out.println("Perceptron with 1 hidden layer");
        perceptron.parse(1);
    }

    @Test
    public void testPerceptron2() {
        MultilayerPerceptronAnalyzer perceptron = new MultilayerPerceptronAnalyzer(2);
        System.out.println("Perceptron with 2 hidden layers");
        perceptron.parse(1);
    }

    @Test
    public void testPerceptron3() {
        MultilayerPerceptronAnalyzer perceptron = new MultilayerPerceptronAnalyzer(3);
        System.out.println("Perceptron with 3 hidden layers");
        perceptron.parse(1);
    }

    @Test
    public void testPerceptron4() {
        MultilayerPerceptronAnalyzer perceptron = new MultilayerPerceptronAnalyzer(4);
        System.out.println("Perceptron with 4 hidden layers");
        perceptron.parse(1);
    }

    @Test
    public void testPerceptron5() {
        MultilayerPerceptronAnalyzer perceptron = new MultilayerPerceptronAnalyzer(5);
        System.out.println("Perceptron with 5 hidden layers");
        perceptron.parse(1);
    }


}
