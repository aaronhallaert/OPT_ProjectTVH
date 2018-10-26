import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input.txt");

        try {
            Input input= new Input();
            input.fromTxt(inputFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
