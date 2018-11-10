package TVH.Entities.Truck;

import TVH.Entities.Job.Move;
import TVH.Entities.Machine;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Swap;
import TVH.Problem;
import com.google.common.collect.HashMultimap;

import java.sql.SQLOutput;
import java.util.*;

public class Route {
    Truck truck;
    LinkedList<Stop> stops;
    HashMultimap<Location, Stop> locationStopMap;
    Stop first;
    Stop last;

    public Route(Stop first, Stop last, Truck truck){
        this.truck = truck;
        this.first = first;
        this.last = last;
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
        this.first = r.first;
        this.last = r.last;
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
            if(s.isEmpty() && s != first && s != last){
                stops.remove(s);
                locationStopMap.remove(s.getLocation(), s);
                stopsRemoved = true;
            }
        }
        for(Stop s: stopsAtCollectLoc){
            s.removeFromCollect(m.getMachine());
            if(s.isEmpty() && s != first && s != last){
                stops.remove(s);
                locationStopMap.remove(s.getLocation(), s);
                stopsRemoved = true;
            }
        }
        if(stopsRemoved && optimize){
            optimizeRoute();
            if(!isFeasible()){
                System.out.println("stop");
            }
        }
    }


    public void optimizeRoute(){
        Set<Swap> swaps = new HashSet<>();
        for(int i=1; i < stops.size()-1; i++) {
            for (int j = 1; j < stops.size() - 1; j++) {
                if (i != j) {
                    swaps.add(new Swap(i, j));
                }
            }
        }

        boolean betterRouteFound = true;
        while(betterRouteFound){
            betterRouteFound = false;
            for(Swap swap: swaps){
                LinkedList<Stop> swapped = new LinkedList<>(stops);
                Collections.swap(swapped, swap.i1, swap.i2);
                Route candidate = new Route(swapped);
                if(candidate.calculateCost() < this.calculateCost()){
                    this.stops = candidate.stops;
                    betterRouteFound = true;
                }
            }
        }
    }
    public boolean isFeasible(){
        if(getOrderViolations() > 0) return false;
        if(timeCost() > 0) return false;
        if(getFillrateViolations() > 0) return false;
        return true;
    }
    public int calculateCost(){
        int timeFactor = 100;
        int distanceFactor = 1;
        int orderFactor = 1000;
        int fillrateFactor = 100;

        return distanceFactor*calculateDistance() + timeFactor*timeCost() + orderFactor*getOrderViolations() + fillrateFactor * getFillrateViolations();

    }

    public int calculateTime(){
        int totalTime = 0;
        if(stops.isEmpty()){
            System.out.println("stop");
        }
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
    public int timeCost(){
        int timeTooMuch = calculateTime() - Problem.getInstance().TRUCK_WORKING_TIME;
        if(timeTooMuch > 0){
            return timeTooMuch;
        }
        else return 0;
    }
    public int getOrderViolations(){
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
}
