package pl.edu.pw.ee.Algorithms;

import pl.edu.pw.ee.Structs.Chain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChainsFinder {

    private final List<Chain> chains = new ArrayList<>();

    public void findChains(byte[] data, int maxChainLength) {
        chains.clear();

        if (data == null || data.length == 0) return;

        List<byte[]> dictionary = new ArrayList<>();

        // 1. Szukamy długich łańcuchów (od max do 2)
        if (maxChainLength >= 2) {
            for (int len = maxChainLength; len >= 2; len--) {
                for (int i = 0; i <= data.length - len; i++) {
                    byte[] sub = Arrays.copyOfRange(data, i, i + len);

                    if (contains(dictionary, sub)) continue;

                    // KLUCZ: Liczymy wystąpienia, które nie nachodzą na siebie
                    int count = countNonOverlapping(data, sub);

                    if (count > 1) {
                        dictionary.add(sub);
                        chains.add(new Chain(sub, count));
                    }
                }
            }
        }

        // 2. Dodajemy pojedyncze bajty, ale TYLKO te, które są w tekście
        // i zliczamy ich faktyczną częstotliwość
        addSingleBytes(data, dictionary);

        // 3. Sortujemy: najpierw najdłuższe, potem najczęstsze
        chains.sort((a, b) -> {
            int lenCmp = Integer.compare(b.bytes.length, a.bytes.length);
            if (lenCmp != 0) return lenCmp;
            return Integer.compare(b.amount, a.amount);
        });
    }

    private int countNonOverlapping(byte[] data, byte[] sub) {
        int count = 0;
        int i = 0;
        while (i <= data.length - sub.length) {
            if (matchesAt(data, sub, i)) {
                count++;
                i += sub.length; // Przeskakujemy o całą długość wzorca!
            } else {
                i++;
            }
        }
        return count;
    }

    private void addSingleBytes(byte[] data, List<byte[]> dictionary) {
        for (int b = 0; b < 256; b++) {
            byte target = (byte) b;
            int count = 0;
            for (byte d : data) {
                if (d == target) count++;
            }

            if (count > 0) {
                chains.add(new Chain(new byte[]{target}, count));
            }
        }
    }

    private boolean matchesAt(byte[] data, byte[] sub, int pos) {
        for (int i = 0; i < sub.length; i++) {
            if (data[pos + i] != sub[i]) return false;
        }
        return true;
    }

    private boolean contains(List<byte[]> list, byte[] target) {
        for (byte[] item : list) {
            if (Arrays.equals(item, target)) return true;
        }
        return false;
    }

    public List<Chain> getChains() {
        return chains;
    }
}