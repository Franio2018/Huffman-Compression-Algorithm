package pl.edu.pw.ee.ProgramModes;

import pl.edu.pw.ee.Structs.ChainAndCode;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class Compression {

    private ChainAndCode findLongestMatch(byte[] data, int index, List<ChainAndCode> codedChains) {
        ChainAndCode best = null;

        for (ChainAndCode chain : codedChains) {
            byte[] symbol = chain.symbol;

            if (matchesAt(data, index, symbol)) {
                if (best == null || symbol.length > best.symbol.length) {
                    best = chain;
                }
            }
        }
        return best;
    }

    private boolean matchesAt(byte[] data, int index, byte[] symbol) {
        if (index + symbol.length > data.length) return false;

        for (int i = 0; i < symbol.length; i++) {
            if (data[index + i] != symbol[i]) return false;
        }
        return true;
    }

    /**
     * Compress input and return packed bytes with 3-bit padding header
     */
    public byte[] compressToBytes(byte[] data, List<ChainAndCode> codedChains) {
        StringBuilder bits = new StringBuilder();
        int i = 0;

        // 1. Build Huffman bit string
        while (i < data.length) {
            ChainAndCode match = findLongestMatch(data, i, codedChains);

            if (match == null) {
                throw new IllegalStateException(
                        "No Huffman code for byte at position " + i
                );
            }

            bits.append(match.code);
            i += match.symbol.length;
        }

        // 2. Insert 3-bit placeholder for header at start
        bits.insert(0, "000"); // placeholder for padding count

        // 3. Compute extra padding bits AFTER adding header
        int totalBits = bits.length();
        int extraBits = (8 - (totalBits % 8)) % 8; // 0-7 padding bits

        // 4. Replace placeholder with actual 3-bit header
        String header = String.format("%3s", Integer.toBinaryString(extraBits)).replace(' ', '0');
        bits.replace(0, 3, header);

        // 5. Pack bits into bytes
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int currentByte = 0;
        int bitsFilled = 0;

        for (int k = 0; k < bits.length(); k++) {
            currentByte = (currentByte << 1) | (bits.charAt(k) == '1' ? 1 : 0);
            bitsFilled++;

            if (bitsFilled == 8) {
                out.write(currentByte);
                currentByte = 0;
                bitsFilled = 0;
            }
        }

        // 6. Pad last byte with zeros if necessary
        if (bitsFilled > 0) {
            currentByte <<= (8 - bitsFilled);
            out.write(currentByte);
        }

        return out.toByteArray();
    }
}