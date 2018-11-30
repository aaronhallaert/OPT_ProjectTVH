package TVH.GUI;

import TVH.Main;
import TVH.Solution;
import com.google.common.graph.Graph;
import javafx.application.Platform;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Listener {
    public static Listener instance = new Listener();
    int lowestDistance;
    long startTime;
    GraphDriver graphDriver;

    private Listener(){
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
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(clockUpdater).start();


    }

    public static Listener getInstance(){
        return instance;
    }

    public void newSolutionFound(Solution s){
        //if(s.getTotalDistance() < lowestDistance){
        if(true){
            lowestDistance = s.getTotalDistance();
            int timestamp = (int)(System.currentTimeMillis() - startTime);
            Platform.runLater(() -> {
                graphDriver.addPoint(timestamp,s.getTotalDistance());
                //playEskettit();

            });
        }
    }

    public void updateTemperature(double temp){
        graphDriver.updateTemp(temp);
    }

    public static synchronized void playEskettit() {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            Main.class.getResourceAsStream("GUI/Esketit.wav"));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }





}
