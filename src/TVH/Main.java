package TVH;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application{

    static String INPUT_FILE;
    static String OUTPUT_FILE ;
    static long BEGIN_TIME;

    public static void main(String[] args) {

        launch(args);

    }

    //dit moet der in, anders krijg je geen javaFX componenten
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI/ConfigGUI.fxml"));
        primaryStage.setTitle("Configurations");
        Scene startScene= new Scene(root,650 , 400);
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}
