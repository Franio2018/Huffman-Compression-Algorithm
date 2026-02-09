package pl.edu.pw.ee.Algorithms;

import pl.edu.pw.ee.Structs.Chain;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public List<byte[]> tokenize(byte[] data, List<Chain> dictionary) {
        List<byte[]> tokens = new ArrayList<>();
        int i = 0;

        while (i < data.length) {
            byte[] best = null;

            for (Chain c : dictionary) {
                byte[] symbol = c.bytes;

                if (matchesAt(data, i, symbol)) {
                    if (best == null || symbol.length > best.length) {
                        best = symbol;
                    }
                }
            }

            if (best == null) {
                throw new IllegalStateException(
                        "No matching symbol at position " + i
                );
            }

            tokens.add(best);
            i += best.length;
        }

        return tokens;
    }

    private boolean matchesAt(byte[] data, int pos, byte[] symbol) {
        if (pos + symbol.length > data.length) return false;

        for (int i = 0; i < symbol.length; i++) {
            if (data[pos + i] != symbol[i]) return false;
        }

        return true;
    }
}