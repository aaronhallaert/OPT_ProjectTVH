package TVH.Gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import javax.sound.sampled.Line;

public class GrafiekController {

    @FXML
    private Label solutionLabel;

    private XYChart.Series series1; // = dataverzameling

    @FXML
    private ScatterChart<Integer, Integer> scatterChart;

    @FXML
    public void initialize(){

        //zodat alle punten er altijd op zullen graken
       // scatterChart.getXAxis().setTickLength(1000); // 1 seconde
        //scatterChart.getYAxis().setTickLength(20);
        scatterChart.getXAxis().setLabel("tijd");
        scatterChart.getYAxis().setLabel("aantal km");
        scatterChart.getXAxis().setAutoRanging(true);

        //css file inladen (punten anders veel te dik)

        //scatterChart.getYAxis().setAutoRanging(true);

        series1 = new XYChart.Series();
        series1.setName("verzameling");

        scatterChart.getData().add(series1);





    }

    public void addPunt(Integer currentTime, Integer aantalKm){
        series1.getData().add(new XYChart.Data(currentTime+"",aantalKm));
    }






}