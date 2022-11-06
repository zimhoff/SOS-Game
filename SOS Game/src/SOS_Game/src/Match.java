package SOS_Game.src;

public class Match implements Comparable<Match> {
    public final Pair first;
    public final Pair second;
    public final Pair third;

    public Match(Pair first, Pair second, Pair third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public String toString() {
        return String.format("[%s, %s, %s]", this.first, this.second, this.third);
    }

    @Override
    public int compareTo(Match other) {
        if (this.first.compareTo(other.first) == 0 && this.second.compareTo(other.second) == 0 && this.third.compareTo(other.third) == 0) {
            return 0;
        } else {
            return -1;
        }
    }
}