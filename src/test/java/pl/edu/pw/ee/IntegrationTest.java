package pl.edu.pw.ee;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.ProgramModes.Decompression;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private final String fileName = "example.txt";
    private final String compressedFileName = "compOutput.comp";

    @BeforeEach
    void setUp() {
        String fileName = "example.txt";
        String content = "Mniej mam i mniemam ze nie mam ja mienia\n" +
                "Mnie nie omamia mania mania mniemania\n" +
                "Ja mam imie a nie mienienie sie mianem\n" +
                "Ja manie mam na nie a me imie Niemanie";

        try {
            // Create file and write content (overwrites if exists)
            Files.writeString(Paths.get(fileName), content);
            System.out.println("File created successfully: " + fileName);
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // List of files to delete
        String[] filesToDelete = {"example.txt", "compOutput.comp", "decompOutput.txt"};

        for (String fileName : filesToDelete) {
            Path filePath = Paths.get(fileName);
            try {
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("File deleted successfully: " + filePath);
                } else {
                    System.out.println("File does not exist: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete the file: " + filePath + " -> " + e.getMessage());
            }
        }
    }


    @org.junit.jupiter.api.Test
    void shouldRunWholeProgramWithExampleInput() {
        String expectedValue = "1001110001010101011011111011000000101101000101100010111010011010010001000000011000111100001110000011001010100101110000110011000110101100000110011101110010010010111001111010111011011111000101011100110101111101101011001010001111111111010110010100011111100111101";

        // Dane wejściowe, które normalnie użytkownik wpisuje w konsoli
        String input = "java -jar AiSD2025ZEx5.jar -m comp -s example.txt -d C:\\Users\\Franek\\OneDrive - BAKK Sp. z o.o\\Pulpit -l 2";

        // Przechwytujemy wyjście
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {
            // Przekierowujemy wejście
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

            // Przekierowujemy wyjście
            System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));

            // Uruchamiamy cały program
            Main.main(input.split(" "));

            // Odczytujemy, co program wypisał
            String output = outContent.toString(StandardCharsets.UTF_8);

        } finally {
            // Przywracamy oryginalne strumienie
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    /*
    @Test
    void decompressCorruptedFilesShouldFail() throws IOException {
        Path corruptedPath = Paths.get("src/test/java/corruptedResources").toAbsolutePath();
        assertTrue(Files.exists(corruptedPath), "Corrupted resources folder does not exist");

        try (Stream<Path> files = Files.list(corruptedPath)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                byte[] corruptedData;
                try {
                    corruptedData = Files.readAllBytes(file);
                } catch (IOException e) {
                    fail("Failed to read file: " + file.getFileName());
                    return;
                }

                Decompression decompression = new Decompression();

                // Expect decompression to throw
                assertThrows(Exception.class, () -> {
                    decompression.decompressFile(corruptedData);
                }, "File should fail decompression: " + file.getFileName());
            });
        }
    }
     */

}
