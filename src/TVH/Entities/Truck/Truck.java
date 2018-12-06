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
    private final Location startLocation;
    private final Location endLocation;
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

            Problem.getInstance().locationJobMap.put(m.getDrop(), j);
            Problem.getInstance().locationJobMap.put(m.getCollect(), j);

            return true;
        }

        return false;

    }

    /**
     * Verwijder een Job van een Truck
     * @param j Job die moet verwijderd worden
     */
    public void removeJob(Job j){

        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;

        //Move opzoeken en verwijderen uit Route
        Move move = jobMoveMap.get(j);
        route.removeMove(move);
        jobMoveMap.remove(j);

        //Registreren bij collect en drop node
        Node collect = nodesMap.get(move.getCollect());
        Node drop =  nodesMap.get(move.getDrop());
        collect.undoTakeMachine(move.getMachine());
        drop.undoPutMachine(move.getMachine());

        Problem.getInstance().locationJobMap.remove(move.getDrop(), j);
        Problem.getInstance().locationJobMap.remove(move.getCollect(), j);


    }

    public boolean isIdle(){
        return jobMoveMap.isEmpty();
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
     * Maakt een lijst van proposals aan voor een Job j
     * @param j Job die moet uitgevoerd worden
     * @return Lijst met gegenereerde proposals
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
                copy.removeMove(m);
            }
        }
        return proposals;
    }

    /**
     * Maakt een lijst met proposals om een Job uit te voeren, maar enkel van specifieke moves (ipv alle mogelijke moves)
     * @param j Job die moet uitgevoerd worden
     * @param moves moves die moeten bekeken worden
     * @return lijst van gegenereerde proposals
     */
    public List<Proposal> getProposals(Job j, Set<Move> moves){
        List<Proposal> proposals = new ArrayList<>();
        for(Move m: moves){
            Route copy = new Route(route, false);
            int oldCost = copy.getCost();
            if(copy.addMove(m)) {
                int newCost = copy.getCost();
                proposals.add(new Proposal(this, j, m, newCost - oldCost));
                copy.removeMove(m);
            }
        }
        return proposals;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Truck: "+truckId+" ("+route.getTotalDistance() +"km) ("+route.getTotalTime()+" min) (f: "+route.isFeasible()+")\n";
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
