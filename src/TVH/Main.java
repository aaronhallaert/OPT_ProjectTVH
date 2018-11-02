package TVH;

import TVH.Visualisation.Visualisation;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");
        long startTIme = System.currentTimeMillis();

        try{
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve();
            System.out.println("Calculation time: "+(System.currentTimeMillis()-startTIme)+"ms");
            Visualisation.run(solution, problem.clusters, problem.depots);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
