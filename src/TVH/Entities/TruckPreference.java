package TVH.Entities;

public class TruckPreference {

    private Truck truck;
    private int preference;

    public TruckPreference(Truck truck, Move m){
        this.truck = truck;


        Location moveTo = m.getTo();
        Location moveFrom = m.getFrom();

        int minDistanceToFrom = Integer.MAX_VALUE;
        int minDistanceToTo = Integer.MAX_VALUE;
        for(Stop s: truck.getRoute()){
            Location l = s.getLocation();
            if(l.getEdgeMap().get(m.getFrom()).distance < minDistanceToFrom){
                minDistanceToFrom = l.getEdgeMap().get(m.getFrom()).distance;
            }
            if(l.getEdgeMap().get(m.getTo()).distance < minDistanceToTo){
                minDistanceToTo = l.getEdgeMap().get(m.getTo()).distance;
            }
        }
        preference = minDistanceToFrom + minDistanceToTo;




    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }
}
