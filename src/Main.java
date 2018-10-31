import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);
            Solution solution = problem.solve();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
