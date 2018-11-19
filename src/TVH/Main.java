package TVH;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
public class Main extends Application{

    static String INPUT_FILE;
    static String OUTPUT_FILE ;
    static long BEGIN_TIME;

    public static void main(String[] args) {

        INPUT_FILE = args[0]+".txt";
        OUTPUT_FILE = args[0]+"_out.txt";
        BEGIN_TIME = System.currentTimeMillis();

        File inputFile = new File(INPUT_FILE);
        long startTime = System.currentTimeMillis();

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
