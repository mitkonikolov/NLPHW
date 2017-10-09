import org.junit.Test;
import org.junit.Assert;

import java.util.*;

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

        List<String> line5 = new ArrayList<>();
        line5.add("I");
        line5.add("think,");
        line5.add("uh,");
        line5.add("I");
        line5.add("wonder");
        line5.add("if");
        line5.add("that");
        line5.add("worked.");

        List<String> line6 = new ArrayList<>();
        line6.add("Does");
        line6.add("it");
        line6.add("say");
        line6.add("something?");

        List<String> line7 = new ArrayList<>();
        line7.add("I");
        line7.add("think");
        line7.add("it");
        line7.add("usually");
        line7.add("does.");

        List<String> line8 = new ArrayList<>();
        line8.add("You");
        line8.add("might");
        line8.add("try,");
        line8.add("uh,");

        List<String> line9 = new ArrayList<>();
        line9.add("I");
        line9.add("don't");
        line9.add("know,");

        List<String> line10 = new ArrayList<>();
        line10.add("hold");
        line10.add("it");
        line10.add("down");
        line10.add("a");
        line10.add("little");
        line10.add("longer,");

        List<String> line11 = new ArrayList<>();
        line11.add("Okay");
        line11.add(".");

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

        List<String> line16 = new ArrayList<>();
        line16.add("Oh");
        line16.add(".");

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

        List<String> line22 = new ArrayList<>();
        line22.add("and");
        line22.add("we");
        line22.add("look");
        line22.add("wh-,");
        line22.add("we");
        line22.add("have");
        line22.add("anything.");

        List<String> line23 = new ArrayList<>();
        line23.add("Deductibles");
        line23.add(",");
        line23.add("are");
        line23.add("high,");

        List<String> line24 = new ArrayList<>();
        line24.add("when");
        line24.add("John");
        line24.add("Stallworth");
        line24.add("played");

        List<String> line25 = new ArrayList<>();
        line25.add("That's");
        line25.add("pretty");
        line25.add("nice");
        line25.add(".");

        List<String> line26 = new ArrayList<>();
        line26.add("Yeah,");

        List<String> line27 = new ArrayList<>();
        line27.add("is");
        line27.add("close");

        List<String> line28 = new ArrayList<>();
        line28.add("and");
        line28.add("Allen");
        line28.add(",");

        List<String> line29 = new ArrayList<>();
        line29.add("get");
        line29.add("things");
        line29.add("from");
        line29.add("T");
        line29.add("V");
        line29.add("or");
        line29.add(".");

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

    @Test
    public void testRemExtraChars3() {
        LanguageModel lm = new LanguageModel("train_set6.csv");
        lm.parseTrainingSet();
        lm.remExtraChars();
        HashMap<Integer, List<String>> r = lm.getWordsByLine();

        List<String> line1 = new ArrayList<>();
        line1.add("I");

        HashMap<Integer, List<String>> expected = new HashMap<>();

        expected.put(0, line1);

        this.compareLists(r, expected);
    }


    @Test
    public void testFinishProcessingWords1() {
        LanguageModel lm = new LanguageModel("train_set1.csv");

        lm.parseTrainingSet();
        lm.remExtraChars();
        lm.finishProcessingWords();

        HashMap<Integer, List<String>> r = lm.getWordsByLine();

        List<String> line1 = new ArrayList<>();
        line1.add("<s><s>");
        line1.add("okay");
        line1.add(".");
        line1.add("</s>");

        List<String> line2 = new ArrayList<>();
        line2.add("<s><s>");
        line2.add("so");
        line2.add(",");
        line2.add("</s>");

        List<String> line3 = new ArrayList<>();
        line3.add("<s><s>");
        line3.add("i");
        line3.add("guess");
        line3.add(",");
        line3.add("</s>");

        HashMap<Integer, List<String>> expected = new HashMap<>();
        expected.put(0, line1);
        expected.put(1, line2);
        expected.put(2, line3);

        this.compareLists(r, expected);

    }


    @Test
    public void testFinishProcessingWords2() {
        LanguageModel lm = new LanguageModel("train_set3.csv");

        lm.parseTrainingSet();
        lm.remExtraChars();
        lm.finishProcessingWords();

        HashMap<Integer, List<String>> r = lm.getWordsByLine();

        List<String> line1 = new ArrayList<>();
        line1.add("<s><s>");
        line1.add("okay");
        line1.add(".");
        line1.add("</s>");

        List<String> line2 = new ArrayList<>();
        line2.add("<s><s>");
        line2.add("so");
        line2.add(",");
        line2.add("</s>");

        List<String> line3 = new ArrayList<>();
        line3.add("<s><s>");
        line3.add("i");
        line3.add("guess");
        line3.add(",");
        line3.add("</s>");

        List<String> line4 = new ArrayList<>();
        line4.add("<s><s>");
        line4.add("what");
        line4.add("kind");
        line4.add("of");
        line4.add("experience");
        line4.add("do");
        line4.add("you");
        line4.add(",");
        line4.add("do");
        line4.add("you");
        line4.add("have");
        line4.add(",");
        line4.add("then");
        line4.add("with");
        line4.add("child");
        line4.add("care");
        line4.add("?");
        line4.add("</s>");

        List<String> line5 = new ArrayList<>();
        line5.add("<s><s>");
        line5.add("i");
        line5.add("think");
        line5.add(",");
        line5.add("uh");
        line5.add(",");
        line5.add("i");
        line5.add("wonder");
        line5.add("if");
        line5.add("that");
        line5.add("worked");
        line5.add(".");
        line5.add("</s>");

        List<String> line6 = new ArrayList<>();
        line6.add("<s><s>");
        line6.add("does");
        line6.add("it");
        line6.add("say");
        line6.add("something");
        line6.add("?");
        line6.add("</s>");

        List<String> line7 = new ArrayList<>();
        line7.add("<s><s>");
        line7.add("i");
        line7.add("think");
        line7.add("it");
        line7.add("usually");
        line7.add("does");
        line7.add(".");
        line7.add("</s>");

        List<String> line8 = new ArrayList<>();
        line8.add("<s><s>");
        line8.add("you");
        line8.add("might");
        line8.add("try");
        line8.add(",");
        line8.add("uh");
        line8.add(",");
        line8.add("</s>");

        List<String> line9 = new ArrayList<>();
        line9.add("<s><s>");
        line9.add("i");
        line9.add("don");
        line9.add("'");
        line9.add("t");
        line9.add("know");
        line9.add(",");
        line9.add("</s>");

        List<String> line10 = new ArrayList<>();
        line10.add("<s><s>");
        line10.add("hold");
        line10.add("it");
        line10.add("down");
        line10.add("a");
        line10.add("little");
        line10.add("longer");
        line10.add(",");
        line10.add("</s>");

        List<String> line11 = new ArrayList<>();
        line11.add("<s><s>");
        line11.add("okay");
        line11.add(".");
        line11.add("</s>");

        List<String> line12 = new ArrayList<>();
        line12.add("<s><s>");
        line12.add("well");
        line12.add(",");
        line12.add("</s>");

        List<String> line13 = new ArrayList<>();
        line13.add("<s><s>");
        line13.add("i");
        line13.add("</s>");

        List<String> line14 = new ArrayList<>();
        line14.add("<s><s>");
        line14.add("and");
        line14.add("of");
        line14.add("course");
        line14.add(",");
        line14.add("you");
        line14.add("know");
        line14.add(",");
        line14.add("they");
        line14.add("we");
        line14.add("started");
        line14.add(",");
        line14.add("</s>");

        List<String> line15 = new ArrayList<>();
        line15.add("<s><s>");
        line15.add("i");
        line15.add("can");
        line15.add("'");
        line15.add("t");
        line15.add("believe");
        line15.add("i");
        line15.add("was");
        line15.add("brazen");
        line15.add("before");
        line15.add(".");
        line15.add("</s>");

        List<String> line16 = new ArrayList<>();
        line16.add("<s><s>");
        line16.add("oh");
        line16.add(".");
        line16.add("</s>");

        List<String> line17 = new ArrayList<>();
        line17.add("<s><s>");
        line17.add(".");
        line17.add("</s>");

        List<String> line18 = new ArrayList<>();
        line18.add("<s><s>");
        line18.add("years");
        line18.add("uh");
        line18.add(",");
        line18.add("they");
        line18.add("of");
        line18.add("these");
        line18.add(",");
        line18.add("uh");
        line18.add(",");
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
        line18.add("</s>");

        List<String> line19 = new ArrayList<>();
        line19.add("<s><s>");
        line19.add("although");
        line19.add(",");
        line19.add("um");
        line19.add(".");
        line19.add("</s>");

        List<String> line20 = new ArrayList<>();
        line20.add("<s><s>");
        line20.add("ju-");
        line20.add(",");
        line20.add("</s>");

        List<String> line21 = new ArrayList<>();
        line21.add("<s><s>");
        line21.add("she");
        line21.add("has");
        line21.add("just");
        line21.add("three");
        line21.add("!");
        line21.add("kids");
        line21.add(",");
        line21.add("eleven");
        line21.add(",");
        line21.add("nine");
        line21.add(",");
        line21.add("and");
        line21.add("eight");
        line21.add(".");
        line21.add("</s>");

        List<String> line22 = new ArrayList<>();
        line22.add("<s><s>");
        line22.add("and");
        line22.add("we");
        line22.add("look");
        line22.add("wh-");
        line22.add(",");
        line22.add("we");
        line22.add("have");
        line22.add("anything");
        line22.add(".");
        line22.add("</s>");

        List<String> line23 = new ArrayList<>();
        line23.add("<s><s>");
        line23.add("deductibles");
        line23.add(",");
        line23.add("are");
        line23.add("high");
        line23.add(",");
        line23.add("</s>");

        List<String> line24 = new ArrayList<>();
        line24.add("<s><s>");
        line24.add("when");
        line24.add("john");
        line24.add("stallworth");
        line24.add("played");
        line24.add("</s>");

        List<String> line25 = new ArrayList<>();
        line25.add("<s><s>");
        line25.add("that");
        line25.add("'");
        line25.add("s");
        line25.add("pretty");
        line25.add("nice");
        line25.add(".");
        line25.add("</s>");

        List<String> line26 = new ArrayList<>();
        line26.add("<s><s>");
        line26.add("yeah");
        line26.add(",");
        line26.add("</s>");

        List<String> line27 = new ArrayList<>();
        line27.add("<s><s>");
        line27.add("is");
        line27.add("close");
        line27.add("</s>");

        List<String> line28 = new ArrayList<>();
        line28.add("<s><s>");
        line28.add("and");
        line28.add("allen");
        line28.add(",");
        line28.add("</s>");

        List<String> line29 = new ArrayList<>();
        line29.add("<s><s>");
        line29.add("get");
        line29.add("things");
        line29.add("from");
        line29.add("t");
        line29.add("v");
        line29.add("or");
        line29.add(".");
        line29.add("</s>");

        List<String> line30 = new ArrayList<>();
        line30.add("<s><s>");
        line30.add("i");
        line30.add("am");
        line30.add("fairly");
        line30.add("knowledgeable");
        line30.add("of");
        line30.add(".");
        line30.add("</s>");

        List<String> line31 = new ArrayList<>();
        line31.add("<s><s>");
        line31.add("uh");
        line31.add(",");
        line31.add("guy");
        line31.add(",");
        line31.add("</s>");

        List<String> line32 = new ArrayList<>();
        line32.add("<s><s>");
        line32.add("whi-");
        line32.add(",");
        line32.add(".");
        line32.add("</s>");

        List<String> line33 = new ArrayList<>();
        line33.add("<s><s>");
        line33.add("i");
        line33.add(",");
        line33.add("guess");
        line33.add(",");
        line33.add("</s>");

        List<String> line34 = new ArrayList<>();
        line34.add("<s><s>");
        line34.add("i");
        line34.add(",");
        line34.add("don");
        line34.add("'");
        line34.add("t");
        line34.add("!");
        line34.add("</s>");

        List<String> line35 = new ArrayList<>();
        line35.add("<s><s>");
        line35.add("that");
        line35.add("'");
        line35.add("s");
        line35.add(",");
        line35.add("uh");
        line35.add(",");
        line35.add("that");
        line35.add("'");
        line35.add("s");
        line35.add("a");
        line35.add("little");
        line35.add("bit");
        line35.add("to");
        line35.add(",");
        line35.add("uh");
        line35.add(",");
        line35.add("</s>");

        List<String> line36 = new ArrayList<>();
        line36.add("<s><s>");
        line36.add("it");
        line36.add("'");
        line36.add("s");
        line36.add("such");
        line36.add("</s>");

        List<String> line37 = new ArrayList<>();
        line37.add("<s><s>");
        line37.add("you");
        line37.add("'");
        line37.add("re");
        line37.add(",");
        line37.add("you");
        line37.add("'");
        line37.add("re");
        line37.add("about");
        line37.add("like");
        line37.add("i");
        line37.add("am");
        line37.add("then");
        line37.add(".");
        line37.add("</s>");

        List<String> line38 = new ArrayList<>();
        line38.add("<s><s>");
        line38.add(".");
        line38.add("</s>");

        List<String> line39 = new ArrayList<>();
        line39.add("<s><s>");
        line39.add("and");
        line39.add("nowhere");
        line39.add("does");
        line39.add(".");
        line39.add("</s>");

        List<String> line40 = new ArrayList<>();
        line40.add("<s><s>");
        line40.add("well");
        line40.add(",");
        line40.add("i");
        line40.add("haven");
        line40.add("'");
        line40.add("t");
        line40.add("tried");
        line40.add(",");
        line40.add("</s>");

        List<String> line41 = new ArrayList<>();
        line41.add("<s><s>");
        line41.add("oh");
        line41.add(",");
        line41.add("i");
        line41.add("like");
        line41.add("it");
        line41.add(".");
        line41.add("</s>");

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
        expected.put(32, line33);
        expected.put(33, line34);
        expected.put(34, line35);
        expected.put(35, line36);
        expected.put(36, line37);
        expected.put(37, line38);
        expected.put(38, line39);
        expected.put(39, line40);
        expected.put(40, line41);

        this.compareLists(r, expected);

    }


    private void compareLists(HashMap<Integer, List<String>> r,
                              HashMap<Integer, List<String>> expected) {
        for(int i = 0; i<r.size(); i++) {
            List<String> actualLine = r.get(i);
            List<String> expectedLine = expected.get(i);

            assertEquals("Length of list on line " + (i+1),
                    expectedLine.size(), actualLine.size());

            for(int p = 0; p<actualLine.size(); p++) {
                System.out.println(actualLine.get(p));
                assertEquals(expectedLine.get(p), actualLine.get(p));
            }
        }
    }


    @Test
    public void testLearnIndCount() {
        LanguageModel lm = new LanguageModel("train_set4.csv");

        lm.parseTrainingSet();
        lm.remExtraChars();
        lm.finishProcessingWords();
        lm.learn();

        HashMap<String, Integer> indCount = lm.getIndividualCounts();

        HashMap<String, Integer> indCountExpected = new HashMap<>();

        indCountExpected.put("<s><s>", 3);
        indCountExpected.put("okay", 1);
        indCountExpected.put(".", 1);
        indCountExpected.put("so", 1);
        indCountExpected.put(",", 2);
        indCountExpected.put("i", 1);
        indCountExpected.put("guess", 1);
        indCountExpected.put("</s>", 3);

        compareIndCounts(indCountExpected, indCount);
    }

    private void compareIndCounts(HashMap<String, Integer> expected,
                                  HashMap<String, Integer> actual) {
        assertEquals("size of individual count maps",
                actual.size(), expected.size());

        Iterator iteratorActual = actual.entrySet().iterator();
        Iterator iteratorExpected = expected.entrySet().iterator();

        while(iteratorActual.hasNext()) {
            String expectedWord = iteratorExpected.next().toString();
            String actualWord = iteratorActual.next().toString();

            assertEquals(expectedWord, actualWord);
        }
    }

    @Test
    public void testLearningAllWords() {
        LanguageModel lm = new LanguageModel("train_set.csv");

        lm.parseTrainingSet();
        lm.remExtraChars();
        lm.finishProcessingWords();
        lm.learn();

        showTrigrams(lm.getTrigramCounts());
    }


    private void showTrigrams(HashMap<String, HashMap<String, Integer>> trigramCounts) {
        Iterator wordsBeforeIter = trigramCounts.entrySet().iterator();

        String expression;

        while(wordsBeforeIter.hasNext()) {
            expression = wordsBeforeIter.next().toString();

            System.out.println(expression);
        }
    }


    @Test
    public void testAdd1Smoothing() {
        LanguageModel lm = new LanguageModel("train_set7.csv");

        lm.parseTrainingSet();
        lm.remExtraChars();
        lm.finishProcessingWords();
        lm.learn();

        HashMap<String, HashMap<String, Integer>> trigramsBeforeSmoothing;
        trigramsBeforeSmoothing = lm.getTrigramCounts();

        showTrigrams(trigramsBeforeSmoothing);

        System.out.println();
        System.out.println();
        System.out.println();

        lm.add1Smoothing();

        HashMap<String, HashMap<String, Integer>> trigramsAfterSmoothing;
        trigramsAfterSmoothing = lm.getTrigramCounts();

        showTrigrams(trigramsAfterSmoothing);
    }

