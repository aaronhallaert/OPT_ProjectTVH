package TVH.Entities.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * De klasse Location bevat alle punten op de kaart, met hun ID, lat & lon etc.
 * Het bevat een Map met alle edges waarbij het vertrekpunt deze locatie is.
 * Het bevat ook een gesorteerde lijst van deze edges op basis van afstand.
 */
public class Location {
    private final int locationID;
    private final double latitude;
    private final double longitude;
    private final String name;
    //snelle manier om van de ene location naar de andere te gaan;
    private final HashMap<Location ,Edge> edgeMap;
    private final LinkedList<Edge> sortedEdgeList;

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
        sortedEdgeList.add(edge);
        sortedEdgeList.sort(Comparator.comparing(Edge::getDistance));
    }

    public int distanceTo(Location l){
        return edgeMap.get(l).getDistance();
    }

    public int timeTo(Location l){
        return edgeMap.get(l).getTime();
    }

    public int getLocationID() {
        return locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public HashMap<Location, Edge> getEdgeMap() {
        return edgeMap;
    }

    public LinkedList<Edge> getSortedEdgeList() {
        return sortedEdgeList;
    }

    @Override
    public int hashCode() {
        return locationID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return locationID == location.locationID;
    }

    @Override
    public String toString() {
        return locationID +": "+name;
    }
}
