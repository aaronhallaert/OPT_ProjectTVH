package TVH.Entities.Job;

import TVH.Entities.Edge;
import TVH.Entities.Location;
import TVH.Entities.MachineType;

import java.util.List;

public class DropJob implements Job {
    private Location to;
    private List<Location> from;
    private MachineType mt;

    public DropJob(Location to, List<Location> from, MachineType mt) {
        this.to = to;
        this.from = from;
        this.mt = mt;
    }

    public Location getJobLocation(){
        return to;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public List<Location> getFrom() {
        return from;
    }

    public void setFrom(List<Location> from) {
        this.from = from;
    }

    public MachineType getMt() {
        return mt;
    }

    public void setMt(MachineType mt) {
        this.mt = mt;
    }

    public int getRemoteFactor(){
        int distanceToAllNodes = 0;
        for(Edge e: to.getEdgeMap().values()){
            distanceToAllNodes += e.getDistance();
        }
        return distanceToAllNodes;
    }
}
