public class Edge {
    Node from;
    Node to;
    int time;
    int distance;

    public Edge(Node from, Node to, int time, int distance) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.distance = distance;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
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
