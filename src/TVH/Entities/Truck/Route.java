package TVH.Entities.Truck;

import TVH.Entities.Job.Move;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;
import TVH.Problem;
import com.google.common.collect.HashMultimap;

import java.util.*;

//Dynamische klasse
public class Route {
    private LinkedList<Stop> stops;
    private HashMultimap<Location, Stop> locationStopMap;
    private int totalDistance = 0;
    private int cost = 0;
    private int orderViolations = 0;
    private int timeViolations = 0;
    private int fillRateViolations = 0;
    private int fillrateAbove65 = 0;
    private int totalTime = 0;

    private int hash1 = 0;
    private int hash2 = 0;
    private int hash3 = 0;
    private int hash4 = 0;
    private int hash5 = 0;
    private int hash6 = 0;
    private int hash7 = 0;


    public Route(Stop first, Stop last, Truck truck){
        stops = new LinkedList<>();
        stops.add(first);
        stops.add(last);
        locationStopMap = HashMultimap.create();
        locationStopMap.put(first.getLocation(), first);
        locationStopMap.put(last.getLocation(), last);
    }

    public Route(LinkedList<Stop> stops) {
        this.stops = stops;
    }

    //Copy constructor
    public Route(Route r){
        stops = new LinkedList<>();
        locationStopMap = HashMultimap.create();
        for(Stop s: r.stops){
            Stop newStop = new Stop(s);
            stops.add(newStop);
            locationStopMap.put(newStop.getLocation(), newStop);
        }
    }
    public boolean addMove(Move m){
        //Backups nemen van stops en locationstopmap
        LinkedList<Stop> previousOrder = new LinkedList<>(stops);
        Stop collectStop = null;
        Stop dropStop = null;
        //Collect stop opzoeken
        if(locationStopMap.get(m.getCollect()).size() > 0){
            int index = Integer.MAX_VALUE;
            for(Stop s: locationStopMap.get(m.getCollect())){
                if(stops.indexOf(s) < index){
                    index = stops.indexOf(s);
                    collectStop = s;
                }
            }
        }
        //Indien deze nog niet in de route zit, hem toevoegen.
        else {
            collectStop = new Stop(m.getCollect());
            stops.add(stops.size()-1,collectStop);
            locationStopMap.put(m.getCollect(), collectStop);
        }
        if(locationStopMap.get(m.getDrop()).size() > 0){
            int index = -1;
            for(Stop s: locationStopMap.get(m.getDrop())){
                if(stops.indexOf(s) > index){
                    index = stops.indexOf(s);
                    dropStop = s;
                }
            }
        }
        else {
            dropStop = new Stop(m.getDrop());
            stops.add(stops.size()-1,dropStop);
            locationStopMap.put(m.getDrop(), dropStop);
        }

        collectStop.addToCollect(m.getMachine());
        dropStop.addToDrop(m.getMachine());
        optimizeRoute();
        if(!isFeasible()){
            removeMove(m, false);
            stops = previousOrder;

            return false;
        }
        return true;
    }
    public void removeMove(Move m, boolean optimize){
        boolean stopsRemoved = false;
        List<Stop> stopsAtDropLoc = new ArrayList<>(locationStopMap.get(m.getDrop()));
        List<Stop> stopsAtCollectLoc = new ArrayList<>(locationStopMap.get(m.getCollect()));
        for(Stop s: stopsAtDropLoc){
            s.removeFromDrop(m.getMachine());
            if(s.isEmpty() && s != stops.getFirst() && s != stops.getLast()){
                stops.remove(s);
                locationStopMap.remove(s.getLocation(), s);
                stopsRemoved = true;
            }
        }
        for(Stop s: stopsAtCollectLoc){
            s.removeFromCollect(m.getMachine());
            if(s.isEmpty() && s != stops.getFirst() && s != stops.getLast()){
                stops.remove(s);
                locationStopMap.remove(s.getLocation(), s);
                stopsRemoved = true;
            }
        }
        if(stopsRemoved && optimize){
            optimizeRoute();
        }
    }

