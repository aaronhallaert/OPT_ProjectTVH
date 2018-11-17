package TVH.Entities.Job;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Edge;
import TVH.Entities.Node.Location;
import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Node;
import TVH.Problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//DropJob bevat drop opdrachten moeten uitgevoerd worden
//Statische klasse

public class DropJob implements Job {
    private Location drop;
    private List<Location> collect;
    private MachineType machineType;
    //private List<Move> allMoves;

    public DropJob(Location drop, List<Location> collect, MachineType mt) {
        this.drop = drop;
        this.collect = collect;
        this.machineType = mt;

        /*HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        allMoves = new ArrayList<>();
        for(Location l: collect){
            Node node = nodesMap.get(l);
            List<Machine> available = node.getAvailableMachines();
            for(Machine m: available){
                if(m.getType() == machineType) {
                    allMoves.add(new Move(m, l, drop));
                }
            }
        }*/
    }
    public DropJob(DropJob dj){
        this.drop = dj.drop;
        this.collect = new ArrayList<>(dj.collect);
        this.machineType = dj.machineType;
    }

    public boolean notDone(){
        HashMap<Location,Node> nodesMap = Problem.getInstance().nodesMap;
        Node dropNode = nodesMap.get(drop);
        return dropNode.canPutMachineType(machineType);
    }

    public Location getFixedLocation(){
        return drop;
    }

    public Location getDrop() {
        return drop;
    }

    public void setDrop(Location drop) {
        this.drop = drop;
    }

    public List<Location> getCollect() {
        return collect;
    }

    public void setCollect(List<Location> collect) {
        this.collect = collect;
    }

    public MachineType getMachineType() {
        return machineType;
    }

    public void setMachineType(MachineType machineType) {
        this.machineType = machineType;
    }

    public int getRemoteFactor(){
        int distanceToAllNodes = 0;
        for(Edge e: drop.getEdgeMap().values()){
            distanceToAllNodes += e.getDistance();
        }
        return distanceToAllNodes;
    }

    public ArrayList<Move> generatePossibleMoves(){
        HashMap<Location, Node> nodesMap = Problem.getInstance().nodesMap;
        ArrayList<Move> moves = new ArrayList<>();
        //We voegen alle mogelijke moves toe;
        for(Location l: collect){
            Node node = nodesMap.get(l);
            if(node.hasMachineAvailableOfType(machineType)){
                Machine machine = node.viewMachineOfType(machineType);
                moves.add(new Move(machine, l, drop));
            }
        }
        return moves;
    }

    /*public List<Move> getAllMoves() {
        return allMoves;
    }*/

    public String toString(){
        return getFixedLocation().toString() + " ("+getMachineType()+")";
    }
}
