import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Problem problem = new Problem(inputFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
