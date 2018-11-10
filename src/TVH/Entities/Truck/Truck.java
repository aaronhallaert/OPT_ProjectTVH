package TVH.Entities.Truck;

import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Entities.Machine;
import TVH.Entities.MachineType;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Node;
import TVH.Problem;

import java.util.*;

public class Truck {

    private int truckId;
    private Route route;
    private Location startLocation;
    private Location endLocation;
    private HashMap<Job, Move> jobMoveMap;
    private boolean used;

    public Truck(int truckId, Location startLocation, Location endLocation){
        this.truckId=truckId;
        this.startLocation= startLocation;
        this.endLocation=endLocation;

        used = false;

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
        this.used = t.used;

        //Deep copy each stop in route
        this.route = new Route(t.getRoute());

        //Add al job move relations to new hashmap;
        jobMoveMap = new HashMap<>();
        for(Map.Entry<Job, Move> entry: t.jobMoveMap.entrySet()){
            jobMoveMap.put(entry.getKey(), entry.getValue());
        }
    }

    public boolean addJob(Job j){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        List<Move> moves = new ArrayList<>();
        if(j instanceof DropJob){
            DropJob dj = (DropJob) j;
            Location drop = dj.getDrop();
            MachineType mt = dj.getMachineType();
            List<Location> possibleCollects = new LinkedList<>(dj.getCollect());
            //We voegen alle mogelijke moves toe;
            for(Location collect: dj.getCollect()){
                Node node = nodesMap.get(collect);
                if(node.hasMachineAvailableOfType(mt)){
                    Machine machine = node.viewMachineOfType(mt);
                    moves.add(new Move(machine, collect, drop));
                }
            }
        }
        else{
            CollectJob cj = (CollectJob) j;

            Location collect = cj.getCollect();
            Machine machine = cj.getMachine();
            List<Location> possibleDrops = new LinkedList<>(cj.getDrop());
            //Eerst alle drop opties verwijderen waar we de machine niet meer kunnen zetten hebben
            for(Location drop: cj.getDrop()){
                Node node = nodesMap.get(drop);
                if(node.canPutMachineType(machine.getType())){
                    moves.add(new Move(machine, collect, drop));
                }
            }
        }
        Move optimalMove = null;
        int minDistance = Integer.MAX_VALUE;
        for(Move candidate: moves){
            LinkedList<Stop> previousOrder = new LinkedList<>(route.stops);
            if(route.addMove(candidate)){
                int distance = route.calculateDistance();
                if(distance < minDistance){
                    optimalMove = candidate;
                    minDistance = distance;
                }
                route.removeMove(candidate, false);
                route.stops = previousOrder;
            }
        }
        if(optimalMove == null) return false;

        route.addMove(optimalMove);
        jobMoveMap.put(j, optimalMove);
        Node collectNode = nodesMap.get(optimalMove.getCollect());
        Node dropNode =  nodesMap.get(optimalMove.getDrop());
        collectNode.takeMachine(optimalMove.getMachine());
        dropNode.putMachine(optimalMove.getMachine());
        return true;
    }

    public void removeJob(Job j, boolean optimize){

        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        Move move = jobMoveMap.get(j);

        route.removeMove(move, optimize);
        jobMoveMap.remove(j);

        Node collect = nodesMap.get(move.getCollect());
        Node drop =  nodesMap.get(move.getDrop());
        collect.undoTakeMachine(move.getMachine());
        drop.undoPutMachine(move.getMachine());

    }

    public int getDistanceToLocation(Location l){
        int minDistance = Integer.MAX_VALUE;
        for(Stop s: route.stops){
            Location stopLoc = s.getLocation();
            if(l.distanceTo(stopLoc) < minDistance){
                minDistance = l.distanceTo(stopLoc);
            }
        }
        return minDistance;
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

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Truck: "+truckId+" ("+route.calculateDistance() +"km) ("+route.calculateTime()+" min) (f: "+route.isFeasible()+")\n");
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
}
