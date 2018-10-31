import java.util.ArrayList;

public class Truck {

    private int truckId;
    private ArrayList<Stop> route;
    private int totalTime; //van de rit
    private int totalDistance;
    private Location startLocation;
    private Location endLocation;

    public Truck(int truckId, Location startLocation, Location endLocation){
        this.truckId=truckId;
        this.startLocation= startLocation;
        this.endLocation=endLocation;
        this.route = new ArrayList<>();
    }

    //Copy constructor
    public Truck(Truck t) {
        this.truckId = t.truckId;
        this.totalTime = t.totalTime;
        this.totalDistance = t.totalDistance;
        this.startLocation = t.startLocation;
        this.endLocation = t.endLocation;

        //route kopieren
        this.route = new ArrayList<>();
        for(Stop s: t.route){
            route.add(new Stop(s));
        }

    }

    //@TODO: goeie manier vinden om bij te houden welke machines op welk moment op de truck zitten eventueel met intervaltree met laad en los node



    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
    }

    public ArrayList<Stop> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Stop> route) {
        this.route = route;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

}
