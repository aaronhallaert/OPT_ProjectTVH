package TVH.Entities;

import TVH.Problem;

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
     * This method let's a truck handle a move. It also checks if no constraints are broken by handling the new move.
     * @param m Move that needs to be handled by the truck.
     * @return true if the truck breaks no constraints while handling the new move. False if it does break a constraint;
     */
    public boolean doMove(Move m){
        Location from = m.getFrom();
        Location to = m.getTo();
        Machine machine = m.getMachine();

        /*
            First step: Check if the truck already passes the origin and/or destination of the truck.
            If so no new stops are needed.
         */

        if(!(locationStopMap.containsKey(from) || from==startLocation)) {
            //In case the Truck doesn't yet pass by Move.from.
            Stop newStop = new Stop(from);
            //Add a new stop on the optimal location


            int index = findBestIndexToInsert(newStop, 0, findHighBound(to));
            if(index < 0){
                System.out.println("stop");
            }


            newStop.setOnTruck(route.get(index-1).getOnTruck());
            route.add(index, newStop);
            locationStopMap.put(from, newStop);
        }
        if(!(locationStopMap.containsKey(to) || to==endLocation)){
            //In case the Truck doesn't yet pass by Move.to.
            Stop newStop = new Stop(to);
            //Add a new stop on the optimal location

            int index = findBestIndexToInsert(newStop, findLowBound(from), route.size()-1);
            if(index < 0){
                System.out.println("stop");
            }
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

        if(route.indexOf(collectStop) > route.indexOf(dropStop)){
            //Dit kan enkel het geval zijn als de nodes er al inzaten voor een andere move
            return false;
        };

        //Add the machine to the right stops, and check the fillrate constraint;
        collectStop.addCollectItem(machine);
        dropStop.addDropItem(machine);
        if(!recalculateOnTruck()) {
            return false;
        }
        /*int collectIndex = route.indexOf(collectStop);
        int dropIndex = route.indexOf(dropStop);
        for(int i = collectIndex; i < dropIndex; i++){
            if(!route.get(i).addToTruck(machine)){
                return false;
            }
        }*/
        //Total time is recalculated and checked;
        if(!recalculateTime()) {
            return false;
        }

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
        return totalTime <= Problem.TRUCK_WORKING_TIME;
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


    /**
     * deze route controleert feasibility van volgorde van route aan de hand van stops
     *
     * per stop wordt gecontroleerd of de drop kan uitgevoerd worden aan de hand van een
     * variërende "availableMap" die upgedate wordt bij het controleren van elke stop
     *
     * @param route route is lijst met alle stops in te controleren volgorde
     * @param problem algemeen probleem nodig om te kunnen itereren over alle machinetypes
     * @return false indien route niet feasible is, true indien volgorde wel feasible
     */
    public boolean routeControleBasedOnStops(LinkedList<Stop> route, Problem problem){
        HashMap<MachineType, Integer> availableMap= new HashMap<>();
        for (MachineType machineType : problem.machineTypes) {
            availableMap.put(machineType, 0);
        }

        // CONTROLE
        for (Stop stop : route) {
            ArrayList<Machine> machinesCollect = stop.getCollectItems();
            ArrayList<Machine> machinesDrop = stop.getDropItems();

            // controlleer of er genoeg aanwezig is om te droppen
            HashMap<MachineType, Integer> neededMapStop= new HashMap<>();
            for (Machine machine : machinesDrop) {
                neededMapStop.putIfAbsent(machine.getType(), 0);
                neededMapStop.replace(machine.getType(), neededMapStop.get(machine.getType())+1);
            }

            // oppikken van machines (dus available aanvullen)
            for (Machine machine : machinesCollect) {
                availableMap.replace(machine.getType(), availableMap.get(machine.getType())+1);
            }

            for (Map.Entry<MachineType, Integer> machineTypeNeeded : neededMapStop.entrySet()) {
                MachineType nodigeMachinetype= machineTypeNeeded.getKey();
                int aantalAvailable= availableMap.get(nodigeMachinetype);
                int aantalNodig= machineTypeNeeded.getValue();

                // drop is niet feasible als er meer gedropt dient te worden dan er aanwezig is
                if(aantalNodig>aantalAvailable){
                    return false;
                }
                else{
                    availableMap.replace(machineTypeNeeded.getKey(),(availableMap.get(machineTypeNeeded.getKey())-machineTypeNeeded.getValue()));
                }
            }

        }

        return true;
    }

    public static double getRandomDoubleBetweenRange(double min, double max){

        double x = (Math.random()*((max-min)+1))+min;

        return x;

    }

    /**
     * deze methode swapt 2 items en doet dit een aantal keer afhankelijk van de grootte van de list
     * @param alleStops
     */
    public void shuffleList(List<Stop> alleStops){
        int aantalShuffles= alleStops.size()/2;
        List<Integer> randomIndexes1= new ArrayList<>();
        List<Integer> randomIndexes2= new ArrayList<>();
        for (int i = 0; i < aantalShuffles; i++) {
            randomIndexes1.add((int) getRandomDoubleBetweenRange(0, alleStops.size()-1));
            randomIndexes2.add((int) getRandomDoubleBetweenRange(0, alleStops.size()-1));
        }


        for (int i = 0; i < aantalShuffles; i++) {
            if (randomIndexes1.get(i) != randomIndexes2.get(i)) {
                Collections.swap(alleStops, randomIndexes1.get(i), randomIndexes2.get(i));
            }
        }





    }


    /**
     * deze methode probeert de oorspronkelijke route van een truck te optimaliseren (km verminderen)
     * @param problem Probleem is nodig om alle machinetypes te bereiken
     */
    public void optimiseRoute(Problem problem){
        System.out.println("check truck " + truckId + " voor verbetering in route");
        // sla originele route op (deze wordt upgedate indien er iets beters gevonden wordt)
        LinkedList<Stop> besteRoute= new LinkedList<>(route);
        // sla originele distance op (deze wordt upgedate indien er iets beters gevonden wordt)
        int besteTotaldistance= getTotalDistance();



        // vraag eerste en laatste node op, deze hebben we nodig om uit "alle stops" te halen
        // aangezien we deze altijd in het begin en op het einde van de route willen
        Stop firstStop = route.getFirst();
        Stop lastStop = route.getLast();



        for (int i = 0; i < locationStopMap.values().size()*50000; i++) {
            // haal alle stops uit locationStopMap
            LinkedList<Stop> alleStops = new LinkedList<>(locationStopMap.values());



            // haal eerste en laatste stop uit voorlopig route, aangezien deze niet geshuffled mogen worden
            alleStops.remove(firstStop);
            alleStops.remove(lastStop);

            // shuffle de stops
            shuffleList(alleStops);

            // voeg eerste en laatste stop terug toe
            alleStops.addFirst(firstStop);
            alleStops.addLast(lastStop);



            // controleer route of deze beter is
            if (routeControleBasedOnStops(alleStops, problem)) {
                // voorlopige route instellen als route van truck (om te kunnen recalculaten)
                route = alleStops;
                recalculateOnTruck();
                recalculateTime();


                if (!recalculateOnTruck() || !recalculateTime()) {
                    //System.out.println("verwerp deze nieuwe route");
                    route = besteRoute;
                    recalculateOnTruck();
                    recalculateTime();
                }
                else {
                    int currentTotalDistance= getTotalDistance();
                    if (besteTotaldistance > currentTotalDistance) {
                        int verbetering= besteTotaldistance- currentTotalDistance;
                        System.out.println("gank verbetering van "+ verbetering +" km");
                        besteRoute = route;
                        besteTotaldistance = getTotalDistance();

                    }
                }
            }




           // alleStops.removeFirst();
           // alleStops.removeLast();
        }
        route = besteRoute;
        recalculateOnTruck();
        recalculateTime();

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
