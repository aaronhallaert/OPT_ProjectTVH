package TVH.Entities.Truck;

import TVH.Entities.Job.Move;
import TVH.Entities.Machine;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Swap;
import TVH.Problem;
import com.google.common.collect.HashMultimap;

import java.util.*;

public class Route {
    Truck truck;
    LinkedList<Stop> stops;
    HashMultimap<Location, Stop> locationStopMap;

    public Route(Stop first, Stop last, Truck truck){
        this.truck = truck;
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
        this.truck = r.truck;
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
        if(locationStopMap.get(m.getCollect()).size() > 0){
            int index = Integer.MAX_VALUE;
            for(Stop s: locationStopMap.get(m.getCollect())){
                if(stops.indexOf(s) < index){
                    index = stops.indexOf(s);
                    collectStop = s;
                }
            }
        }
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


    public void optimizeRoute(){
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
                        if (candidate.calculateCost() < localBest.calculateCost()) {
                            localBest.stops = new LinkedList<>(candidate.stops);
                            betterRouteFound = true;
                            if(candidate.calculateCost() < overallBest.calculateCost()){
                                overallBest.stops = new LinkedList<>(candidate.stops);
                            }
                        }
                    }
                }
            }
            /*if(!betterRouteFound && randomSwapsDone < ((stops.size()-2)/5)){
                int index1 = (int) (Math.random() * ((stops.size()-1) - 1)) + 1;
                int index2 = (int) (Math.random() * ((stops.size()-1) - 1)) + 1;
                //int index1 = 1;
                //int index2 = 5;
                Collections.swap(localBest.stops, index1, index2);
                betterRouteFound = true;
                randomSwapsDone++;
            }*/
        }
        stops = overallBest.stops;
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
        if(orderViolation() > 0) return false;
        if(timeViolation() > 0) return false;
        if(getFillrateViolations() > 0) return false;
        return true;
    }
    public int calculateCost(){
        int timeFactor = 100;
        int distanceFactor = 1;
        int orderFactor = 1000;
        int fillrateViolationFactor = 100;
        int avgFillRateFactor = 3;

        int totalcost = distanceFactor*calculateDistance()
                        //+ avgFillRateFactor * calculateAvgFillRate()
                        + timeFactor*timeViolation()
                        + orderFactor*orderViolation()
                        + fillrateViolationFactor*getFillrateViolations();
        return totalcost;

    }

    public int calculateTime(){
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
    public int timeViolation(){
        int timeTooMuch = calculateTime() - Problem.getInstance().TRUCK_WORKING_TIME;
        if(timeTooMuch > 0){
            return timeTooMuch;
        }
        else return 0;
    }
    public int orderViolation(){
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
    public int getFillrateViolations(){
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
    public int calculateFillRate(List<Machine> machines){
        int fillrate = 0;
        for(Machine m: machines){
            fillrate += m.getType().getVolume();
        }
        return fillrate;
    }
    public int calculateAvgFillRate(){
        List<Machine> onTruck = new LinkedList<>();
        int fillRate = 0;
        for(Stop s: stops){
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            fillRate += calculateFillRate(onTruck);

        }
        return fillRate/stops.size();
    }
    public int calculateDistance() {
        //TODO:Efficienter maken;

        int totalDistance = 0;
        for (int i = 0; i < stops.size()-1; i++) {
            Location A = stops.get(i).getLocation();
            Location B = stops.get(i+1).getLocation();
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
}
