package TVH.Entities.Job;

import TVH.Entities.Node.Edge;
import TVH.Entities.Node.Location;
import TVH.Entities.MachineType;

import java.util.List;

public class DropJob implements Job {
    private Location drop;
    private List<Location> collect;
    private MachineType machineType;

    public DropJob(Location drop, List<Location> collect, MachineType mt) {
        this.drop = drop;
        this.collect = collect;
        this.machineType = mt;
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
}
