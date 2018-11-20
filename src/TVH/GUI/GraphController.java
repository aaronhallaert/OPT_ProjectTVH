package TVH.GUI;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;

public class GraphController {

    long start;
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @FXML
    Label timeLabel;
    @FXML
    Label remainingTimeLabel;


    @FXML
    private Label solutionLabel;

    private XYChart.Series series1; // = dataverzameling

    @FXML
    private LineChart lineChart;

    @FXML
    public void initialize(){
        start = System.currentTimeMillis();
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


        long elapsed = System.currentTimeMillis() - start;

        long elapsedSec = elapsed/1000;
        long secDisp = elapsedSec%60;


        long elapsedMin = elapsedSec/60;
        long minDisp = elapsedMin%60;


        long elapsedHours = elapsedMin/60;
        long hoursDisp = elapsedHours%24;


        timeLabel.setText(hoursDisp + ":" + minDisp + ":" + secDisp);

        series1.getData().add(new XYChart.Data(currentTime,aantalKm));
    }






}