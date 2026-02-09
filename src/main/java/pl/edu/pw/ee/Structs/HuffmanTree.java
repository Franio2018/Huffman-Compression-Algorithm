package pl.edu.pw.ee.Structs;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HuffmanTree {

    private Node root;

    public HuffmanTree(List<Chain> chains) {
        List<Node> leaves = new ArrayList<>();
        for (Chain c : chains) {
            leaves.add(new Node(c.bytes, c.amount));
        }

        leaves.sort(Comparator.comparingInt(n -> n.count));

        List<Node> branches = new ArrayList<>();

        while (leaves.size() + branches.size() > 1) {
            Node left = popSmallest(leaves, branches);
            Node right = popSmallest(leaves, branches);

            Node parent = new Node(left, right);
            insertSorted(branches, parent);
        }

        root = popSmallest(leaves, branches);
    }

    public List<ChainAndCode> codedChains = new ArrayList<>();

    public void startWalkingOnTree() {
        preorder(root, "");
    }

    private void preorder(Node node, String path) {
        if (node == null) return;

        if (node.value != null) {
            codedChains.add(new ChainAndCode(node.value, path));
        }

        preorder(node.left, path + "0");
        preorder(node.right, path + "1");
    }

    /* ================= TREE BUILD HELPERS ================= */

    private Node popSmallest(List<Node> a, List<Node> b) {
        if (a.isEmpty()) return b.remove(0);
        if (b.isEmpty()) return a.remove(0);
        return (a.get(0).count <= b.get(0).count) ? a.remove(0) : b.remove(0);
    }

    private void insertSorted(List<Node> list, Node node) {
        int i = 0;
        while (i < list.size() && list.get(i).count < node.count) {
            i++;
        }
        list.add(i, node);
    }

    /* ================= NODE ================= */

    public static class Node {
        public Node left, right;
        public byte[] value; // null for internal nodes
        public int count;

        public Node(byte[] value, int count) {
            this.value = value;
            this.count = count;
        }

        public Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.count = left.count + right.count;
            this.value = null;
        }
    }

    /* ================= SERIALIZATION ================= */

    public byte[] serializeTree() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeNode(root, out);
        return out.toByteArray();
    }

    private void serializeNode(Node node, ByteArrayOutputStream out) {
        if (node.value != null) {
            out.write(1);                  // leaf marker
            out.write(node.value.length);  // symbol length
            out.writeBytes(node.value);    // symbol bytes
        } else {
            out.write(0);                  // internal node
            serializeNode(node.left, out);
            serializeNode(node.right, out);
        }
    }
}