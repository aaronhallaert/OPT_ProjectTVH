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

    private static final int TIME_FACTOR = Config.getInstance().getTimefactor(); //100
    private static final int ORDER_FACTOR = Config.getInstance().getOrderfactor(); //1000
    private static final int FILL_RATE_VIOLATIONS_FACTOR = Config.getInstance().getFrviolationsfactor(); // 1000
    private static final int DISTANCE_FACTOR = Config.getInstance().getDistancefactor(); //1

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

        //De kans bestaat dat de truck al langs drop of collect locatie van de move passeert:
        Stop collectStop = null;
        Stop dropStop = null;
        //Collect stop opzoeken
        int collectIndex = -1;
        int dropIndex = -1;
        for (int i = 0; i < stops.size(); i++) {
            Stop s = stops.get(i);
            if (s.getLocation() == m.getCollect() && collectIndex == -1) {
                collectStop = s;
                collectIndex = i;
            }
            if(s.getLocation() == m.getDrop()){
                dropStop = s;
                dropIndex = i;
            }
        }

        //Indien deze nog niet in de route zit, hem toevoegen.
        if (collectStop == null && dropStop == null) {
            if(m.getCollect() == m.getDrop()){
                Stop collectAndDropStop = new Stop(m.getCollect());
                collectAndDropStop.addToCollect(m.getMachine());
                collectAndDropStop.addToDrop(m.getMachine());

                insertStop(collectAndDropStop, 1, stops.size());
            }
            else {
                collectStop = new Stop(m.getCollect());
                dropStop = new Stop(m.getDrop());
                collectStop.addToCollect(m.getMachine());
                dropStop.addToDrop(m.getMachine());

                insertStops(collectStop, dropStop);
            }
        }
        else {
            if (collectStop == null) {
                collectStop = new Stop(m.getCollect());
                collectStop.addToCollect(m.getMachine());
                dropStop.addToDrop(m.getMachine());

                insertStop(collectStop, 1, dropIndex+1);

            }
            else if(dropStop == null){
                dropStop = new Stop(m.getDrop());
                dropStop.addToDrop(m.getMachine());
                collectStop.addToCollect(m.getMachine());

                insertStop(dropStop, collectIndex+1, stops.size());
            }
            else{
                dropStop.addToDrop(m.getMachine());
                collectStop.addToCollect(m.getMachine());
            }
        }

        changed = true;
        //De optimalisatie geeft geen garantie van feasibility, er moet dus nog gekeken worden als de rit feasible is
        if (!isFeasible()) {
            //Zo niet herstellen, verwijderen we de move terug en zetten we de volgorde terug naar de originele
            removeMove(m, false);
            loadRoute(backup);

            return false;
        }


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

        /*if (optimize) {
            optimizeRoute();
        }*/

        changed = true;
    }

    /**
     * Deze methode optimaliseert de route met behulp van 2-opt swaps
     */
    /*private void optimizeRoute() {
        boolean betterRouteFound = true;
        //int randomSwapsDone = 0;
        boolean feasibleRouteExists = this.quickFeasiblityCheck();
        while (betterRouteFound) {
            betterRouteFound = false;
            //Elke mogelijk combinatie van swap overlopen
            for (int i = 1; i < getStops().size() - 1; i++) {
                for (int j = 1; j < getStops().size() - 1; j++) {
                    //Enkel als i kleiner is dan j is het nuttig op de swap uit te voeren
                    if (i < j) {
                        Route candidate = new Route(this, false);
                        candidate.twoOptSwap(i, j);
                        if(feasibleRouteExists){
                            if(!candidate.quickFeasiblityCheck()) break;
                        }
                        //kijken als de nieuwe route beter is
                        if (candidate.getCost() < this.getCost()) {
                            this.loadRoute(candidate);
                            betterRouteFound = true;
                            //Vanaf een feasible kandidaat gevonden is, smijten we een oplossing weg van zodra hij niet feasible is
                            if(candidate.isFeasible()){
                                feasibleRouteExists = true;
                            }
                        }
                    }
                }
            }
        }
    }*/

    private void insertStops(Stop stop1, Stop stop2){
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
                    }
                }
            }
        }
        loadRoute(best);
    }

    private void insertStop(Stop stop1, int lowerbound, int upperbound){
        int minCost = Integer.MAX_VALUE;
        Route best = null;
        for (int i = lowerbound; i < upperbound; i++) {
            Route candidate = new Route(this, false);
            candidate.getStops().add(i, stop1);
            candidate.setChanged(true);
            if(candidate.getCost() < minCost){
                best = candidate;
                minCost = candidate.getCost();
            }
        }
        loadRoute(best);
    }

    /**
     * Effectieve 2opt swap methode
     *
     * @param firstCut  waar de 1ste cut gemaakt wordt
     * @param secondCut waar de 2de cut gemaakt wordt
     * @return geswapte lijst
     */
    public void twoOptSwap(int firstCut, int secondCut) {
        List<Stop> cut = new ArrayList<>();
        for (int i = secondCut; i >=firstCut ; i--) {
            cut.add(stops.get(i));
        }
        for (int i = 0; i < cut.size(); i++) {
            stops.set(firstCut + i, cut.get(i));
        }
        changed = true;
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

            Set<Machine> onTruck = new HashSet<>(stops.get(0).getCollect());

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
                onTruck.addAll(selected.getCollect());
                for(Machine m: selected.getDrop()){
                    if(!onTruck.remove(m)) orderViolations++;
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
