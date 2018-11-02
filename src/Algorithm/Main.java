package Algorithm;

import PlotPackage.GraphBuilder;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {


    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);
            problem.createInitialSolution();
            printGraph(problem);

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
