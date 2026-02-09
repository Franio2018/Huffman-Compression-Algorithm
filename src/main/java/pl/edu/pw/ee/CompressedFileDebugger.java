package pl.edu.pw.ee.debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CompressedFileDebugger {

    public static void debug(Path file) throws Exception {
        byte[] data = Files.readAllBytes(file);

        if (data.length < 4) {
            throw new IllegalArgumentException("File too short");
        }

        // 1. Read tree length
        int treeLength = ((data[0] & 0xFF) << 24)
                | ((data[1] & 0xFF) << 16)
                | ((data[2] & 0xFF) << 8)
                |  (data[3] & 0xFF);

        System.out.println("=== COMPRESSED FILE DEBUG ===");
        System.out.println("Tree length: " + treeLength + " bytes\n");

        // 2. Extract tree bytes
        byte[] treeBytes = Arrays.copyOfRange(data, 4, 4 + treeLength);
        System.out.println("--- HUFFMAN TREE (RAW) ---");
        dumpTree(treeBytes, new int[]{0}, 0);

        // 3. Extract compressed data
        byte[] compressed = Arrays.copyOfRange(data, 4 + treeLength, data.length);

        System.out.println("\n--- COMPRESSED BITSTREAM ---");

        // Read padding header (first 3 bits)
        int padding = 0;
        for (int i = 7; i >= 5; i--) {
            padding = (padding << 1) | ((compressed[0] >> i) & 1);
        }

        System.out.println("Padding bits: " + padding);

        // Convert to bit string
        StringBuilder bits = new StringBuilder();
        for (byte b : compressed) {
            for (int i = 7; i >= 0; i--) {
                bits.append((b >> i) & 1);
            }
        }

        // Remove header + padding
        bits.delete(0, 3);
        bits.setLength(bits.length() - padding);

        System.out.println("Bits:");
        System.out.println(bits);
        System.out.println("\nTotal bits (without padding): " + bits.length());
        System.out.println("================================");
    }

    // ---- TREE DUMP ----
    private static void dumpTree(byte[] bytes, int[] idx, int depth) {
        indent(depth);

        byte marker = bytes[idx[0]++];

        if (marker == 1) { // leaf
            int len = bytes[idx[0]++] & 0xFF;
            byte[] value = Arrays.copyOfRange(bytes, idx[0], idx[0] + len);
            idx[0] += len;

            System.out.println("Leaf: \"" + printable(value) + "\"");
        } else {
            System.out.println("Node");
            dumpTree(bytes, idx, depth + 1);
            dumpTree(bytes, idx, depth + 1);
        }
    }

    private static void indent(int depth) {
        System.out.print("  ".repeat(depth));
    }

    private static String printable(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            if (b >= 32 && b <= 126) sb.append((char) b);
            else sb.append(String.format("\\x%02X", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        CompressedFileDebugger.debug(Paths.get("C:\\Users\\Franek\\Downloads\\test.txt.comp"));
    }
}