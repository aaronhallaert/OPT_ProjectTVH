package TVH.GUI;

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
        graphDriver.start();

        Runnable clockUpdater = () -> {
            while (true){
                try {
                    graphDriver.updateClock(System.currentTimeMillis() - startTime);
                    synchronized (this) {
                        wait(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(clockUpdater).start();


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
