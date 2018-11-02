package TVH;

import TVH.Visualization.Visualisation;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");
        long startTIme = System.currentTimeMillis();

        try {
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve();

            Long timeRun = System.currentTimeMillis() - startTIme;
            System.out.println("Time run: "+ timeRun + "ms");
            Visualisation.start(new Solution(Problem.trucks), Problem.clusters, Problem.depots);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
