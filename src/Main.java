import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);
//            Solution solution = problem.solve();

            Cluster cluster = new Cluster(1.2,2.4,1.2,2.4);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
