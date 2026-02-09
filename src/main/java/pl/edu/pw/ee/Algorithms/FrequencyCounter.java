package pl.edu.pw.ee.Algorithms;

import pl.edu.pw.ee.Structs.Chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrequencyCounter {

    // Count actual frequencies of tokens
    public List<Chain> countFrequencies(List<byte[]> tokens) {
        List<Chain> freqChains = new ArrayList<>();

        for (byte[] token : tokens) {
            boolean found = false;

            for (Chain c : freqChains) {
                if (Arrays.equals(c.bytes, token)) {
                    c.amount++;
                    found = true;
                    break;
                }
            }

            if (!found) {
                freqChains.add(new Chain(token, 1));
            }
        }

        return freqChains;
    }
}