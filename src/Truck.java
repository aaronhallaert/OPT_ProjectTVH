import java.util.*;

public class Truck {

    private int truckId;
    private LinkedList<Stop> route;
    private HashMap<Location, Stop> locationStopMap;
    private int totalTime; //van de rit
    private int totalDistance;
    private Location startLocation;
    private Location endLocation;
    private boolean used;

    public Truck(int truckId, Location startLocation, Location endLocation){
        this.truckId=truckId;
        this.startLocation= startLocation;
        this.endLocation=endLocation;

        used = false;

        route = new LinkedList<>();
        Stop startStop = new Stop(startLocation);
        Stop endStop = new Stop(endLocation);
        route.add(startStop);
        route.add(endStop);

        locationStopMap = new HashMap<>();
    }

    //Copy constructor
    public Truck(Truck t) {
        this.truckId = t.truckId;
        this.totalTime = t.totalTime;
        this.totalDistance = t.totalDistance;
        this.startLocation = t.startLocation;
        this.endLocation = t.endLocation;
        this.used = t.used;

        //Deep copy each stop in route
        this.route = new LinkedList<>();
        for(Stop s: t.route){
            route.add(new Stop(s));
        }
        //Create a new hashmap;
        this.locationStopMap = new HashMap<>();
        for(Stop s: this.route){
            locationStopMap.put(s.getLocation(), s);
        }
    }

    /**
     * This method allows to make a truck handle a certain move
     * @param m Move that has to be handled by the truck.
     * @return true if the truck can handle the move (no constraints are broken);
     */
    public boolean doMove(Move m){
        Location from = m.getFrom();
        Location to = m.getTo();
        Machine machine = m.getMachine();

        //If the origin and/or the destination of the move are not yet part of the route, it's necessary to add them.
        if(!(locationStopMap.containsKey(from) || from==startLocation)) {
            Stop newStop = new Stop(from);
            int index = findBestIndexToInsert(newStop);
            newStop.setOnTruck(route.get(index-1).getOnTruck());
            route.add(index, newStop);
            locationStopMap.put(from, newStop);
        }
        if(!(locationStopMap.containsKey(to) || to==endLocation)){
            Stop newStop = new Stop(to);
            int index = findBestIndexToInsert(newStop);
            newStop.setOnTruck(route.get(index-1).getOnTruck());
            route.add(index, newStop);
            locationStopMap.put(to, newStop);
        }

        //Search the collect en drop stops;
        Stop collectStop;
        Stop dropStop;
        if(startLocation == from) collectStop = route.getFirst();
        else collectStop = locationStopMap.get(from);

        if(endLocation == to) dropStop = route.getLast();
        else dropStop = locationStopMap.get(to);


        //Add the machine to the right stops, and check the fillrate constraint;
        collectStop.addCollectItem(machine);
        dropStop.addDropItem(machine);
        if(!recalculateOnTruck()) return false;
        /*int collectIndex = route.indexOf(collectStop);
        int dropIndex = route.indexOf(dropStop);
        for(int i = collectIndex; i < dropIndex; i++){
            if(!route.get(i).addToTruck(machine)){
                return false;
            }
        }*/
        //Total time is recalculated and checked;
        if(!recalculateTime()) return false;

        //If this part of the code is reached, it means that the truck can handle the new move without breaking any constraints;
        used = true;
        return true;
    }
    public boolean doesTruckPass(Location l){
        for(Stop s: route){
            if(s.getLocation() == l) return true;
        }
        return false;
    }

    public boolean recalculateTime(){
        totalTime = 0;
        Stop prevStop = route.get(0);
        //Time to drive to each stop
        for (int i = 1; i < route.size(); i++) {
            totalTime += prevStop.getLocation().timeTo(route.get(i).getLocation());
            prevStop = route.get(i);
        }
        //Time spend at each stop to load/unload
        for(Stop s: route){
            totalTime += s.getTimeSpend();
        }
        return totalTime < Problem.TRUCK_WORKING_TIME;
    }
    public boolean recalculateOnTruck(){
        LinkedList<Machine> onTruck = new LinkedList<>();
        for(Stop s: route){
            onTruck.addAll(s.getCollectItems());
            onTruck.removeAll(s.getDropItems());
            s.setOnTruck(new LinkedList<>(onTruck));
            if (!s.calculateFillRate()){
                return false;
            }
        }
        return true;
    }

    /**
     * This method searches the closest stop to a new stop. This method is used to determined where a stop is inserted in the route;
     * @param toInsert
     * @return
     */
    public int findBestIndexToInsert(Stop toInsert){
        int minAddedDistance = Integer.MAX_VALUE;
        int index = -1;
        Location X = toInsert.getLocation();
        for (int i = 0; i < route.size()-1; i++) {
            Location A = route.get(i).getLocation();
            Location B = route.get(i+1).getLocation();
            int oldDistance = A.distanceTo(B);
            int newDistance = A.distanceTo(X) + X.distanceTo(B);
            if((newDistance - oldDistance) < minAddedDistance) {
                minAddedDistance = newDistance - oldDistance;
                index = i + 1;
            }
        }
        return index;
    }

    public int getTimeNewInsert(Stop s, int index){
        /**
         * Get the total time driven when a new stop is inserted;
         * A and B represent the 2 original stops.
         * X represents the new stop that is inserted in between A and B.
         * before: A-B, after: A-X-B
         */
        int time = totalTime;
        Location A = route.get(index-1).getLocation();
        Location B = route.get(index).getLocation();
        Location X = s.getLocation();
        time -= A.timeTo(B);
        time += A.timeTo(X);
        time += X.timeTo(B);
        return time;

    }
    public int getDistanceNewInsert(Stop s, int index){
        /**
         * Get the total distance driven when a new stop is inserted;
         * A and B represent the 2 original stops.
         * X represents the new stop that is inserted in between A and B.
         * before: A-B, after: A-X-B
         */
        int distance = totalDistance;
        Location A = route.get(index-1).getLocation();
        Location B = route.get(index).getLocation();
        Location X = s.getLocation();
        distance -= A.distanceTo(B);
        distance += A.distanceTo(X);
        distance += X.distanceTo(B);
        return distance;
    }

    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalDistance() {
        //TIJDELIJK GRT

        totalDistance = 0;
        for (int i = 0; i < route.size()-1; i++) {
            Location A = route.get(i).getLocation();
            Location B = route.get(i+1).getLocation();
            totalDistance += A.distanceTo(B);
        }
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
