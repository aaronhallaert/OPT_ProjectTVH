import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class Node {

    private int nodeID;
    private double latitude;
    private double longitude;
    private LinkedList<Machine> pickupItems;
    private LinkedList<Machine> dropOffItems;
    private HashMap<MachineType, Stack<Machine>>  machines;
    boolean depot;


    public Node(int nodeID, double latitude, double longitude, boolean depot) {
        this.nodeID = nodeID;
        this.latitude = latitude;
        this.longitude = longitude;
        pickupItems = new LinkedList<>();
        dropOffItems = new LinkedList<>();
        machines= new HashMap<>();
        this.depot=depot;
    }

    public void setDepot(){
        this.depot=true;
    }
}
