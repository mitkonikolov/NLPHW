import org.junit.Test;

/**
 * Created by Mitko on 10/3/17.
 */
public class TestLanguageModel {

    @Test
    public void testParsing() {
        LanguageModel lm = new LanguageModel();
        lm.parseTrainingSet();
    }

}
