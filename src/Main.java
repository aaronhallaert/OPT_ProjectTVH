import PlotPackage.Grafiek;
import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);
            final Grafiek plot= new Grafiek("locaties", problem.locations, problem.depots);
            plot.pack();
            RefineryUtilities.centerFrameOnScreen(plot);
            plot.setVisible(true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }
}
