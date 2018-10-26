import java.util.ArrayList;

public class Truck {

    private int truckId;
    private ArrayList<Stop> route;
    private int totaleDuur; //van de rit
    private int totaleAfstand;
    private int startLocationId;
    private int endLocationId;

    public Truck(int truckId, int startLocationId, int endLocationId){
        this.truckId=truckId;
        this.startLocationId= startLocationId;
        this.endLocationId=endLocationId;
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

    public int getStartLocationId() {
        return startLocationId;
    }

    public void setStartLocationId(int startLocationId) {
        this.startLocationId = startLocationId;
    }

    public int getEndLocationId() {
        return endLocationId;
    }

    public void setEndLocationId(int endLocationId) {
        this.endLocationId = endLocationId;
    }


}
