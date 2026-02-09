package pl.edu.pw.ee.ProgramModes;

import java.io.ByteArrayOutputStream;

public class Decompression {

    private static class Node {
        Node left, right;
        byte[] value;  // null for internal nodes
        boolean isLeaf() { return value != null; }
    }

    /**
     * Decompresses a file written with `writeCompressedFile`:
     * - first 4 bytes = tree length (big-endian)
     * - next treeLength bytes = serialized Huffman tree
     * - rest = compressed bitstream
     */
    public byte[] decompressFile(byte[] fileContent) {
        if (fileContent.length < 4) throw new IllegalArgumentException("File too short");

        // 1. Read tree length (first 4 bytes, big-endian)
        int treeLength = ((fileContent[0] & 0xFF) << 24) |
                ((fileContent[1] & 0xFF) << 16) |
                ((fileContent[2] & 0xFF) << 8) |
                (fileContent[3] & 0xFF);

        if (fileContent.length < 4 + treeLength)
            throw new IllegalArgumentException("Invalid file: tree length exceeds file size");

        // 2. Extract tree bytes
        byte[] treeBytes = new byte[treeLength];
        System.arraycopy(fileContent, 4, treeBytes, 0, treeLength);

        // 3. Extract compressed data
        byte[] compressedData = new byte[fileContent.length - 4 - treeLength];
        System.arraycopy(fileContent, 4 + treeLength, compressedData, 0, compressedData.length);

        // 4. Reconstruct Huffman tree
        int[] idx = {0};
        Node root = readTree(treeBytes, idx);

        // 5. Decode compressed data
        return decodeData(compressedData, root);
    }

    // Deserialize tree from serialized bytes
    private Node readTree(byte[] bytes, int[] idx) {
        if (idx[0] >= bytes.length) throw new IllegalStateException("Unexpected end of tree bytes");

        byte marker = bytes[idx[0]++];
        if (marker == 1) { // leaf node
            int len = bytes[idx[0]++] & 0xFF;
            byte[] val = new byte[len];
            System.arraycopy(bytes, idx[0], val, 0, len);
            idx[0] += len;

            Node leaf = new Node();
            leaf.value = val;
            return leaf;
        } else if (marker == 0) { // internal node
            Node node = new Node();
            node.left = readTree(bytes, idx);
            node.right = readTree(bytes, idx);
            return node;
        } else {
            throw new IllegalStateException("Invalid tree marker: " + marker);
        }
    }

    // Decode bitstream using Huffman tree with 3-bit padding header
    private byte[] decodeData(byte[] data, Node root) {
        if (data.length == 0) return new byte[0];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Node current = root;

        // --- Read first 3 bits = number of extra padding bits ---
        int extraBits = ((data[0] >> 5) & 0b111);

        int totalBits = data.length * 8 - extraBits - 3; // subtract 3 header bits
        int bitPos = 0;

        for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
            for (int bit = 7; bit >= 0; bit--) {
                // Skip the 3-bit header in the first byte
                if (byteIndex == 0 && bit >= 5) continue;

                if (bitPos >= totalBits) break;

                int b = (data[byteIndex] >> bit) & 1;
                current = (b == 0) ? current.left : current.right;

                if (current.isLeaf()) {
                    out.write(current.value, 0, current.value.length);
                    current = root;
                }

                bitPos++;
            }
        }

        return out.toByteArray();
    }
}