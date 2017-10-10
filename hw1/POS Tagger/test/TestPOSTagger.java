import org.junit.Test;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Mitko on 10/10/17.
 */
public class TestPOSTagger {

    @Test
    public void testParsing() {
        POSTagger ptagger = new POSTagger("train2.counts");

        ptagger.parseFile();

        HashMap<String, HashMap<String, Integer>> wordTags = ptagger.getWordTagCount();
        HashMap<String, Integer> unigramCount = ptagger.getUnigramCount();
        HashMap<String, HashMap<String, Integer>> bigramCount = ptagger.getBigramTagCount();
        HashMap<String, HashMap<String, HashMap<String, Integer>>> trigramCount = ptagger.getTrigramCount();

        //printWordTagCount(wordTags);
        //printUnigramCount(unigramCount);
        //printWordTagCount(bigramCount);
        printTrigramCount(trigramCount);

    }


    private void printWordTagCount(HashMap<String, HashMap<String, Integer>> wordTags) {
        Set<String> words = wordTags.keySet();

        Iterator<String> wordsIter = words.iterator();

        while(wordsIter.hasNext()) {
            String word = wordsIter.next();
            System.out.println(word);

            HashMap<String, Integer> tags = wordTags.get(word);

            Set<String> allTags = tags.keySet();
            Iterator<String> tagIter = allTags.iterator();

            while(tagIter.hasNext()) {
                String t = tagIter.next();
                System.out.println(t);

                System.out.println(tags.get(t));
            }
        }
    }



    private void printUnigramCount(HashMap<String, Integer> unigramCount) {
        Set<String> tags = unigramCount.keySet();

        Iterator<String> tagsIter = tags.iterator();

        while(tagsIter.hasNext()) {
            String tag = tagsIter.next();

            System.out.println(tag);
            System.out.println(unigramCount.get(tag));
        }
    }


    private void printTrigramCount(HashMap<String, HashMap<String, HashMap<String, Integer>>> trigramCount) {

        Set<String> tags = trigramCount.keySet();
        Iterator<String> tagsIter = tags.iterator();
        String tag;

        while(tagsIter.hasNext()) {
            tag = tagsIter.next();
            System.out.println("Outer tag: " + tag);

            HashMap<String, HashMap<String, Integer>> tagsAfter = trigramCount.get(tag);
            Set<String> middleTags = tagsAfter.keySet();
            Iterator<String> middleTagsIter = middleTags.iterator();
            String middleTag;

            while(middleTagsIter.hasNext()) {
                middleTag = middleTagsIter.next();

                System.out.println("Middle tag: " + middleTag);

                HashMap<String, Integer> tagsEnd = tagsAfter.get(middleTag);
                Set<String> innerTags = tagsEnd.keySet();
                Iterator<String> innerTagsIter = innerTags.iterator();
                String innerTag;

                while(innerTagsIter.hasNext()) {
                    innerTag = innerTagsIter.next();

                    System.out.println("Inner tag " + innerTag + " occurs " + tagsEnd.get(innerTag));
                }

            }

            System.out.println("\n\n");
        }

    }
}
