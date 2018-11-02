package TVH.Entities;

public class Edge {
    Location from;
    Location to;
    int time;
    int distance;

    public Edge(Location from, Location to, int time, int distance) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.distance = distance;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