    private void optimizeRoute(){
        boolean betterRouteFound = true;
        int randomSwapsDone = 0;
        Route localBest = new Route(new LinkedList<>(stops));
        Route overallBest = new Route(new LinkedList<>(stops));
        while(betterRouteFound){
            betterRouteFound = false;
            for(int i=1; i < stops.size()-1; i++){
                for(int j=1; j < stops.size()-1; j++){
                    if(i != j) {
                        Route candidate = new Route(twoOptSwap(i, j, localBest.stops));
                        if (candidate.getCost() < localBest.getCost()) {
                            localBest.setStops(new LinkedList<>(candidate.stops));
                            betterRouteFound = true;
                            if(candidate.getCost() < overallBest.getCost()){
                                overallBest.setStops(new LinkedList<>(candidate.stops));
                            }
                        }
                    }
                }
            }
            //TODO: das hier echt raar gedrag whi;
            /*if(!betterRouteFound && randomSwapsDone < ((stops.size()-2)/5)){
                int index1 = (int) (Math.random() * ((stops.size()-1) - 1)) + 1;
                int index2 = (int) (Math.random() * ((stops.size()-1) - 1)) + 1;
                LinkedList<Stop> swappinge = new LinkedList<>(localBest.stops);
                Collections.swap(swappinge, index1, index2);
                localBest.stops = new LinkedList<>(swappinge);
                betterRouteFound = true;
                randomSwapsDone++;
            }*/
        }
        stops = new LinkedList<>(overallBest.getStops());
    }
    public static LinkedList<Stop> twoOptSwap(int i1, int i2, LinkedList<Stop> stopList){
        int beginCut = Math.min(i1, i2);
        int endCut = Math.max(i1, i2);
        LinkedList<Stop> candidate = new LinkedList<>();
        int size = stopList.size();

        //in order;
        for(int i=0; i < beginCut; i++){
            candidate.add(stopList.get(i));
        }
        //reversed;
        int count = 0;
        for(int i=beginCut; i < endCut+1; i++){
            candidate.add(stopList.get(endCut - count));
            count++;
        }
        //in order;
        for(int i=endCut+1; i < stopList.size(); i++){
            candidate.add(stopList.get(i));
        }
        return candidate;

    }
    public boolean isFeasible(){
        if(getOrderViolations() > 0) return false;
        if(getTimeViolations() > 0) return false;
        if(getFillRateViolations() > 0) return false;
        return true;
    }
    /*private void recalculateAll(){
        totalDistance = calculateDistance();
        totalTime = calculateTime();
        fillrateAbove65 = calculateFillrateAbove65();
        timeViolations = calculateTimeViolations();
        orderViolations = calculateOrderViolations();
        fillRateViolations = calculateFillRateViolations();
        cost = calculateCost();
    }*/
    private int calculateCost(){
            int timeFactor = 100;
            int orderFactor = 1000;
            int fillrateViolationFactor = 100;
            int distanceFactor = 1;
            int avgFillRateFactor = 0;

            int totalCost = distanceFactor * getTotalDistance()
                    + avgFillRateFactor * getFillrateAbove65()
                    + timeFactor * getTimeViolations()
                    + orderFactor * getOrderViolations()
                    + fillrateViolationFactor * getFillRateViolations();
            return totalCost;

    }

    private int calculateTime(){
        int totalTime = 0;
        Stop prevStop = stops.get(0);
        //Time to drive to each stop
        for (int i = 1; i < stops.size(); i++) {
            totalTime += prevStop.getLocation().timeTo(stops.get(i).getLocation());
            prevStop = stops.get(i);
        }
        //Time spend at each stop to load/unload
        for(Stop s: stops){
            totalTime += s.getTimeSpend();
        }
        return totalTime;
    }
    private int calculateTimeViolations(){
        int timeTooMuch = calculateTime() - Problem.getInstance().TRUCK_WORKING_TIME;
        if(timeTooMuch > 0){
            return timeTooMuch;
        }
        else return 0;
    }
    private int calculateOrderViolations(){
        List<Machine> onTruck = new LinkedList<>();
        int orderViolations = 0;
        for(Stop s: stops){
            onTruck.addAll(s.getCollect());
            for(Machine m: s.getDrop()){
                if(!onTruck.remove(m)){
                    //Een item van de truck halen die er niet opzit!
                    orderViolations++;
                }
            }
        }
        return orderViolations;
    }
    private int calculateFillRateViolations(){
        List<Machine> onTruck = new LinkedList<>();
        int fillRateViolations = 0;
        for(Stop s: stops){
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            int fillRate = calculateFillRate(onTruck);
            if(fillRate > Problem.getInstance().TRUCK_CAPACITY){
                fillRateViolations += 100 + fillRate - Problem.getInstance().TRUCK_CAPACITY;
            }
        }
        return fillRateViolations;
    }
    private int calculateFillRate(List<Machine> machines){
        int fillrate = 0;
        for(Machine m: machines){
            fillrate += m.getType().getVolume();
        }
        return fillrate;
    }
    private int calculateFillrateAbove65(){
        List<Machine> onTruck = new LinkedList<>();
        int timesOver65 = 0;
        for(Stop s: stops){
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            if(calculateFillRate(onTruck) > 65){
                timesOver65++;
            };

        }
        return timesOver65;

    }
    private int calculateDistance() {
        int totalDistance = 0;
        for (int i = 0; i < stops.size() - 1; i++) {
            Location A = stops.get(i).getLocation();
            Location B = stops.get(i + 1).getLocation();
            totalDistance += A.distanceTo(B);
        }
        return totalDistance;
    }

    public LinkedList<Stop> getStops() {
        return stops;
    }

    public void setStops(LinkedList<Stop> stops) {
        this.stops = stops;
    }

    public int getTotalDistance() {
        int hash = Objects.hash(stops);
        if(hash != hash1){
            totalDistance = calculateDistance();
            hash1 = hash;
        }
        return totalDistance;
    }

    public int getCost() {
        int hash = Objects.hash(stops);
        if(hash != hash2){
            cost = calculateCost();
            hash2 = hash;
        }
        return cost;
    }

    public int getOrderViolations() {
        int hash = Objects.hash(stops);
        if(hash != hash3){
            orderViolations = calculateOrderViolations();
            hash3 = hash;
        }
        return orderViolations;
    }

    public int getTimeViolations() {
        int hash = Objects.hash(stops);
        if(hash != hash4){
            timeViolations = calculateTimeViolations();
            hash4 = hash;
        }
        return timeViolations;
    }

    public int getFillRateViolations() {
        int hash = Objects.hash(stops);
        if(hash != hash5){
            fillRateViolations = calculateFillRateViolations();
            hash5 = hash;
        }
        return fillRateViolations;
    }

    public int getFillrateAbove65() {
        int hash = Objects.hash(stops);
        if(hash != hash6){
            fillrateAbove65 = calculateFillrateAbove65();
            hash6 = hash;
        }
        return fillrateAbove65;
    }

    public int getTotalTime() {
        int hash = Objects.hash(stops);
        if(hash != hash7){
            totalTime = calculateTime();
            hash7 = hash;
        }
        return totalTime;
    }
}
