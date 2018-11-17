package TVH.Gui;

import TVH.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GrafiekAanstuurder extends Thread {

    //attributen
    private GrafiekController grafContr;

    //constructor
    public GrafiekAanstuurder(){
        grafContr = loadAndSetGui();
    }

    private GrafiekController loadAndSetGui() {
        Parent root = null;
        GrafiekController controller = null;
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Gui/grafiek.fxml"));
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent finalRoot = root;
        Platform.runLater(
                () -> {
                    Stage spelViewStage= new Stage();
                    spelViewStage.setTitle("grafiek");
                    //width en height van de scene bepalen
                    //dit moet hier geset worden, jammergenoeg, we kunnen dit niet later aanpassen
                    Scene startScene= new Scene(finalRoot, 700,500); //misschien nog wijzigen
                    spelViewStage.setScene(startScene);
                    spelViewStage.setResizable(false);
                    spelViewStage.show();
                }
        );

        return controller;

    }

    public void addPunt(Integer currentTime, Integer aantalKm){
        grafContr.addPunt(currentTime, aantalKm);
    }


    //ander tuug
    @Override
    public void run() {

    }
}
