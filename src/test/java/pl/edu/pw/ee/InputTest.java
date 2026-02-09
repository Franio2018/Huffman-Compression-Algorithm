package pl.edu.pw.ee;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.Structs.ProgramMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class InputTest {
    Input input;
    @BeforeEach
    void setUp() {
        input = new Input();
        String fileName = "example.txt";
        String content = "Hello, this is a new text file created using Java!\nLine 2\nLine 3";

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
        Path filePath = Paths.get("example.txt");

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete the file: " + e.getMessage());
        }
    }

    @org.junit.jupiter.api.Test
    void ShouldAcceptInput() {
        input.read("java -jar AiSD2025ZEx5.jar -m comp -s example.txt -d C:/ -l 2".split(" "));
        input.validateInputs();
        assertEquals("C:/", input.getOutputPath());
        assertEquals("example.txt", input.getInputPath());
        assertEquals(ProgramMode.comp, input.getMode());
        assertEquals(2, input.getMaxChainLength());
    }

    @org.junit.jupiter.api.Test
    void ShouldThrowNoInputPath() {
        input.read("java -jar AiSD2025ZEx5.jar -m comp -d sciezka/do/innego/katalogu/niemanie_txt.comp -l 2".split(" "));
        RuntimeException exception = assertThrows(NullPointerException.class, () -> {
            input.validateInputs();
        });
    }

    @org.junit.jupiter.api.Test
    void ShouldThrowWrongInputPath() {
        input.read("java -jar AiSD2025ZEx5.jar -m comp -s lipa -d sciezka/do/innego/katalogu/niemanie_txt.comp -l 2".split(" "));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            input.validateInputs();
        });
    }

    @org.junit.jupiter.api.Test
    void ShouldThrowError() {
        input.read("java -jar AiSD2025ZEx5.jar -m -s -d -l ".split(" "));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            input.validateInputs();
        });
    }
    @org.junit.jupiter.api.Test
    void CheckForIndexOutOfRange() {
        input.read("java -jar AiSD2025ZEx5.jar -m comp -s example.txt -d C:/ -l ".split(" "));
        input.validateInputs();
        input.read("java -jar AiSD2025ZEx5.jar -m comp -s example.txt -l 2 -d".split(" "));
        input.validateInputs();
        input.read("java -jar AiSD2025ZEx5.jar -m comp -d C:/ -l 2 -s".split(" "));
        input.validateInputs();
        input.read("java -jar AiSD2025ZEx5.jar -s example.txt -d C:/ -l 2 -m".split(" "));
        input.validateInputs();
    }

    @org.junit.jupiter.api.Test
    void ShouldChangeOutputPathToProjectPath() {
        input.read("java -jar AiSD2025ZEx5.jar -m comp -s example.txt -d C:/nie/ma/takiej/scieżki -l 2".split(" "));
        input.validateInputs();
        assertEquals(input.getOutputPath(), System.getProperty("user.dir"));
    }
    @org.junit.jupiter.api.Test
    void ShouldHandleNullOrEmptyInput() {
        input.read("".split(" "));
        RuntimeException RTE = assertThrows( RuntimeException.class, () -> {
            input.validateInputs();
        });
        assertEquals(RTE.getMessage(), "mode can't be null!");

        NullPointerException exception = assertThrows( NullPointerException.class, () -> {
            input.read(null);
        });
        RTE = assertThrows( RuntimeException.class, () -> {
            input.validateInputs();
        });
        assertEquals(RTE.getMessage(), "mode can't be null!");
    }

    @org.junit.jupiter.api.Test
    void InputIsEmpty() {
        input.read("".split(" "));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            input.validateInputs();
        });
    }

    @org.junit.jupiter.api.Test
    void InputIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> input.read(null));
        assertEquals("Input is null", ex.getMessage());
    }
}