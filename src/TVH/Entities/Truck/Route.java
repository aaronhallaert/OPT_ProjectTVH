package TVH.Entities.Truck;

import TVH.Config;
import TVH.Entities.Job.Move;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;
import TVH.Problem;

import java.util.*;

/**
 * De klasse Route geeft aan langs waar een truck rijdt.
 * Een route kan zijn eigen kost bepalen en zichzelf optimaliseren
 * Het is een dynamiche klasse
 */

public class Route {

    public static int TIME_FACTOR = 0;
    public static int ORDER_FACTOR = 0;
    public static int FILL_RATE_VIOLATIONS_FACTOR = 0;
    public static int DISTANCE_FACTOR = 0;

    private ArrayList<Stop> stops;
    private int totalDistance = 0;
    private int cost = 0;
    private int orderViolations = 0;
    private int timeViolations = 0;
    private int fillRateViolations = 0;
    private int avgStopsOnTruck = 0;
    private int totalTime = 0;

    public boolean changed;
    //private String wut;


    public Route(Stop first, Stop last, Truck truck) {
        stops = new ArrayList<>();
        stops.add(first);
        stops.add(last);
        changed = true;
    }

    public Route(ArrayList<Stop> stops) {
        this.stops = stops;
        changed = true;
    }

    public Route(Route r, boolean DeepCopyStops){
        if(DeepCopyStops) {
            this.stops = new ArrayList<>();
            for (Stop s : r.stops) {
                stops.add(new Stop(s));
            }
        }
        else{
            this.stops = new ArrayList<>(r.stops);
        }
        this.totalDistance = r.totalDistance;
        this.cost = r.cost;
        this.orderViolations = r.orderViolations;
        this.timeViolations = r.timeViolations;
        this.fillRateViolations = r.fillRateViolations;
        this.avgStopsOnTruck = r.avgStopsOnTruck;
        this.totalTime = r.totalTime;
        this.changed = r.changed;
    }

    public void loadRoute(Route r){
        this.stops = r.stops;
        this.totalDistance = r.totalDistance;
        this.cost = r.cost;
        this.orderViolations = r.orderViolations;
        this.timeViolations = r.timeViolations;
        this.fillRateViolations = r.fillRateViolations;
        this.avgStopsOnTruck = r.avgStopsOnTruck;
        this.totalTime = r.totalTime;
        this.changed = r.changed;
    }

    /**
     * Deze methode probeert een bepaalde Move toe te voegen aan de route.
     *
     * @param m Move die moet toegevoegd worden.
     * @return true als de move succesvol is toegevoegd (zonder constraints te breken)
     */
    public boolean addMove(Move m) {

        //Backups nemen van stops en locationstopmap
        Route backup = new Route(this, false);

        //2 nieuwe stops bijmaken
        Stop collectStop = new Stop(m.getCollect());
        Stop dropStop = new Stop(m.getDrop());
        collectStop.addToCollect(m.getMachine());
        dropStop.addToDrop(m.getMachine());

        //Stops inserten op beste plek
        int[] indices = insertStops(collectStop, dropStop);

        //changed = true;
        //De optimalisatie geeft geen garantie van feasibility, er moet dus nog gekeken worden als de rit feasible is
        if (!isFeasible()) {
            //Zo niet herstellen, verwijderen we de move terug en zetten we de volgorde terug naar de originele
            removeMove(m, false);
            loadRoute(backup);

            return false;
        }

        //Stops met zelfde locatie mergen met elkaar
        mergeStops(indices);

        return true;
    }

    /**
     * Deze methode verwijdert een move terug van de route
     *
     * @param m
     * @param optimize
     */

    public void removeMove(Move m, boolean optimize) {
        Stop collectStop = null;
        Stop dropStop = null;

        //Respectieve stops waar de move effect op had opzoeken en de machine verwijderen van collect en drop
        for (Stop s : stops) {
            if (s.getLocation() == m.getCollect()) {
                collectStop = s;
                s.removeFromCollect(m.getMachine());

            }
            if (s.getLocation() == m.getDrop()) {
                dropStop = s;
                s.removeFromDrop(m.getMachine());

            }
        }
        //Als er bij een bepaalde stop niets meer gedaan moet worden kan deze verwijderd worden uit de route behalve
        //als het de eerste of laatste stop i
        if(collectStop == null || dropStop == null){
            System.out.println("stop");
        }

        if (collectStop.isEmpty() && collectStop != stops.get(0) && collectStop != stops.get(stops.size()-1)) {
            stops.remove(collectStop);
        }
        if (dropStop.isEmpty() && dropStop != stops.get(0) && dropStop != stops.get(stops.size()-1)) {
            stops.remove(dropStop);
        }

        changed = true;
    }

