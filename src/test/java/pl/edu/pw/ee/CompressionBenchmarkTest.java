package pl.edu.pw.ee;

import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CompressionBenchmarkTest {

    private static final int MAX_CHAIN_LENGTH = 10;

    @Test
    void benchmarkCompressionEfficiencyByChainLength() throws Exception {

        Path resourcesPath = Paths.get("src/test/java/resources").toAbsolutePath();
        assertTrue(Files.exists(resourcesPath), "Resources folder not found");

        List<Result> results = new ArrayList<>();

        for (int chainLen = 1; chainLen <= 10; chainLen++) {

            List<Double> ratios = new ArrayList<>();
            boolean allCorrect = true;

            try (Stream<Path> files = Files.list(resourcesPath)) {
                for (Path resourceFile : files
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList())) {

                    Path tempDir = Files.createTempDirectory("bench");
                    Path inputCopy = tempDir.resolve(resourceFile.getFileName());
                    Files.copy(resourceFile, inputCopy, StandardCopyOption.REPLACE_EXISTING);

                    Path outDir = tempDir.resolve("out");
                    Files.createDirectories(outDir);

                    // COMPRESS
                    Main.main(new String[]{
                            "-m", "comp",
                            "-s", inputCopy.toString(),
                            "-d", outDir.toString(),
                            "-l", String.valueOf(chainLen)
                    });

                    Path compFile = outDir.resolve(inputCopy.getFileName() + ".comp");
                    if (!Files.exists(compFile)) {
                        allCorrect = false;
                        continue;
                    }

                    // DECOMPRESS
                    Main.main(new String[]{
                            "-m", "decomp",
                            "-s", compFile.toString(),
                            "-d", outDir.toString()
                    });

                    Path decompFile = outDir.resolve(inputCopy.getFileName() + ".decomp");
                    if (!Files.exists(decompFile)) {
                        allCorrect = false;
                        continue;
                    }

                    byte[] original = Files.readAllBytes(inputCopy);
                    byte[] decompressed = Files.readAllBytes(decompFile);

                    if (!Arrays.equals(original, decompressed)) {
                        allCorrect = false;
                    }

                    double ratio = (double) Files.size(compFile) / original.length;
                    ratios.add(ratio);
                }
            }

            if (ratios.isEmpty()) {
                results.add(new Result(chainLen, -1, -1, -1, false));
            } else {
                double avg = ratios.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double min = ratios.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                double max = ratios.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                results.add(new Result(chainLen, avg, min, max, allCorrect));
            }
        }

        // ---- FINAL SUMMARY ----
        System.out.println("\n=== HUFFMAN COMPRESSION BENCHMARK SUMMARY ===");
        System.out.println("Chain | AvgRatio | Best | Worst | Status");
        System.out.println("-------------------------------------------");

        for (Result r : results) {
            if (r.avg < 0) {
                System.out.printf("%5d |   n/a    |  n/a |  n/a  | FAIL%n", r.chainLength);
            } else {
                System.out.printf(
                        "%5d | %8.3f | %5.3f | %5.3f | %s%n",
                        r.chainLength, r.avg, r.min, r.max, r.ok ? "OK" : "FAIL"
                );
            }
        }

        System.out.println("===========================================\n");
    }

    private static class Result {
        int chainLength;
        double avg;
        double min;
        double max;
        boolean ok;

        Result(int chainLength, double avg, double min, double max, boolean ok) {
            this.chainLength = chainLength;
            this.avg = avg;
            this.min = min;
            this.max = max;
            this.ok = ok;
        }
    }
}