/*    private void compareFirst100Trigrams(HashMap<String, HashMap<String, Integer>> before,
                                         HashMap<String, HashMap<String, Integer>> after) {
        // all two words coming before (bigrams)
        Set<String> keysBefore = before.keySet();

        // iterators to iterate over the keys on the outer map
        Iterator keysIter = keysBefore.iterator();

        String key;

        String innerKey;

        for(int i=0; (i<1) && keysIter.hasNext(); i++) {
            key = keysIter.next().toString();

            HashMap<String, Integer> wordsAfterNoSmoothing = before.get(key);
            HashMap<String, Integer> wordsAfterWithSmoothing = after.get(key);

            // all words coming after the two words (unigrams)
            Set<String> words = wordsAfterNoSmoothing.keySet();

            // iterator to iterate over the string key of the inner map
            Iterator innerKeysIter = words.iterator();

            // check all numbers for the strings in the inner map
            while(innerKeysIter.hasNext()) {
                innerKey = innerKeysIter.next().toString();

                int numberBeforeSmoothing = wordsAfterNoSmoothing.get(innerKey);
                int numberAfterSmoothing = wordsAfterWithSmoothing.get(innerKey);

                numberBeforeSmoothing += 1;

                System.out.println(numberBeforeSmoothing);
                System.out.println(numberAfterSmoothing);

                System.out.println(key);

                assertEquals(key,numberBeforeSmoothing, numberAfterSmoothing);
            }

        }
    }*/




}