    private int[] insertStops(Stop stop1, Stop stop2){
        int stop1Index = -1;
        int stop2Index = -1;

        int minCost = Integer.MAX_VALUE;
        Route best = null;
        for (int i = 1; i < getStops().size(); i++) {
            for (int j = 1; j < getStops().size(); j++) {
                if(i <= j) {
                    Route candidate = new Route(this, false);
                    candidate.getStops().add(i, stop1);
                    candidate.getStops().add(j+1, stop2);
                    candidate.setChanged(true);
                    if(candidate.getCost() < minCost){
                        best = candidate;
                        minCost = candidate.getCost();
                        stop1Index = i;
                        stop2Index = j+1;
                    }
                }
            }
        }
        loadRoute(best);

        return new int[]{stop1Index, stop2Index};
    }

    private void mergeStops(int [] indices){
        Stop stop1 = stops.get(indices[0]);
        Stop stop2 = stops.get(indices[1]);

        boolean stop1merged = false;
        boolean stop2merged = false;
        //Normaal zijn de nieuwe stops altijd toegevoegd voor de oude (behalve bij de eerste)
        if(stop1.getLocation() == stops.get(indices[0]+1).getLocation()){
            stops.get(indices[0]+1).merge(stop1);
            stop1merged = true;
        }
        //Speciaal voor 1ste stop in de route
        else if(stop1.getLocation() == stops.get(indices[0]-1).getLocation()){
            stops.get(indices[0]-1).merge(stop1);
            stop1merged = true;
        }
        if (stop2.getLocation() == stops.get(indices[1] + 1).getLocation()) {
            stops.get(indices[1] + 1).merge(stop2);
            stop2merged = true;
        }
        try {

            if(stop1merged && stop2merged){
                stops.remove(indices[0]);
                stops.remove(indices[1]-1);
            }
            else if(stop1merged) stops.remove(indices[0]);
            else if(stop2merged) stops.remove(indices[1]);
        }


        catch(IndexOutOfBoundsException e){
            System.out.println("verwijder");
        }
    }

    /**
     * Deze methode checkt de feasibility van de route
     *
     * @return true als de route feasible is
     */
    public boolean isFeasible() {
        if (changed) calculateCost();
        if (orderViolations > 0) return false;
        if (timeViolations > 0) return false;
        if (fillRateViolations > 0) return false;
        return true;
    }

    //Bedoelt voor 2opt
    public boolean quickFeasiblityCheck(){
        Set<Machine> onTruck = new HashSet<>(stops.get(0).getCollect());
        int totalTime = stops.get(0).getTimeSpend();
        int fillrate = stops.get(0).getDeltaFillRate();
        for (int i = 1; i < stops.size(); i++) {
            Stop selected = stops.get(i);
            //Order
            onTruck.addAll(selected.getCollect());
            for(Machine m: selected.getDrop()){
                if(!onTruck.remove(m)) return false;
            }
            //Fillrate
            fillrate += selected.getDeltaFillRate();
            if(fillrate > Problem.getInstance().TRUCK_CAPACITY) return false;

            //Time
            totalTime += selected.getLocation().timeTo(stops.get(i-1).getLocation());
            totalTime += selected.getTimeSpend();
            if(totalTime > Problem.getInstance().TRUCK_WORKING_TIME) return false;
        }
        return true;
    }

