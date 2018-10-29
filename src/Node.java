import java.util.*;

public class Node {

    private int nodeID;
    private double latitude;
    private double longitude;
    private LinkedList<Machine> pickupItems;
    private LinkedList<MachineType> dropOffItems;
    private HashMap<MachineType, Stack<Machine>>  machines;
    private LinkedList<Edge> edgeList;
    private boolean depot;


    public Node(int nodeID, double latitude, double longitude, boolean depot) {
        this.nodeID = nodeID;
        this.latitude = latitude;
        this.longitude = longitude;
        pickupItems = new LinkedList<>();
        dropOffItems = new LinkedList<>();
        machines= new HashMap<>();
        edgeList = new LinkedList<>();
        this.depot=depot;
    }

    public void addEdge(Edge edge){
        edgeList.add(edge);
        //Edge list moet altijd gesorteerd blijven op basis van afstand
        edgeList.sort(Comparator.comparing(Edge::getDistance));
    }

    public void addMachine(Machine m){
        Stack<Machine> stack = machines.get(m.getType());
        if(machines.get(m.getType()) != null){
            stack.push(m);
        }
        else{
            machines.put(m.getType(), new Stack<>());
            machines.get(m.getType()).push(m);
        }
    }

    public Node(int nodeID) {
        this.nodeID = nodeID;
    }

    public void setDepot(){
        this.depot=true;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LinkedList<Machine> getPickupItems() {
        return pickupItems;
    }

    public void setPickupItems(LinkedList<Machine> pickupItems) {
        this.pickupItems = pickupItems;
    }

    public LinkedList<MachineType> getDropOffItems() {
        return dropOffItems;
    }

    public void setDropOffItems(LinkedList<MachineType> dropOffItems) {
        this.dropOffItems = dropOffItems;
    }

    public HashMap<MachineType, Stack<Machine>> getMachines() {
        return machines;
    }

    public void setMachines(HashMap<MachineType, Stack<Machine>> machines) {
        this.machines = machines;
    }

    public boolean isDepot() {
        return depot;
    }

    public void setDepot(boolean depot) {
        this.depot = depot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return nodeID == node.nodeID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID);
    }
}
