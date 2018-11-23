package TVH;

import TVH.GUI.ConfigController;
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI/ConfigGUI.fxml"));
        Parent root = loader.load();
        ConfigController configc = loader.getController();
        //Configfile doorgeven aan controller
        if(!getParameters().getRaw().isEmpty()) {
            configc.autoLoadConfig(getParameters().getRaw().get(0));
        }

        primaryStage.setTitle("Configurations");
        Scene startScene= new Scene(root);
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}