    /*
     * Update the cost factors and return the total cost if not changed
     * */
    private int calculateCost() {
        if (changed) {

            totalDistance = 0;
            timeViolations = 0;
            orderViolations = 0;
            fillRateViolations = 0;

            boolean[] onTruck = new boolean[Problem.getInstance().machines.size()];

            for(Machine m: stops.get(0).getCollect()){
                onTruck[m.getId()] = true;
            }

            totalTime = stops.get(0).getTimeSpend();
            int fillrate = stops.get(0).getDeltaFillRate();
            if (fillrate > Problem.getInstance().TRUCK_CAPACITY) {
                fillRateViolations += fillrate - Problem.getInstance().TRUCK_CAPACITY;
            }

            for (int i = 1; i < stops.size(); i++) {
                Stop selected = stops.get(i);
                //Total distance
                Location A = selected.getLocation();
                Location B = stops.get(i-1).getLocation();
                totalDistance += A.distanceTo(B);
                //Calculate total time
                totalTime += A.timeTo(B);
                totalTime += selected.getTimeSpend();

                //order violations
                for(Machine m: selected.getCollect()){
                    onTruck[m.getId()] = true;
                }
                for(Machine m: selected.getDrop()){
                    if(!onTruck[m.getId()]) orderViolations++;
                }

                //FillRateViolations
                fillrate += selected.getDeltaFillRate();
                if (fillrate > Problem.getInstance().TRUCK_CAPACITY) {
                    fillRateViolations += fillrate - Problem.getInstance().TRUCK_CAPACITY;
                }
            }

            timeViolations = (totalTime > Problem.getInstance().TRUCK_WORKING_TIME) ? totalTime - Problem.getInstance().TRUCK_WORKING_TIME : 0;

            cost = DISTANCE_FACTOR * totalDistance
                    + TIME_FACTOR * timeViolations
                    + ORDER_FACTOR * orderViolations
                    + FILL_RATE_VIOLATIONS_FACTOR * fillRateViolations;
            changed = false;
        }
        return cost;
    }

    private int calculateTime() {
        int totalTime = 0;
        Stop prevStop = stops.get(0);
        //Time to drive to each stop
        for (int i = 1; i < stops.size(); i++) {
            totalTime += prevStop.getLocation().timeTo(stops.get(i).getLocation());
            prevStop = stops.get(i);
        }
        //Time spend at each stop to load/unload
        for (Stop s : stops) {
            totalTime += s.getTimeSpend();
        }
        return totalTime;
    }

    private int calculateTimeViolations() {
        int timeTooMuch = calculateTime() - Problem.getInstance().TRUCK_WORKING_TIME;
        if (timeTooMuch > 0) {
            return timeTooMuch;
        } else return 0;
    }

    private int calculateOrderViolations() {
        List<Machine> onTruck = new LinkedList<>();
        int orderViolations = 0;
        for (Stop s : stops) {
            onTruck.addAll(s.getCollect());
            for (Machine m : s.getDrop()) {
                if (!onTruck.remove(m)) {
                    //Een item van de truck halen die er niet opzit!
                    orderViolations++;
                }
            }
        }
        return orderViolations;
    }

    private int calculateFillRateViolations() {
        List<Machine> onTruck = new LinkedList<>();
        int fillRateViolations = 0;
        for (Stop s : stops) {
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            int fillRate = calculateFillRate(onTruck);
            if (fillRate > Problem.getInstance().TRUCK_CAPACITY) {
                fillRateViolations += 100 + fillRate - Problem.getInstance().TRUCK_CAPACITY;
            }
        }
        return fillRateViolations;
    }

    private int calculateFillRate(List<Machine> machines) {
        int fillrate = 0;
        for (Machine m : machines) {
            fillrate += m.getType().getVolume();
        }
        return fillrate;
    }

    private int calculateAvgStopsOnTruck() {
        List<Machine> onTruck = new LinkedList<>();
        HashMap<Machine, Integer> nStopsOnTruck = new HashMap<>();
        for (Stop s : stops) {
            onTruck.addAll(s.getCollect());
            onTruck.removeAll(s.getDrop());
            for (Machine m : onTruck) {
                if (nStopsOnTruck.containsKey(m)) {
                    nStopsOnTruck.put(m, nStopsOnTruck.get(m) + 1);
                } else {
                    nStopsOnTruck.put(m, 1);
                }
            }

        }
        if (nStopsOnTruck.size() == 0) return 0;

        int avgStopsOnTruck = 0;
        for (Integer i : nStopsOnTruck.values()) {
            avgStopsOnTruck += i;
        }
        return avgStopsOnTruck / nStopsOnTruck.size();

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

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        changed = true;
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
        //    hash2 = hash;
        //}
        return calculateCost();
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

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        //getCost();
    }
}
