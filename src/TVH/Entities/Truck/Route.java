package TVH.Entities.Truck;

import TVH.Entities.Job.Move;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;
import TVH.Problem;

import java.io.Serializable;
import java.util.*;

/**
 * De klasse Route geeft aan langs waar een truck rijdt.
 * Een route kan zijn eigen kosf bepalen en zichzelf optimaliseren
 * Het is een dynamiche klasse
 */

public class Route implements Serializable {
    private LinkedList<Stop> stops;
    private int totalDistance = 0;
    private int cost = 0;
    private int orderViolations = 0;
    private int timeViolations = 0;
    private int fillRateViolations = 0;
    private int avgStopsOnTruck = 0;
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
    }

    public Route(LinkedList<Stop> stops) {
        this.stops = stops;
    }

    //Copy constructor
    public Route(Route r){
        stops = new LinkedList<>();
        for(Stop s: r.stops){
            stops.add(new Stop(s));
        }
        this.totalDistance = r.totalDistance;
        this.cost = r.cost;
        this.orderViolations = r.orderViolations;
        this.timeViolations = r.timeViolations;
        this.fillRateViolations = r.fillRateViolations;
        this.avgStopsOnTruck = r.avgStopsOnTruck;
        this.totalTime = r.totalTime;
        this.hash1 = r.hash1;
        this.hash2 = r.hash2;
        this.hash3 = r.hash3;
        this.hash4 = r.hash4;
        this.hash5 = r.hash5;
        this.hash6 = r.hash6;
        this.hash7 = r.hash7;
    }

    /**
     * Deze methode probeert een bepaalde Move toe te voegen aan de route.
     * @param m Move die moet toegevoegd worden.
     * @return true als de move succesvol is toegevoegd (zonder constraints te breken)
     */
    public boolean addMove(Move m){
        //Backups nemen van stops en locationstopmap
        LinkedList<Stop> previousOrder = new LinkedList<>(stops);

        //De kans bestaat dat de truck al langs drop of collect locatie van de move passeert:
        Stop collectStop = null;
        Stop dropStop = null;
        //Collect stop opzoeken
        boolean collectNotFound = true;
        for(Stop s: stops){
            if(s.getLocation() == m.getCollect() && collectNotFound){
                collectNotFound = false;
                collectStop = s;
            }
        }
        //Indien deze nog niet in de route zit, hem toevoegen.
        if(collectStop == null) {
            collectStop = new Stop(m.getCollect());
            stops.add(stops.size()-1,collectStop);
        }
        //Drop stop toevoegen
        for(Stop s: stops) {
            if (s.getLocation() == m.getDrop()) {
                dropStop = s;
            }
        }
        //Indien deze nog niet in de route zit, hem toevoegen.
        if(dropStop == null) {
            dropStop = new Stop(m.getDrop());
            stops.add(stops.size()-1,dropStop);
        }

        //De machine toevoegen aan de beide stops (collecten en droppen)
        collectStop.addToCollect(m.getMachine());
        dropStop.addToDrop(m.getMachine());

        optimizeRoute();
        //De optimalisatie geeft geen garantie van feasibility, er moet dus nog gekeken worden als de rit feasible is
        if(!isFeasible()){
            //Zo niet herstellen, verwijderen we de move terug en zetten we de volgorde terug naar de originele
            removeMove(m, false);
            stops = previousOrder;

            return false;
        }

        return true;
    }

    /**
     * Deze methode verwijdert een move terug van de route
     * @param m
     * @param optimize
     */

    public void removeMove(Move m, boolean optimize){
        Stop collectStop = null;
        Stop dropStop = null;

        //Respectieve stops waar de move effect op had opzoeken en de machine verwijderen van collect en drop
        for(Stop s: stops){
            if(s.getLocation() == m.getCollect()){
                collectStop = s;
                s.removeFromCollect(m.getMachine());

            }
            if(s.getLocation() == m.getDrop()){
                dropStop = s;
                s.removeFromDrop(m.getMachine());

            }
        }
        //Als er bij een bepaalde stop niets meer gedaan moet worden kan deze verwijderd worden uit de route behalve
        //als het de eerste of laatste stop i
        if(collectStop.isEmpty() && collectStop != stops.getFirst() && collectStop != stops.getLast()){
            stops.remove(collectStop);
        }
        if(dropStop.isEmpty() && dropStop != stops.getFirst() && dropStop != stops.getLast()){
            stops.remove(dropStop);
        }

        if(optimize){
            optimizeRoute();
        }
    }

    /**
     * Deze methode optimaliseert de route met behulp van 2-opt swaps
     */
    private void optimizeRoute(){
        boolean betterRouteFound = true;
        //int randomSwapsDone = 0;
        Route localBest = new Route(new LinkedList<>(stops));
        Route overallBest = new Route(new LinkedList<>(stops));
        while(betterRouteFound){
            betterRouteFound = false;
            //Elke mogelijk combinatie van swap overlopen
            for(int i=1; i < stops.size()-1; i++){
                for(int j=1; j < stops.size()-1; j++){
                    //Enkel als i kleiner is dan j is het nuttig op de swap uit te voeren
                    if(i < j) {
                        Route candidate = new Route(twoOptSwap(i, j, localBest.stops));
                        //kijken als de nieuwe route beter is
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
            //TODO: Dit hier juist implementeren
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

    /**
     * Effectieve 2opt swap methode
     * @param firstCut waar de 1ste cut gemaakt wordt
     * @param secondCut waar de 2de cut gemaakt wordt
     * @param stopList de lijst die geswapped moet worden
     * @return geswapte lijst
     */
    public static LinkedList<Stop> twoOptSwap(int firstCut, int secondCut, LinkedList<Stop> stopList){
        //TODO: korter schrijven
        LinkedList<Stop> candidate = new LinkedList<>();
        int size = stopList.size();

        //in order;
        for(int i=0; i < firstCut; i++){
            candidate.add(stopList.get(i));
        }
        //reversed;
        int count = 0;
        for(int i=firstCut; i < secondCut+1; i++){
            candidate.add(stopList.get(secondCut - count));
            count++;
        }
        //in order;
        for(int i=secondCut+1; i < stopList.size(); i++){
            candidate.add(stopList.get(i));
        }
        return candidate;

    }

    public Location getGeographicalCenter(){
        double totallat = 0;
        double totallon = 0;
        for(Stop s: stops){
            totallat += s.getLocation().getLatitude();
            totallon += s.getLocation().getLongitude();
        }
        return new Location(-1, null, totallat/stops.size(), totallon/stops.size());
    }

    /**
     * Deze methode checkt de feasibility van de route
     * @return true als de route feasible is
     */
    public boolean isFeasible(){
        if(getOrderViolations() > 0) return false;
        if(getTimeViolations() > 0) return false;
        if(getFillRateViolations() > 0) return false;
        return true;
    }

    private int calculateCost(){
            int timeFactor = 100;
            int orderFactor = 1000;
            int fillrateViolationFactor = 1000;
            int distanceFactor = 1;
            int avgFillRateFactor = 0;

            return distanceFactor * getTotalDistance()
                    //+ avgFillRateFactor * getAvgStopsOnTruck()
                    + timeFactor * getTimeViolations()
                    + orderFactor * getOrderViolations()
                    + fillrateViolationFactor * getFillRateViolations();

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
    private int calculateAvgStopsOnTruck(){
        List<Machine> onTruck = new LinkedList<>();
        HashMap<Machine, Integer> nStopsOnTruck = new HashMap<>();
        for(Stop s: stops){
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            for(Machine m: onTruck){
                if(nStopsOnTruck.containsKey(m)){
                    nStopsOnTruck.put(m, nStopsOnTruck.get(m)+1);
                }
                else{
                    nStopsOnTruck.put(m, 1);
                }
            }

        }
        if(nStopsOnTruck.size() == 0) return 0;

        int avgStopsOnTruck = 0;
        for(Integer i : nStopsOnTruck.values()){
            avgStopsOnTruck += i;
        }
        return avgStopsOnTruck/nStopsOnTruck.size();

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
        //int hash = Objects.hashCode(stops);
        //if(hash != hash1){
            totalDistance = calculateDistance();
        //    hash1 = hash;
        //}
        return totalDistance;
    }

    public int getCost() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash2){
            cost = calculateCost();
        //    hash2 = hash;
        //}
        return cost;
    }

    public int getOrderViolations() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash3){
            orderViolations = calculateOrderViolations();
        //    hash3 = hash;
        //}
        return orderViolations;
    }

    public int getTimeViolations() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash4){
            timeViolations = calculateTimeViolations();
        //    hash4 = hash;
        //}
        return timeViolations;
    }

    public int getFillRateViolations() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash5){
            fillRateViolations = calculateFillRateViolations();
        //    hash5 = hash;
        //}
        return fillRateViolations;
    }

    public int getAvgStopsOnTruck() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash6){
            avgStopsOnTruck = calculateAvgStopsOnTruck();
        //    hash6 = hash;
        //}
        return avgStopsOnTruck;
    }

    public int getTotalTime() {
        //int hash = Objects.hashCode(stops);
        //if(hash != hash7){
            totalTime = calculateTime();
        //    hash7 = hash;
        //}
        return totalTime;
    }
}
