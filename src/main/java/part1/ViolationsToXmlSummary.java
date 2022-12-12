package part1;

import java.io.File;
import java.util.Map;

public class ViolationsToXmlSummary {
    /*
            1 - 8.8s,
            2 - 5.1s,
            4 - 4.2s,
            8 - 5.9s;

            Input: 50 files, 1.53Gb total.
    */
    public static void main(String[] args) {
        Parser p = new Parser();
        File directory = new File("src/main/resources/part1/input");
        Map<ViolationType, Double> fineSummary = p.getFineSummaryFromFolder(directory);
        p.writeToXml(p.sortSummary(fineSummary), new File("src/main/resources/part1/output/fine_summary.xml"));
    }
}