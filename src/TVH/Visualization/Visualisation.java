package TVH.Visualization;

import TVH.Cluster;
import TVH.Entities.Depot;
import TVH.Entities.Location;
import TVH.Entities.Truck;
import TVH.Solution;
import com.google.common.collect.HashMultimap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Visualisation extends Application {
    public static Solution solution;
    public static List<Cluster> clusters;
    public static List<Depot> depots;
    public static double minLon = Double.MAX_VALUE;
    public static double maxLon = Double.MIN_VALUE;
    public static double minLat = Double.MAX_VALUE;
    public static double maxLat = Double.MIN_VALUE;
    public static HashMap<Location, Circle> circleMap = new HashMap<>();
    public static HashMultimap<Truck, Line> lineMap = HashMultimap.create();
    public static boolean allTrucksShown = true;
    public static final int HEIGHT = 900;
    public static final int WIDTH = 1700;
    public static final int BORDER = 50;


    public static void start(Solution solution_input, List<Cluster> clusters_input, List<Depot> depots_input) {
        solution = solution_input;
        clusters = clusters_input;
        depots = depots_input;

        for(Cluster c: clusters){
            for(Location l: c.getMembers()){
                if(l.getLongitude() < minLon) minLon = l.getLongitude();
                if(l.getLongitude() > maxLon) maxLon = l.getLongitude();
                if(l.getLatitude() < minLat) minLat = l.getLatitude();
                if(l.getLatitude() > maxLat) maxLat = l.getLatitude();
            }
        }

        Application.launch();
    }

    public Visualisation() {
    }

    private static double translateLong(double longitude){
        double range = maxLon - minLon;
        double offset = longitude - minLon;
        double toreturn = WIDTH*(offset/range);
        return toreturn + BORDER;
    }
    private static double translateLat(double latitude){
        double range = maxLat - minLat;
        double offset = maxLat - latitude;
        double toreturn =  HEIGHT*(offset/range);
        return toreturn + BORDER;
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        LinkedList<Color> colorList = new LinkedList<Color>(){{
            add(Color.LIGHTGREEN);
            add(Color.LIGHTBLUE);
            add(Color.BLACK);
            add(Color.PINK);
            add(Color.ORANGE);
        }};
        for(Cluster c: clusters){
            Color color = colorList.getFirst();
            for(Location l: c.getMembers()){
                Circle circle =  new Circle(6, color);
                circle.setTranslateX(translateLong(l.getLongitude()));
                circle.setTranslateY(translateLat(l.getLatitude()));
                circleMap.put(l, circle);
                root.getChildren().add(circle);
            }
            colorList.removeFirst();

        }
        for(Depot d: depots){
            Circle circle = new Circle(6, Color.RED);
            circle.setTranslateX(translateLong(d.getLocation().getLongitude()));
            circle.setTranslateY(translateLat(d.getLocation().getLatitude()));
            circleMap.put(d.getLocation(), circle);
            root.getChildren().add(circle);
        }
        for(Truck t: solution.getTrucks()){
            if(t.getRoute().size() > 2) {
                Color color = Color.color(Math.random(), Math.random(), Math.random(), 0.5);
                for (int i = 0; i < t.getRoute().size() - 1; i++) {
                    Location start = t.getRoute().get(i).getLocation();
                    Location stop = t.getRoute().get(i + 1).getLocation();
                    Line line = new Line();
                    line.setStroke(color);
                    line.setStrokeWidth(4);
                    Circle circle1 = circleMap.get(start);
                    Circle circle2 = circleMap.get(stop);
                    line.startXProperty().bind(circle1.centerXProperty().add(circle1.translateXProperty()));
                    line.startYProperty().bind(circle1.centerYProperty().add(circle1.translateYProperty()));
                    line.endXProperty().bind(circle2.centerXProperty().add(circle2.translateXProperty()));
                    line.endYProperty().bind(circle2.centerYProperty().add(circle2.translateYProperty()));
                    line.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        showSingleTruck(root, t);
                    });

                    lineMap.put(t, line);
                    root.getChildren().add(line);
                }
            }
        }

        Scene scene = new Scene(root, WIDTH+2*BORDER, HEIGHT+2*BORDER);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public void showSingleTruck(Pane root,Truck t){
        if(allTrucksShown){
            root.getChildren().removeAll(lineMap.values());
            root.getChildren().addAll(lineMap.get(t));
            System.out.println(t);
            allTrucksShown = false;
        }
        else{
            root.getChildren().removeAll(lineMap.get(t));
            root.getChildren().addAll(lineMap.values());
            allTrucksShown = true;
        }




    }


}