package TVH.GUI;

import TVH.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphDriver extends Thread {

    //attributen
    private GraphController graphController;

    //constructor
    public GraphDriver(){
        graphController = loadAndSetGui();
    }

    private GraphController loadAndSetGui() {
        Parent root = null;
        GraphController controller = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("GUI/graph.fxml"));
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent finalRoot = root;
        Platform.runLater(
                () -> {
                    Stage spelViewStage= new Stage();
                    spelViewStage.setTitle("Graph");
                    //width en height van de scene bepalen
                    //dit moet hier geset worden, jammergenoeg, we kunnen dit niet later aanpassen
                    Scene startScene= new Scene(finalRoot); //misschien nog wijzigen
                    spelViewStage.setScene(startScene);
                    spelViewStage.setResizable(false);
                    spelViewStage.show();
                }
        );

        return controller;

    }

    public void addPoint(int currentTime, int aantalKm){
        graphController.addPunt(currentTime, aantalKm);
    }


    //ander tuug
    @Override
    public void run() {

    }
}
