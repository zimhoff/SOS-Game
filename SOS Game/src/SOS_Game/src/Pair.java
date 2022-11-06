package SOS_Game.src;
/**
 * Holds a coordinate pair.
 *
 * @see Triplet
 */
public class Pair implements Comparable<Pair> {
    public final int first;
    public final int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public String toString() {
        return String.format("(%s, %s)", this.first, this.second);
    }

    @Override
    public int compareTo(Pair other) {
        if (this.first == other.first && this.second == other.second) {
            return 0;
        } else {
            return -1;
        }
    }
}