import java.util.Objects;

/**
 * Created by Mitko on 11/15/17.
 */
public class Tuple implements Comparable<Tuple> {
    private String word;
    private int numOccurences;


    public Tuple(String word) {
        this.word = word;
        this.numOccurences = 1;
    }

    public Tuple(String word, int numOccurences) {
        this.word = word;
        this.numOccurences = numOccurences;
    }


    public void incrementOccurences() {
        this.numOccurences += 1;
    }


    @Override
    /**
     * The method is written so that the sorting will be done in descending
     * order.
     */
    public int compareTo(Tuple o) {

        if(this.numOccurences > o.numOccurences) {
            return -1;
        }
        else if(this.numOccurences == o.numOccurences) {
            return this.word.compareTo(o.word);
        }
        else {
            return 1;
        }
    }


    public String getWord() {
        return this.word;
    }


    public int getNumOccurences() {
        return this.numOccurences;
    }


    @Override
    public boolean equals(Object o) {
        if( !(o instanceof Tuple)) {
            return false;
        }

        Tuple t2 = (Tuple) o;

        return this.word.equals(t2.word);
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.word);
    }
}
