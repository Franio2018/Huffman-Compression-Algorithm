package pl.edu.pw.ee;

import pl.edu.pw.ee.Structs.ProgramMode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Input {
    private ProgramMode mode;
    private String inputPath;
    private String outputPath;
    private int maxChainLength;

    public void read(String[] input){
        if(input == null)
            throw new NullPointerException("Input is null");
        List<String> args = Arrays.asList(input);
// --- MODE ---
        int mIndex = args.indexOf("-m");
        if (mIndex != -1 && mIndex + 1 < args.size()) {
            mode = Arrays.stream(ProgramMode.values())
                    .filter(m -> m.name().equalsIgnoreCase(args.get(mIndex + 1)))
                    .findFirst()
                    .orElse(null);
        }

// --- SOURCE PATH ---
        int sIndex = args.indexOf("-s");
        if (sIndex != -1 && sIndex + 1 < args.size()) {
            inputPath = args.get(sIndex + 1);
        }

// --- DESTINATION PATH ---
        int dIndex = args.indexOf("-d");
        if (dIndex != -1 && dIndex + 1 < args.size()) {
            outputPath = args.get(dIndex + 1);
        }

// --- MAX CHAIN LENGTH ---
        int lIndex = args.indexOf("-l");
        if (lIndex != -1 && lIndex + 1 < args.size()) {
            maxChainLength = Integer.parseInt(args.get(lIndex + 1));
        }
    }

    public void validateInputs(){

        if(mode == null)
            throw(new RuntimeException("mode can't be null!"));
        System.out.println("mode is correct");

        if(!checkIfInputFileExists())
            throw(new RuntimeException("Input file doesn't exists!"));
        System.out.println("Input path is correct");

        boolean outputPathStatus = checkIfOutputFileExists();
        if(!outputPathStatus) {
            outputPath = "Project Path";
            System.out.println("Output path has not been found!");
            String projectPath = System.getProperty("user.dir");
            outputPath = projectPath;
            System.out.println("Output path has been set to " + projectPath);
        }
        else {
            System.out.println("Output path is correct");
        }
        if(maxChainLength <= 0)
        {
            maxChainLength = 1;
        }
    }

    private boolean checkIfInputFileExists() {
        return Files.exists(Paths.get(inputPath));
    }

    private boolean checkIfOutputFileExists() {
        if(getOutputPath() == null)
            return false;
        Path testPath = Paths.get(getOutputPath());
        return Files.exists(testPath);
    }


    public ProgramMode getMode() {
        return mode;
    }

    public String getInputPath() {
        return inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public int getMaxChainLength() {
        return maxChainLength;
    }
}
