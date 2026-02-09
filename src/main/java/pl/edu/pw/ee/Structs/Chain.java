package pl.edu.pw.ee.Structs;

public class Chain implements Comparable<Chain> {
    public final byte[] bytes;
    public int amount;

    public Chain(byte[] bytes, int amount) {
        this.bytes = bytes;
        this.amount = amount;
    }


    @Override
    public int compareTo(Chain o) {
        return this.amount - o.amount;
    }

}
