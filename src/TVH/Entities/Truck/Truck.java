package TVH.Entities.Truck;

import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Location;
import TVH.Entities.Node.Node;
import TVH.Problem;

import java.util.*;

//Dynamische klasse
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
        this.route = new Route(t.route);

        //Add all job move relations to new hashmap;
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
        if(moves.isEmpty()){
            System.out.println("stop");
        }
        Move optimalMove = null;
        int minCost = Integer.MAX_VALUE;
        for(Move candidate: moves){
            LinkedList<Stop> previousOrder = new LinkedList<>(route.getStops());
            if(route.addMove(candidate)){
                if(route.getCost() < minCost){
                    optimalMove = candidate;
                    minCost = route.getCost();
                }
                route.removeMove(candidate, false);
                route.setStops(previousOrder);
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

    //Deze methode voegt een job toe aan een truck, met bijhoorde move en route direct er al bij
    //We zijn zeker dat deze route feasible is
    public void addJob(Job j, Move m, Route r){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;

        route = r;
        jobMoveMap.put(j, m);
        Node collect = nodesMap.get(m.getCollect());
        Node drop =  nodesMap.get(m.getDrop());
        collect.takeMachine(m.getMachine());
        drop.putMachine(m.getMachine());

    }

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

    public void optimizeTruck(){
        List<Job> jobs = new ArrayList<>(jobMoveMap.keySet());
        boolean improvement = true;
        int minCost = route.getCost();
        while (improvement) {
            improvement = false;
            for(Job j: jobs){
                Route backup = new Route(route);
                Move oldMove = jobMoveMap.get(j);
                removeJob(j, false);
                if(addJob(j)){
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
            }
        }
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Truck: "+truckId+" ("+route.getTotalDistance() +"km) ("+route.getTotalTime()+" min) (f: "+route.isFeasible()+") (avgfill: "+route.getFillrateAbove65()+")\n");
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
