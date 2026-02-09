package pl.edu.pw.ee.Structs;

import java.util.List;

public class CompressedInput {
    public final List<ChainAndCode> codedChains;
    public final String compressedData;

    public CompressedInput(List<ChainAndCode> codedChains, String compressedData) {
        this.codedChains = codedChains;
        this.compressedData = compressedData;
    }
}
