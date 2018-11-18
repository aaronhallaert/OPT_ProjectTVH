package TVH.GUI;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

public class GraphController {

    @FXML
    private Label solutionLabel;

    private XYChart.Series series1; // = dataverzameling

    @FXML
    private LineChart lineChart;

    @FXML
    public void initialize(){

        //zodat alle punten er altijd op zullen graken
        // lineChart.getXAxis().setTickLength(1000); // 1 seconde
        //lineChart.getYAxis().setTickLength(20);
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();

        xAxis.setLabel("time (ms)");
        yAxis.setLabel("distance");
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);// schaalt zodat alle punten zichtbaar zijn

        //css file inladen -> in de fxml zelf

        //lineChart.getYAxis().setAutoRanging(true);

        series1 = new XYChart.Series();
        series1.setName("Solutions");

        lineChart.getData().add(series1);






    }

    public void addPunt(Integer currentTime, Integer aantalKm){
        series1.getData().add(new XYChart.Data(currentTime,aantalKm));
    }






}