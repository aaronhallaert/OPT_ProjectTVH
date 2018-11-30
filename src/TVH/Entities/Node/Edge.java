package TVH.Entities.Node;


/**
 * De klasse Edge geeft info over de verbindingen tussen Locations. Voor elke locatie paar bevat het een time en distance
 * waarde om te reizen van "from" naar "to". Dit is een statische klasse
 */
public class Edge {
    private final Location from;
    private final Location to;
    private final int time;
    private final int distance;

    public Edge(Location from, Location to, int time, int distance) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.distance = distance;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }

}
