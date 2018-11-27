package TVH.Entities.Truck;

import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Node;
import TVH.Problem;

import java.util.*;

/**
 * Een truck kan Jobs uitvoeren doormiddel van Moves toe te voegen aan zijn Route.
 */
public class Truck {

    private int truckId;
    private Route route;
    private Location startLocation;
    private Location endLocation;
    private HashMap<Job, Move> jobMoveMap;

    public Truck(int truckId, Location startLocation, Location endLocation){
        this.truckId=truckId;
        this.startLocation= startLocation;
        this.endLocation=endLocation;

        Stop startStop = new Stop(startLocation);
        Stop endStop = new Stop(endLocation);
        route = new Route(startStop, endStop, this);

        jobMoveMap = new HashMap<>();
    }

    //Copy constructor
    public Truck(Truck t) {
        this.truckId = t.truckId;
        this.startLocation = t.startLocation;
        this.endLocation = t.endLocation;

        //Deep copy the entire route
        this.route = new Route(t.route, true);

        //Add all job move relations to new hashmap;
        jobMoveMap = new HashMap<>();
        for(Map.Entry<Job, Move> entry: t.jobMoveMap.entrySet()){
            jobMoveMap.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Laat deze truck aan nieuwe job afhandelen
     * @param j de Job in kwestie
     * @param searchBestMove boolean die aangeeft als de best mogelijke move moet gezocht worden of als het gewoon
     *                       random mag zijn.
     * @return true als het de truck de nieuwe job heeft kunnen afhandelen
     */
    public boolean addJob(Job j, boolean searchBestMove){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        List<Move> moves = j.generatePossibleMoves();

        //Als de beste move moet gezocht worden
        if(searchBestMove) {
            Move optimalMove = null;
            int minCost = Integer.MAX_VALUE;
            //Elke move eens toevoegen aan de route en kijken welke move zorgt voor het minst extra kost
            for (Move candidate : moves) {
                Route copy = new Route(route, false);
                if(copy.addMove(candidate)){
                    if (copy.getCost() < minCost) {
                        optimalMove = candidate;
                        minCost = copy.getCost();
                    }
                    copy.removeMove(candidate, false);
                }
                //De stops van de copy zijn geen diepe kopies, we moeten dus alitjd de move terug verwijderen

            }
            //Als geen optimale move gevonden is, betekent dit dat de truck de job niet kan uitvoeren
            if (optimalMove == null) return false;
            //De optimale move toevoegen aan de route
            route.addMove(optimalMove);
            jobMoveMap.put(j, optimalMove);
            //Dit registeren bij collect en drop node
            Node collectNode = nodesMap.get(optimalMove.getCollect());
            Node dropNode = nodesMap.get(optimalMove.getDrop());
            collectNode.takeMachine(optimalMove.getMachine());
            dropNode.putMachine(optimalMove.getMachine());
            return true;
        }
        //Als een willekeurige move mag toegevoegd worden.
        else{
            boolean moveAdded = false;
            Random r = new Random();
            while(!moveAdded && !moves.isEmpty()){
                Move randomMove = moves.get(r.nextInt(moves.size()));
                if(route.addMove(randomMove)){
                    //Indien de route de move kan doen: job registreren
                    moveAdded = true;
                    jobMoveMap.put(j, randomMove);
                    Node collectNode = nodesMap.get(randomMove.getCollect());
                    Node dropNode = nodesMap.get(randomMove.getDrop());
                    collectNode.takeMachine(randomMove.getMachine());
                    dropNode.putMachine(randomMove.getMachine());
                }
                else{
                    //Indien de route de move niet kan doen: move verwijderen en andere move proberen
                    moves.remove(randomMove);
                }
            }
            return moveAdded;
        }
    }

    /**
     * Laat deze truck een nieuwe Job afhandelen met een specifieke Move
     * @param j Job die moet afgehandeld worden
     * @param m Specifieke Move die de route moet doen op de Job te voltooien
     * @return true als de Job succesvol is toegevoegd
     */
    public boolean addJob(Job j, Move m){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;

        if(route.addMove(m)){
            jobMoveMap.put(j, m);
            Node collect = nodesMap.get(m.getCollect());
            Node drop = nodesMap.get(m.getDrop());
            collect.takeMachine(m.getMachine());
            drop.putMachine(m.getMachine());
            return true;
        }

        return false;

    }

    /**
     * Laat deze truck een nieuwe Job afhandelen met specifieke Move en Route. Enkel te gebruiken als de route zeker
     * feasible is.
     * @param j Job die moet afgehandeld worden.
     * @param m Specifieke Move
     * @param r Specifieke Route
     */
    public void addJob(Job j, Move m, Route r){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;


        route = r;
        jobMoveMap.put(j, m);
        Node collect = nodesMap.get(m.getCollect());
        Node drop = nodesMap.get(m.getDrop());
        collect.takeMachine(m.getMachine());
        drop.putMachine(m.getMachine());

    }

    /**
     * Verwijder een Job van een Truck
     * @param j Job die moet verwijderd worden
     * @param optimize Als de Route moet geoptimaliseerd worden of niet
     */
    public void removeJob(Job j, boolean optimize){

        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;

        //Move opzoeken en verwijderen uit Route
        Move move = jobMoveMap.get(j);
        route.removeMove(move, optimize);
        jobMoveMap.remove(j);

        //Registreren bij collect en drop node
        Node collect = nodesMap.get(move.getCollect());
        Node drop =  nodesMap.get(move.getDrop());
        collect.undoTakeMachine(move.getMachine());
        drop.undoPutMachine(move.getMachine());

    }

    /**
     * Geeft de kortste afstand terug die een Truck komt bij een bepaalde Location tijdens zijn Route.
     * @param l bepaalde Location
     * @return afstand
     */
    public int getDistanceToLocation(Location l){
        int minDistance = Integer.MAX_VALUE;
        for(Stop s: route.getStops()){
            Location stopLoc = s.getLocation();
            if(l.distanceTo(stopLoc) < minDistance){
                minDistance = l.distanceTo(stopLoc);
            }
        }
        return minDistance;
    }

    public boolean isIdle(){
        return jobMoveMap.isEmpty();
        //return route.getStops().size() == 2;
    }

    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
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

    public Route getRoute() {
        return route;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public HashMap<Job, Move> getJobMoveMap() {
        return jobMoveMap;
    }

    /**
     * Deze methode kan de truck zelf optimaliseren door jobs te verwijderen en opnieuw toe te voegen.
     * Dit kan nog de paar laatste kilometers eraf doen op het einde.
     *
     * NIET GEBRUIKEN: BUG
     */
    public void optimizeTruck(){
        //TODO: Er zit hier nog ergens een bug in
        List<Job> jobs = new ArrayList<>(jobMoveMap.keySet());
        boolean improvement = true;
        int minCost = route.getCost();
        while (improvement) {
            improvement = false;
            for(Job j: jobs){
                Route backup = new Route(route, true);
                Move oldMove = jobMoveMap.get(j);
                removeJob(j, false);
                if(addJob(j, true)){
                    int cost = route.getCost();
                    if(minCost > cost){
                        System.out.println("improvement found");
                        improvement = true;
                        minCost = cost;
                    }
                    else{
                        route = backup;
                        jobMoveMap.put(j, oldMove);
                    }
                }
                else{
                    route = backup;
                    jobMoveMap.put(j, oldMove);

                }
                for(Job job: jobs){
                    if(j.notDone()){
                        System.out.println("stop");
                    }
                }
            }
        }

    }

    /**
     * NEGEER
     * @param j
     * @return
     */
    public List<Proposal> getProposals(Job j){
        List<Proposal> proposals = new ArrayList<>();
        List<Move> moves = j.generatePossibleMoves();
        for(Move m: moves){
            Route copy = new Route(route, false);
            int oldCost = copy.getCost();
            if(copy.addMove(m)) {
                int newCost = copy.getCost();
                proposals.add(new Proposal(this, j, m, newCost - oldCost));
                copy.removeMove(m, false);
            }
        }
        return proposals;
    }

    public List<Proposal> getProposals(Job j, Set<Move> moves){
        List<Proposal> proposals = new ArrayList<>();
        for(Move m: moves){
            Route copy = new Route(route, false);
            int oldCost = copy.getCost();
            if(copy.addMove(m)) {
                int newCost = copy.getCost();
                proposals.add(new Proposal(this, j, m, newCost - oldCost));
                copy.removeMove(m, false);
            }
        }
        return proposals;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Truck: "+truckId+" ("+route.getTotalDistance() +"km) ("+route.getTotalTime()+" min) (f: "+route.isFeasible()+") (avgfill: "+route.getAvgStopsOnTruck()+")\n");
        /*for(Stop s: route.stops){
            sb.append("\t" + "Location " + s.getLocation()+"%\n");
            sb.append("\t\t Collect:\n");
            for(Machine m: s.getCollect()){
                sb.append("\t\t\t "+m+"\n");
            }
            sb.append("\t\t Drop:\n");
            for(Machine m: s.getDrop()){
                sb.append("\t\t\t "+m+"\n");
            }

        }*/
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Truck truck = (Truck) o;
        return truckId == truck.truckId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(truckId);
    }
}
