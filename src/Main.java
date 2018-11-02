import javafx.application.Application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");
        long startTIme = System.currentTimeMillis();
        try {
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long endTIme = System.currentTimeMillis();

        System.out.println(endTIme - startTIme);


    }
}
