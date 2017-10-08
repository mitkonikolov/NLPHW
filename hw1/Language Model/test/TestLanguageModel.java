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
        lm.parseTrainingSet();
        HashMap<Integer, List<String>> r = lm.getWordsByLine();

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

        this.compareLists(r, expected);
    }

    @Test
    public void testRemExtraChars1() {
        LanguageModel lm = new LanguageModel("train_set1.csv");
        lm.parseTrainingSet();
        lm.remExtraChars();
        HashMap<Integer, List<String>> r = lm.getWordsByLine();

        List<String> line1 = new ArrayList<>();
        line1.add("Okay.");
        line1.add("/");

        List<String> line2 = new ArrayList<>();
        line2.add("So,");

        List<String> line3 = new ArrayList<>();
        line3.add("I");
        line3.add("guess,");

        HashMap<Integer, List<String>> expected = new HashMap<>();
        expected.put(0, line1);
        expected.put(1, line2);
        expected.put(2, line3);

        this.compareLists(r, expected);

    }

    @Test
    public void testRemExtraChars2() {
        LanguageModel lm = new LanguageModel("train_set2.csv");
        lm.parseTrainingSet();
        lm.remExtraChars();
        HashMap<Integer, List<String>> r = lm.getWordsByLine();

        List<String> line1 = new ArrayList<>();
        line1.add("Okay.");
        line1.add("/");

        List<String> line2 = new ArrayList<>();
        line2.add("So,");

        List<String> line3 = new ArrayList<>();
        line3.add("I");
        line3.add("guess,");

        List<String> line4 = new ArrayList<>();
        line4.add("What");
        line4.add("kind");
        line4.add("of");
        line4.add("experience");
        line4.add("do");
        line4.add("you,");
        line4.add("do");
        line4.add("you");
        line4.add("have,");
        line4.add("then");
        line4.add("with");
        line4.add("child");
        line4.add("care?");
        line4.add("/");

        List<String> line5 = new ArrayList<>();
        line5.add("I");
        line5.add("think,");
        line5.add("uh,");
        line5.add("I");
        line5.add("wonder");
        line5.add("if");
        line5.add("that");
        line5.add("worked.");
        line5.add("/");

        List<String> line6 = new ArrayList<>();
        line6.add("Does");
        line6.add("it");
        line6.add("say");
        line6.add("something?");
        line6.add("/");

        List<String> line7 = new ArrayList<>();
        line7.add("I");
        line7.add("think");
        line7.add("it");
        line7.add("usually");
        line7.add("does.");
        line7.add("/");

        List<String> line8 = new ArrayList<>();
        line8.add("You");
        line8.add("might");
        line8.add("try,");
        line8.add("uh,");
        line8.add("/");

        List<String> line9 = new ArrayList<>();
        line9.add("I");
        line9.add("don't");
        line9.add("know,");
        line9.add("/");

        List<String> line10 = new ArrayList<>();
        line10.add("hold");
        line10.add("it");
        line10.add("down");
        line10.add("a");
        line10.add("little");
        line10.add("longer,");
        line10.add("/");

        List<String> line11 = new ArrayList<>();
        line11.add("Okay");
        line11.add(".");
        line11.add("/");

        List<String> line12 = new ArrayList<>();
        line12.add("Well,");

        List<String> line13 = new ArrayList<>();
        line13.add("I");

        List<String> line14 = new ArrayList<>();
        line14.add("and");
        line14.add("of");
        line14.add("course,");
        line14.add("you");
        line14.add("know,");
        line14.add("they");
        line14.add("we");
        line14.add("started,");

        List<String> line15 = new ArrayList<>();
        line15.add("I");
        line15.add("can't");
        line15.add("believe");
        line15.add("I");
        line15.add("was");
        line15.add("brazen");
        line15.add("before.");
        line15.add("/");

        List<String> line16 = new ArrayList<>();
        line16.add("Oh");
        line16.add(".");
        line16.add("/");

        List<String> line17 = new ArrayList<>();
        line17.add(".");

        List<String> line18 = new ArrayList<>();
        line18.add("years");
        line18.add("uh,");
        line18.add("they");
        line18.add("of");
        line18.add("these,");
        line18.add("uh,");
        line18.add("sort");
        line18.add("of");
        line18.add("mandatory");
        line18.add(",");
        line18.add("tests");
        line18.add("if");
        line18.add("you");
        line18.add("take");
        line18.add("you");
        line18.add("get");

        List<String> line19 = new ArrayList<>();
        line19.add("Although,");
        line19.add("um");
        line19.add(".");
        line19.add("/");

        List<String> line20 = new ArrayList<>();
        line20.add("Ju-,");

        List<String> line21 = new ArrayList<>();
        line21.add("She");
        line21.add("has");
        line21.add("just");
        line21.add("three");
        line21.add("!");
        line21.add("kids,");
        line21.add("eleven,");
        line21.add("nine,");
        line21.add("and");
        line21.add("eight.");
        line21.add("/");

        List<String> line22 = new ArrayList<>();
        line22.add("and");
        line22.add("we");
        line22.add("look");
        line22.add("wh-,");
        line22.add("we");
        line22.add("have");
        line22.add("anything.");
        line22.add("/");

        List<String> line23 = new ArrayList<>();
        line23.add("Deductibles");
        line23.add(",");
        line23.add("are");
        line23.add("high,");
        line23.add("/");

        List<String> line24 = new ArrayList<>();
        line24.add("when");
        line24.add("John");
        line24.add("Stallworth");
        line24.add("played");
        line24.add("/");

        List<String> line25 = new ArrayList<>();
        line25.add("That's");
        line25.add("pretty");
        line25.add("nice");
        line25.add(".");
        line25.add("/");

        List<String> line26 = new ArrayList<>();
        line26.add("Yeah,");
        line26.add("/");

        List<String> line27 = new ArrayList<>();
        line27.add("is");
        line27.add("close");
        line27.add("/");

        List<String> line28 = new ArrayList<>();
        line28.add("and");
        line28.add("Allen");
        line28.add(",");
        line28.add("/");

        List<String> line29 = new ArrayList<>();
        line29.add("get");
        line29.add("things");
        line29.add("from");
        line29.add("T");
        line29.add("V");
        line29.add("or");
        line29.add(".");
        line29.add("/");

        List<String> line30 = new ArrayList<>();
        line30.add("I");
        line30.add("am");
        line30.add("fairly");
        line30.add("knowledgeable");
        line30.add("of");
        line30.add(".");

        List<String> line31 = new ArrayList<>();
        line31.add("Uh,");
        line31.add("guy");
        line31.add(",");
        line31.add("/");

        List<String> line32 = new ArrayList<>();
        line32.add("Whi-,");
        line32.add(".");

        HashMap<Integer, List<String>> expected = new HashMap<>();
        expected.put(0, line1);
        expected.put(1, line2);
        expected.put(2, line3);
        expected.put(3, line4);
        expected.put(4, line5);
        expected.put(5, line6);
        expected.put(6, line7);
        expected.put(7, line8);
        expected.put(8, line9);
        expected.put(9, line10);
        expected.put(10, line11);
        expected.put(11, line12);
        expected.put(12, line13);
        expected.put(13, line14);
        expected.put(14, line15);
        expected.put(15, line16);
        expected.put(16, line17);
        expected.put(17, line18);
        expected.put(18, line19);
        expected.put(19, line20);
        expected.put(20, line21);
        expected.put(21, line22);
        expected.put(22, line23);
        expected.put(23, line24);
        expected.put(24, line25);
        expected.put(25, line26);
        expected.put(26, line27);
        expected.put(27, line28);
        expected.put(28, line29);
        expected.put(29, line30);
        expected.put(30, line31);
        expected.put(31, line32);

        this.compareLists(r, expected);

    }

    private void compareLists(HashMap<Integer, List<String>> r,
                              HashMap<Integer, List<String>> expected) {
        for(int i = 0; i<r.size(); i++) {
            List<String> actualLine = r.get(i);
            List<String> expectedLine = expected.get(i);

            assertEquals("Length of list on line " + i+1,
                    expectedLine.size(), actualLine.size());

            for(int p = 0; p<actualLine.size(); p++) {
                System.out.println(actualLine.get(p));
                assertEquals(expectedLine.get(p), actualLine.get(p));
            }
        }
    }

}
