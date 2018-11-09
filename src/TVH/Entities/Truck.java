package TVH.Entities;

import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Node;
import TVH.Problem;
import com.google.common.collect.HashMultimap;
import javafx.util.Pair;

import java.util.*;

public class Truck {

    private int truckId;
    private LinkedList<Stop> route;
    private HashMultimap<Location, Stop> locationStopMap;
    private int totalTime; //van de rit
    private int totalDistance;
    private Location startLocation;
    private Location endLocation;
    private ArrayList<Job> jobsHandled;
    private HashMap<Job, Move> jobMoveMap;
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

        locationStopMap = HashMultimap.create();
        locationStopMap.put(startLocation, startStop);
        locationStopMap.put(endLocation, endStop);

        jobsHandled = new ArrayList<>();
        jobMoveMap = new HashMap<>();
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
        this.locationStopMap = HashMultimap.create();
        for(Stop s: this.route){
            locationStopMap.put(s.getLocation(), s);
        }
    }

    /**
     * This method let's a truck handle a move. It also checks if no constraints are broken by handling the new move.
     * @param j Job that needs to be handled by the truck.
     * @return true if the truck breaks no constraints while handling the new move. False if it does break a constraint;
     */
    public boolean doJob(Job j){
        HashMap<Location,Node> nodesMap = Problem.getInstance().nodesMap;
        //First step is to decide which machine to move from where to where.
        Location drop = null;
        Location collect = null;
        Machine machine = null;

        if(j instanceof DropJob) {
            DropJob dj = (DropJob) j;
            //TODO: checken als je job al niet vervuld is;
            //First step is to choose a certain Location to pickup the the machine;
            drop = dj.getDrop();
            int distanceClosestFrom = Integer.MAX_VALUE;
            for (Location l : dj.getCollect()) {
                Node node = nodesMap.get(l);
                if (node.hasMachineAvailableOfType(dj.getMachineType())) {
                    int minDistanceToRoute = Integer.MAX_VALUE;
                    for (Stop s : route) {
                        if (s.getLocation().distanceTo(l) < minDistanceToRoute) {
                            minDistanceToRoute = s.getLocation().distanceTo(l);
                        }
                    }
                    if (minDistanceToRoute < distanceClosestFrom) {
                        collect = l;
                        distanceClosestFrom = minDistanceToRoute;
                    }
                }
            }
            //Once we have the location, we define which specific machine
            for(Machine m: nodesMap.get(collect).getAvailableMachines()){
                if(m.getType() == dj.getMachineType()){
                    machine = m;
                    break;
                }
            }
        }
        else if(j instanceof CollectJob) {
            CollectJob cj = (CollectJob) j;
            //TODO: checken als de job al niet vervuld is;
            collect = cj.getCollect();

            int distanceClosestTo = Integer.MAX_VALUE;
            for(Location l: cj.getDrop()) {
                Node node = nodesMap.get(l);
                if (node.canPutMachine(cj.getMachine())) {
                    int minDistanceToRoute = Integer.MAX_VALUE;
                    for (Stop s : route) {
                        if (s.getLocation().distanceTo(l) < minDistanceToRoute) {
                            minDistanceToRoute = s.getLocation().distanceTo(l);
                        }
                    }
                    if (minDistanceToRoute < distanceClosestTo) {
                        drop = l;
                        distanceClosestTo = minDistanceToRoute;
                    }
                }
            }
            machine = cj.getMachine();
        }
        jobsHandled.add(j);
        jobMoveMap.put(j, new Move(machine, collect, drop));

        if(!locationStopMap.containsKey(collect)) {
            //In case the Truck doesn't yet pass by the collect location;
            Stop newStop = new Stop(collect);
            //Add a new stop on the optimal location


            int index = findBestIndexToInsert(newStop, 0, findHighBound(drop));

            newStop.setOnTruck(route.get(index-1).getOnTruck());
            route.add(index, newStop);
            locationStopMap.put(collect, newStop);
        }
        if(!locationStopMap.containsKey(drop)){
            //In case the Truck doesn't yet pass by Job.to.
            Stop newStop = new Stop(drop);
            //Add a new stop on the optimal location

            int index = findBestIndexToInsert(newStop, findLowBound(collect), route.size()-1);

            newStop.setOnTruck(route.get(index-1).getOnTruck());
            route.add(index, newStop);
            locationStopMap.put(drop, newStop);
        }

        //Search the collect en drop stops;
        List<Stop> collectAndDropStops = getBestCollectAndDropLocation(locationStopMap.get(collect), locationStopMap.get(drop));
        Stop collectStop = collectAndDropStops.get(0);
        Stop dropStop = collectAndDropStops.get(1);

        if(collectStop == null || dropStop == null){
            //Dit kan enkel het geval zijn als de nodes er al inzaten van een andere move
            //en de dropstop voor de collect komt;
            return false;
        };

        //Add the machine to the right stops, and check the fillrate constraint;
        collectStop.addCollectItem(machine);
        dropStop.addDropItem(machine);
        if(!recalculateOnTruck()) {
            return false;
        }

        //Total time is recalculated and checked;
        if(!recalculateTime()) {
            return false;
        }

        //If this part of the code is reached, it means that the truck can handle the new move without breaking any constraints;
        used = true;

        //De move registreren bij de clients/depots;
        Node collectNode = nodesMap.get(collect);
        Node dropNode = nodesMap.get(drop);
        collectNode.takeMachine(machine);
        dropNode.putMachine(machine);

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
        return totalTime <= Problem.getInstance().TRUCK_WORKING_TIME;
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

    public int getDistanceToLocation(Location l){
        int minDistance = Integer.MAX_VALUE;
        for(Stop s: route){
            Location stopLoc = s.getLocation();
            if(l.distanceTo(stopLoc) < minDistance){
                minDistance = l.distanceTo(stopLoc);
            }
        }
        return minDistance;
    }

    /**
     * This method determines where a new stop should be inserted in the route.
     * It searches the place where inserting the new Stop adds the least amount of extra distance.
     * @param toInsert new Stop
     * @return index of where the new Stop should be inserted in the route.
     */
    public int findBestIndexToInsert(Stop toInsert, int lowBound, int highBound){
        int minAddedDistance = Integer.MAX_VALUE;
        int index = -1;
        Location X = toInsert.getLocation();
        for (int i = lowBound; i < highBound; i++) {
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

    private int findLowBound(Location l){
        int i = 0;
        int lowBound = -1;
        for(Stop s: route){
            if(s.getLocation() == l){
                lowBound = i;
                break;
            }
            i++;
        }
        if(lowBound == -1) lowBound = 0;

        return lowBound;

    }
    private int findHighBound(Location l){
        int i = 0;
        int highBound = -1;
        for(Stop s: route){
            if(s.getLocation() == l){
                highBound = i;
            }
            i++;
        }
        if(highBound == -1) highBound = route.size()-1;
        return highBound;
    }

    public List<Stop> getBestCollectAndDropLocation(Set<Stop> collects, Set<Stop> drops){
        int minIndexDifference = Integer.MAX_VALUE;
        Stop collect = null;
        Stop drop = null;
        for(Stop c: collects){
            for(Stop d: drops){
                int indexDifference = route.indexOf(d) - route.indexOf(c);
                if(indexDifference > 0 && indexDifference < minIndexDifference){
                    minIndexDifference = indexDifference;
                    collect = c;
                    drop = d;
                }
            }
        }
        List<Stop> collectAndDrop = new ArrayList<>();
        collectAndDrop.add(collect);
        collectAndDrop.add(drop);
        return collectAndDrop;
    }

    /**
     * This method is used to rollback the Truck to a previous state.
     * @param t old state
     */
    public void rollBack(Truck t){
        this.truckId = t.truckId;
        this.totalTime = t.totalTime;
        this.totalDistance = t.totalDistance;
        this.startLocation = t.startLocation;
        this.endLocation = t.endLocation;
        this.used = t.used;
        this.route = t.route;
        this.locationStopMap = t.locationStopMap;
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
        //TODO:Efficienter maken;

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

    public LinkedList<Stop> getRoute() {
        return route;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Truck: "+truckId+" ("+totalDistance +"km) ("+totalTime+" min)\n");
        for(Stop s: route){
            sb.append("\t" + "Location " + s.getLocation() + ", Fillrate: "+s.getFillRate()+"%\n");
            sb.append("\t\t On truck:\n");
            for(Machine m: s.getOnTruck()){
                sb.append("\t\t\t "+m+"\n");
            }
            sb.append("\t\t Collect:\n");
            for(Machine m: s.getCollectItems()){
                sb.append("\t\t\t "+m+"\n");
            }
            sb.append("\t\t Drop:\n");
            for(Machine m: s.getDropItems()){
                sb.append("\t\t\t "+m+"\n");
            }

        }
        return sb.toString();
    }
}
