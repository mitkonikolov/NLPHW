import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Mitko on 10/3/17.
 */
public class TestLanguageModel {

    // Can the Language Model split the words
    @Test
    public void testBasicParsing() {
        LanguageModel lm = new LanguageModel("train_set1.csv");
        HashMap<Integer, List<String>> r =  lm.parseTrainingSet();

        List<String> line1 = new ArrayList<>();
        line1.add("Okay.");
        line1.add("/");

        List<String> line2 = new ArrayList<>();
        line2.add("{D");
        line2.add("So,");
        line2.add("}");

        List<String> line3 = new ArrayList<>();
        line3.add("[");
        line3.add("[");
        line3.add("I");
        line3.add("guess,");
        line3.add("+");

        HashMap<Integer, List<String>> expected = new HashMap<>();
        expected.put(0, line1);
        expected.put(1, line2);
        expected.put(2, line3);

        for(int i = 0; i<r.size(); i++) {
            List<String> actualLine = r.get(i);
            List<String> expectedLine = expected.get(i);

            assertEquals("Lists' length",
                    expectedLine.size(), actualLine.size());

            for(int p = 0; p<actualLine.size(); p++) {
                assertEquals(actualLine.get(p), expectedLine.get(p));
            }
        }
    }

}
