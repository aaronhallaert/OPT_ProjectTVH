import java.util.ArrayList;

public class Truck {

    private int truckId;
    private ArrayList<Stop> route;
    private int totaleDuur; //van de rit
    private int totaleAfstand;
    private Node startLocation;
    private Node endLocation;

    public Truck(int truckId, Node startLocation, Node endLocation){
        this.truckId=truckId;
        this.startLocation= startLocation;
        this.endLocation=endLocation;
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

    public int getTotaleDuur() {
        return totaleDuur;
    }

    public void setTotaleDuur(int totaleDuur) {
        this.totaleDuur = totaleDuur;
    }

    public int getTotaleAfstand() {
        return totaleAfstand;
    }

    public void setTotaleAfstand(int totaleAfstand) {
        this.totaleAfstand = totaleAfstand;
    }

    public Node getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Node startLocation) {
        this.startLocation = startLocation;
    }

    public Node getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Node endLocation) {
        this.endLocation = endLocation;
    }
}
