package TVH;

import TVH.Visualisation.Visualisation;

import java.io.File;
import java.io.IOException;

public class Main {

    static String INPUT_FILE;
    static String OUTPUT_FILE ;

    public static void main(String[] args) {

        INPUT_FILE = args[0]+".txt";
        OUTPUT_FILE = args[0]+"_out.txt";

        File inputFile = new File(INPUT_FILE);
        long startTIme = System.currentTimeMillis();

        try{
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve(1);
            solution.writeToFile(OUTPUT_FILE);
            System.out.println("Calculation time: "+(System.currentTimeMillis()-startTIme)+"ms");
            Visualisation.run(solution, problem.clusters, problem.depots);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
