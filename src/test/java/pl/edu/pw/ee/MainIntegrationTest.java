package pl.edu.pw.ee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MainIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void compressAndDecompressAllResourcesInSrc() throws Exception {

        // 1. Locate src/test/resources
        Path resourcesPath = Paths.get("src/test/java/resources").toAbsolutePath();
        assertTrue(Files.exists(resourcesPath), "Resources folder does not exist: " + resourcesPath);

        List<String> failures = new ArrayList<>();
        List<String> summary = new ArrayList<>();

        try (Stream<Path> files = Files.list(resourcesPath)) {
            for (Path resourceFile : files
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList())) {
                System.out.println("Processing: " + resourceFile.getFileName());

                try {
                    // ---- Copy resource to temp dir ----
                    Path inputCopy = tempDir.resolve(resourceFile.getFileName());
                    Files.copy(resourceFile, inputCopy, StandardCopyOption.REPLACE_EXISTING);

                    Path outDir = tempDir.resolve("out");
                    Files.createDirectories(outDir);

                    // ---- COMPRESS ----
                    Main.main(new String[]{
                            "-m", "comp",
                            "-s", inputCopy.toString(),
                            "-d", outDir.toString(),
                            "-l", "3"
                    });

                    Path compFile = outDir.resolve(inputCopy.getFileName().toString() + ".comp");
                    if (!Files.exists(compFile)) {
                        failures.add("Compressed file missing for " + resourceFile.getFileName());
                        continue;
                    }

                    // ---- DECOMPRESS ----
                    Main.main(new String[]{
                            "-m", "decomp",
                            "-s", compFile.toString(),
                            "-d", outDir.toString()
                    });

                    Path decompFile = outDir.resolve(inputCopy.getFileName().toString() + ".decomp");
                    if (!Files.exists(decompFile)) {
                        failures.add("Decompressed file missing for " + resourceFile.getFileName());
                        continue;
                    }

                    // ---- VERIFY ROUND-TRIP ----
                    byte[] original = Files.readAllBytes(inputCopy);
                    byte[] roundTrip = Files.readAllBytes(decompFile);

                    if (!Arrays.equals(original, roundTrip)) {
                        failures.add("Round-trip mismatch for " + resourceFile.getFileName());
                    }

                    // ---- ADD COMPRESSION SUMMARY ----
                    long originalSize = original.length;
                    long compressedSize = Files.size(compFile);
                    long decompressedSize = roundTrip.length;
                    double compressionRatio = (double) compressedSize / originalSize;

                    summary.add(String.format("%s -> original: %d bytes, compressed: %d bytes, decompressed: %d bytes, ratio: %.2f",
                            resourceFile.getFileName(),
                            originalSize,
                            compressedSize,
                            decompressedSize,
                            compressionRatio));

                } catch (Exception e) {
                    failures.add("Exception for " + resourceFile.getFileName() + ": " + e.getMessage());
                    e.printStackTrace(); // Print full stack trace for debugging
                }
            }
        }

        // ---- PRINT COMPARISON SUMMARY ----
        System.out.println("\n=== COMPRESSION SUMMARY ===");
        summary.forEach(System.out::println);
        System.out.println("===========================");

        // ---- PRINT FAILURES ----
        if (!failures.isEmpty()) {
            System.err.println("\n!!! FAILURES DETECTED !!!");
            failures.forEach(System.err::println);
            fail("Some files failed:\n" + String.join("\n", failures)); // fail the test if any
        }
    }

    // Helper method to generate the range 1-10
    static IntStream range1to10() {
        return IntStream.rangeClosed(1, 10);
    }

    @ParameterizedTest
    @MethodSource("range1to10")
    void testCompressionWithDifferentValues(int testValue) throws Exception {
        Path resourcesPath = Paths.get("src/test/java/resources");
        String valueStr = String.valueOf(testValue);

        System.out.println("\n--- TESTING WITH VALUE: " + valueStr + " ---");

        try (Stream<Path> files = Files.list(resourcesPath)) {
            files.filter(Files::isRegularFile).forEach(resourceFile -> {
                try {
                    // 1. Setup paths
                    Path inputCopy = tempDir.resolve("val_" + valueStr + "_" + resourceFile.getFileName());
                    Files.copy(resourceFile, inputCopy, StandardCopyOption.REPLACE_EXISTING);
                    Path outDir = tempDir.resolve("out_" + valueStr);
                    Files.createDirectories(outDir);

                    // 2. COMPRESS using the current testValue
                    Main.main(new String[]{
                            "-m", "comp",
                            "-s", inputCopy.toString(),
                            "-d", outDir.toString(),
                            "-l", valueStr // Using the range value here
                    });

                    Path compFile = outDir.resolve(inputCopy.getFileName().toString() + ".comp");
                    assertTrue(Files.exists(compFile), "Comp file missing for value " + valueStr);

                    // 3. DECOMPRESS
                    Main.main(new String[]{
                            "-m", "decomp",
                            "-s", compFile.toString(),
                            "-d", outDir.toString()
                    });

                    Path decompFile = outDir.resolve(inputCopy.getFileName().toString() + ".decomp");

                    // 4. VERIFY
                    byte[] original = Files.readAllBytes(inputCopy);
                    byte[] roundTrip = Files.readAllBytes(decompFile);

                    assertArrayEquals(original, roundTrip,
                            "Mismatch for " + resourceFile.getFileName() + " with value " + valueStr);

                } catch (IOException e) {
                    fail("IO Exception for " + resourceFile.getFileName() + " at value " + valueStr + ": " + e.getMessage());
                }
            });
        }
    }

}
