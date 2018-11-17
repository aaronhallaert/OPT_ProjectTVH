package TVH.Gui;

import TVH.Solution;
import javafx.application.Platform;

public class SolutionListener {
    public static SolutionListener instance = new SolutionListener();
    int lowestDistance;
    long startTime;
    GraphDriver graphDriver;

    private SolutionListener(){
        lowestDistance = Integer.MAX_VALUE;
        startTime = System.currentTimeMillis();
        graphDriver = new GraphDriver();
        //GUI thread aanmaken en laten runnen
        graphDriver.run();
    }

    public static SolutionListener getInstance(){
        return instance;
    }

    public void newSolutionFound(Solution s){
        if(s.getTotalDistance() < lowestDistance){
            lowestDistance = s.getTotalDistance();
            int timestamp = (int)(System.currentTimeMillis() - startTime);
            Platform.runLater(() -> {
                graphDriver.addPoint(timestamp,s.getTotalDistance());
            });
        }
    }
}
