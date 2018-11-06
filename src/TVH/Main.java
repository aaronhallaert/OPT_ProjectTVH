package TVH;

import TVH.Visualisation.Visualisation;

import java.io.File;
import java.io.IOException;

public class Main {

    static String INPUT_FILE = "input.txt";
    static String OUTPUT_FILE = "output.txt";

    public static void main(String[] args) {

        File inputFile = new File(INPUT_FILE);
        long startTIme = System.currentTimeMillis();

        try{
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve();
            solution.writeToFile(OUTPUT_FILE);
            System.out.println("Calculation time: "+(System.currentTimeMillis()-startTIme)+"ms");
            Visualisation.run(solution, problem.clusters, problem.depots);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
