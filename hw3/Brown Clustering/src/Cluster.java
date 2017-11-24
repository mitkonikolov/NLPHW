import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mitko on 11/19/17.
 */
public class Cluster {
    private int id;
    private Set<String> words;
    private int numOccurences;
    private final int totNumWords;
    private double p_c;


    public Cluster(int id) {
        this.id = id;
        this.words = new HashSet<>();
        this.p_c = 0.0;
        this.totNumWords = 0;
    }

    public Cluster(int id, String word, int numOccurences, int totNumWords) {
        this.id = id;
        this.words = new HashSet<>();
        this.words.add(word);
        this.numOccurences = numOccurences;
        this.totNumWords = totNumWords;
        this.updateP_c();
    }

    public Cluster(int id, Set<String> words, int numOccurences, int totNumWords) {
        this.id = id;
        this.words = words;
        this.numOccurences = numOccurences;
        this.totNumWords = totNumWords;
        this.updateP_c();
    }

    public int getId() {
        return this.id;
    }


    public Set<String> getWords() {
        return this.words;
    }


    public double getP_c() {
        return this.p_c;
    }


    public void mergeClusters(Cluster after) {
        this.words.addAll(after.getWords());
        this.numOccurences += after.numOccurences;
        updateP_c();
    }


    private void updateP_c() {
        this.p_c = ((double) this.numOccurences) / ((double) this.totNumWords);
    }


    /**
     * Checks what is the quality for this cluster and {@param after}
     * @param after
     * @param N_c1_c2
     * @param N_c1
     * @return
     */
    public double checkQuality(Cluster after, int N_c1_c2, int N_c1) {
        double p_c1_c2 = ((double) N_c1_c2) / ((double)N_c1);

        double result = p_c1_c2 * (Math.log((p_c1_c2) / (this.p_c * after.p_c)));

        if(Double.isNaN(result)) {
            result = 0.1E-100;
        }

        return result;
    }


    /**
     * It merges {@param left} and {@param right} to create a new cluster with the
     * id of {@param left}.
     * @param left
     * @param right
     * @return
     */
    public static Cluster mergeTwoClusters(Cluster left, Cluster right) {
        Set<String> newWords = new HashSet<>();

        newWords.addAll(left.getWords());
        newWords.addAll(right.getWords());

        int newNumOccurences = left.numOccurences + right.numOccurences;
        int newTotNumWords = left.totNumWords;


        return new Cluster(left.id, newWords, newNumOccurences, newTotNumWords);
    }


    double calculateQuality(Cluster c2) {

        return 0;
    }
}
