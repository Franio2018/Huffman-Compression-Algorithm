package pl.edu.pw.ee.Algorithms;

import pl.edu.pw.ee.Structs.ProgramMode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    public byte[] readBytesFile(String inputPath) {
        try {
            return Files.readAllBytes(Paths.get(inputPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + inputPath, e);
        }
    }

    public void writeBytesFile(String inputPathStr, byte[] data, String outputPath, ProgramMode mode) {
        Path input = Paths.get(inputPathStr);

        try {
            Path path = Paths.get(outputPath);

            if (Files.exists(path) && Files.isDirectory(path)) {
                String inputFileName = input.getFileName().toString();

                if (mode == ProgramMode.decomp && inputFileName.endsWith(".comp")) {
                    inputFileName = inputFileName.substring(0, inputFileName.length() - ".comp".length());
                }

                String modeSuffix = (mode == ProgramMode.comp) ? "comp" : "decomp";
                String outputFileName = inputFileName + "." + modeSuffix;
                path = path.resolve(outputFileName);
            }

            if (path.getParent() != null) Files.createDirectories(path.getParent());

            Files.write(path, data);

        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + outputPath, e);
        }
    }

    public void writeCompressedFile(
            String inputPathStr,
            byte[] serializedTree,
            byte[] compressedData,
            String outputPath
    ) {
        // 4 bytes for tree length
        int treeLength = serializedTree.length;

        byte[] finalBytes = new byte[4 + treeLength + compressedData.length];

        // write tree length (big-endian)
        finalBytes[0] = (byte) (treeLength >>> 24);
        finalBytes[1] = (byte) (treeLength >>> 16);
        finalBytes[2] = (byte) (treeLength >>> 8);
        finalBytes[3] = (byte) (treeLength);

        // write tree bytes
        System.arraycopy(serializedTree, 0, finalBytes, 4, treeLength);

        // write compressed data
        System.arraycopy(compressedData, 0, finalBytes, 4 + treeLength, compressedData.length);

        writeBytesFile(inputPathStr, finalBytes, outputPath, ProgramMode.comp);
    }
}