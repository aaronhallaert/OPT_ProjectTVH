package TVH;

import TVH.Visualisation.Visualisation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");
        long startTIme = System.currentTimeMillis();

        try{
            Problem problem = new Problem(inputFile);
            Solution solution= problem.solve();
            Long timeRun = System.currentTimeMillis() - startTIme;
            System.out.println("Time run: "+ timeRun + "ms");
            Visualisation.start(solution, problem.clusters, problem.depots);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
