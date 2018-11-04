package Algorithm;

import Visualisation.GraphBuilder;
import Visualisation.Visualisation;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {


    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);
            Solution solution= problem.createInitialSolution();
            Visualisation.run(solution, problem.jernClusters, problem.jernDepots);
            //printGraph(problem);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }

    public static void printGraph(Problem problem){
        final GraphBuilder plot= new GraphBuilder("locaties", problem.clusters, problem.depots);
        plot.pack();
        RefineryUtilities.centerFrameOnScreen(plot);
        plot.setVisible(true);
    }
}
