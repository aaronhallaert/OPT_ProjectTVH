package TVH.GUI;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

public class GraphController {

    @FXML
    private Label solutionLabel;

    private XYChart.Series<Number, Number> series1; // = dataverzameling

    @FXML
    private ScatterChart<Number, Number> scatterChart;

    @FXML
    public void initialize() {

        //zodat alle punten er altijd op zullen graken
        // scatterChart.getXAxis().setTickLength(1000); // 1 seconde
        //scatterChart.getYAxis().setTickLength(20);
        //scatterChart = new ScatterChart<>(numberAxis, numberAxis);
        scatterChart.getXAxis().setLabel("Time (ms)");
        scatterChart.getYAxis().setLabel("Distance");
        scatterChart.getXAxis().setAutoRanging(true);
        scatterChart.getYAxis().setAutoRanging(true); // schaalt zodat alle punten zichtbaar zijn

        //css file inladen (punten anders veel te dik)

        //scatterChart.getYAxis().setAutoRanging(true);

        series1 = new XYChart.Series<Number, Number>();
        series1.setName("Solutions");

        scatterChart.getData().add(series1);


    }

    public void addPunt(int currentTime, int aantalKm) {
        series1.getData().add(new XYChart.Data(currentTime, aantalKm));


    }
}