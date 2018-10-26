import java.util.LinkedList;

public class Node {

    private int nodeID;
    private double latitude;
    private double longitude;
    private LinkedList<Machine> pickupItems;
    private LinkedList<Machine> dropOffItems;

    public Node(int nodeID, double latitude, double longitude) {
        this.nodeID = nodeID;
        this.latitude = latitude;
        this.longitude = longitude;
        pickupItems = new LinkedList<>();
        dropOffItems = new LinkedList<>();
    }
}
