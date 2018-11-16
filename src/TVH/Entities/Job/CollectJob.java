package TVH.Entities.Job;

import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Edge;
import TVH.Entities.Node.Location;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Node;
import TVH.Problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//De klasse CollectJob bevat collect opdrachten die de trucks moeten uitvoeren.
//Statische klasse

public class CollectJob implements Job {
    private Location collect;
    private List<Location> drop;
    private Machine machine;

    public CollectJob(Location collect, List<Location> drop, Machine machine) {
        this.collect = collect;
        this.drop = drop;
        this.machine = machine;
    }

    public boolean notDone(){
        HashMap<Location,Node> nodesMap = Problem.getInstance().nodesMap;
        Node collectNode = nodesMap.get(collect);
        return collectNode.getAvailableMachines().contains(machine);

    }

    public Location getFixedLocation(){
        return collect;
    }

    public Location getCollect() {
        return collect;
    }

    public void setCollect(Location collect) {
        this.collect = collect;
    }

    public List<Location> getDrop() {
        return drop;
    }

    public void setDrop(List<Location> drop) {
        this.drop = drop;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public int getRemoteFactor(){
        int distanceToAllNodes = 0;
        for(Edge e: collect.getEdgeMap().values()){
            distanceToAllNodes += e.getDistance();
        }
        return distanceToAllNodes;
    }
    public MachineType getMachineType(){
        return machine.getType();
    }

    public ArrayList<Move> generatePossibleMoves(){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        ArrayList<Move> moves = new ArrayList<>();
        //Eerst alle drop opties verwijderen waar we de machine niet meer kunnen zetten hebben
        for(Location l: drop){
            Node node = nodesMap.get(l);
            if(node.canPutMachineType(machine.getType())){
                moves.add(new Move(machine, collect, l));
            }
        }
        return moves;
    }

    public String toString(){
        return getFixedLocation().toString()+ " ("+getMachineType()+")";
    }
}
