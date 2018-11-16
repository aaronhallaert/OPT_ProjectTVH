package TVH;

import TVH.Gui.GrafiekAanstuurder;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
public class Main extends Application{

    static String INPUT_FILE;
    static String OUTPUT_FILE ;

    public static void main(String[] args) {

        INPUT_FILE = args[0]+".txt";
        OUTPUT_FILE = args[0]+"_out.txt";

        File inputFile = new File(INPUT_FILE);
        long startTime = System.currentTimeMillis();

        //GUI thread aanmaken en laten runnen
        GrafiekAanstuurder grafiekAanstuurder = new GrafiekAanstuurder();
        grafiekAanstuurder.run();


        try{
            Problem problem = Problem.newInstance(inputFile);
            Solution solution = problem.solve();
            solution.writeToFile(OUTPUT_FILE);
            System.out.println("Calculation time: "+(System.currentTimeMillis()-startTime)+"ms");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //dit moet der in, anders krijg je geen javaFX componenten
    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}
