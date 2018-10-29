import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;

public class Location {
    private int locationID;
    private double latitude;
    private double longitude;
    private LinkedList<Edge> edgeList;

    public Location(int locationID, double latitude, double longitude) {
        this.locationID = locationID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edgeList = new LinkedList<>();
    }

    public void addEdge(Edge edge){
        edgeList.add(edge);
        //Edge list moet altijd gesorteerd blijven op basis van afstand
        edgeList.sort(Comparator.comparing(Edge::getDistance));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return locationID == location.locationID;
    }

    @Override
    public int hashCode() {
        return locationID;
    }
}
