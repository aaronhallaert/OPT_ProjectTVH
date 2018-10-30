import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Location {
    private int locationID;
    private double latitude;
    private double longitude;
    private String name;
    private HashMap<Location ,Edge> edgeMap;
    private LinkedList<Edge> sortedEdgeList;

    public Location(int locationID,String name ,double latitude, double longitude) {
        this.locationID = locationID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.edgeMap = new HashMap<>();
        this.sortedEdgeList = new LinkedList<>();
    }

    public void addEdge(Edge edge){
        edgeMap.put(edge.getTo(), edge);
        //Edge list moet altijd gesorteerd blijven op basis van afstand
        sortedEdgeList.add(edge);
        sortedEdgeList.sort(Comparator.comparing(Edge::getDistance));
    }

    public int distanceTo(Location l){
        return edgeMap.get(l).getDistance();
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
