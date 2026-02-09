package pl.edu.pw.ee;

import pl.edu.pw.ee.Algorithms.ChainsFinder;
import pl.edu.pw.ee.Algorithms.FileManager;
import pl.edu.pw.ee.Algorithms.FrequencyCounter;
import pl.edu.pw.ee.Algorithms.Tokenizer;
import pl.edu.pw.ee.ProgramModes.Compression;
import pl.edu.pw.ee.ProgramModes.Decompression;
import pl.edu.pw.ee.Structs.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\Franek\\Downloads\\compOutput.comp -d lipa -l 4".split(" ") : args;

        args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\Franek\\Downloads\\test.txt -d C:\\Users\\Franek\\Downloads -l 2".split(" ") : args;
        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m decomp -s C:\\Users\\Franek\\Downloads\\test.txt.comp -d lipa".split(" ") : args;

        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\Franek\\Downloads\\seaLion.jpg -d C:\\Users\\Franek\\Downloads".split(" ") : args;
        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m decomp -s C:\\Users\\Franek\\Downloads\\seaLion.jpg.comp -d lipa".split(" ") : args;

        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\Franek\\Downloads\\Cr-20251023T215004Z-1-001\\1024.mp4 -d C:\\Users\\Franek\\Downloads".split(" ") : args;
        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m decomp -s C:\\Users\\Franek\\Downloads\\seaLion.jpg.comp -d lipa".split(" ") : args;

        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\Franek\\Downloads\\ZdIcEGwn.jpg -d lipa -l 10".split(" ") : args;
        //args = args.length == 0 ? "java -jar .\\target\\AiSD2025ZEx5-1.0-SNAPSHOT.jar -m comp -s C:\\Users\\abc\\Downloads\\3S_xn1wH.jpg -d sciezka/do/innego/katalogu/niemanie_txt.comp -l 4".split(" ") : args;

        Input input = new Input();
        input.read(args);
        input.validateInputs();

        switch (input.getMode()) {

            case comp: {
                FileManager fm = new FileManager();

                // 1. Read input
                byte[] data = fm.readBytesFile(input.getInputPath());

                // 2. Build dictionary
                ChainsFinder chainsFinder = new ChainsFinder();
                chainsFinder.findChains(data, input.getMaxChainLength());
                List<Chain> dictionary = chainsFinder.getChains();

                // 3. Tokenize
                Tokenizer tokenizer = new Tokenizer();
                List<byte[]> tokens = tokenizer.tokenize(data, dictionary);

                // 4. Count real frequencies
                FrequencyCounter freqCounter = new FrequencyCounter();
                List<Chain> freqChains = freqCounter.countFrequencies(tokens);

                // 5. Build Huffman tree
                HuffmanTree hffTree = new HuffmanTree(freqChains);
                hffTree.startWalkingOnTree();
                List<ChainAndCode> codedChains = hffTree.codedChains;

                // 5. Compress
                Compression compression = new Compression();
                byte[] compressedBits = compression.compressToBytes(data, codedChains);

                // 6. Write compressed file (DICT + DATA)
                fm.writeCompressedFile(
                        input.getInputPath(),
                        hffTree.serializeTree(),
                        compressedBits,
                        input.getOutputPath()
                );

                break;
            }

            case decomp: {
                FileManager fm = new FileManager();
                byte[] compressed = fm.readBytesFile(input.getInputPath());

                Decompression decompression = new Decompression();
                byte[] decompressedBytes = decompression.decompressFile(compressed);

                fm.writeBytesFile(input.getInputPath(), decompressedBytes, input.getOutputPath(), ProgramMode.decomp);
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown mode: " + input.getMode());
        }
    }
}