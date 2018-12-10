package TVH;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdk.nashorn.internal.scripts.JO;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main{

    static String INPUT_FILE;
    static String OUTPUT_FILE ;
    static long SEED;
    static int DURATION;
    static long BEGIN_TIME;
    static int TEMPERATUUR;
    static int TRUCKS;
    static int MACHINETYPES;
    static int JOBS;



    public static void main(String[] args) {
        for(String s: args){
            String argument = s.split("=")[0];
            String value = s.split("=")[1];
            switch(argument){
                case "--problem":
                    INPUT_FILE = value;
                    break;

                case "--solution":
                    OUTPUT_FILE = value;
                    break;

                case "--seed" :
                    SEED = Long.parseLong(value);
                    break;

                case "--time":
                    DURATION = Integer.parseInt(value);
                    break;

                case "--temp":
                    TEMPERATUUR = Integer.parseInt(value);
                    break;

                case "--atrucks":
                    TRUCKS = Integer.parseInt(value);
                    break;

                case "--amtypes":
                    MACHINETYPES = Integer.parseInt(value);
                    break;

                case "--ajobs":
                    JOBS = Integer.parseInt(value);

            }
        }

        Config config = new Config();

        config.update(TEMPERATUUR, TRUCKS, MACHINETYPES, JOBS, "Jeroen", 100, 1000, 1000,1, 600, INPUT_FILE);

        File inputFile = new File(INPUT_FILE);
        long BEGIN_TIME = System.currentTimeMillis();

        try {
            Problem problem = Problem.newInstance(inputFile, SEED);
            Solution solution = problem.solve(config);
            solution.writeToFile(OUTPUT_FILE, INPUT_FILE);
            System.out.println("Calculation time: " + (System.currentTimeMillis() - BEGIN_TIME) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
