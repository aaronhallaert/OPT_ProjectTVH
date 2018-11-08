package TVH.Entities.Job;

import TVH.Entities.Edge;
import TVH.Entities.Location;
import TVH.Entities.Machine;

import java.util.List;

public class CollectJob implements Job {
    private Location from;
    private List<Location> drop;
    private Machine m;

    public CollectJob(Location from, List<Location> drop, Machine m) {
        this.from = from;
        this.drop = drop;
        this.m = m;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public List<Location> getDrop() {
        return drop;
    }

    public void setDrop(List<Location> drop) {
        this.drop = drop;
    }

    public Machine getM() {
        return m;
    }

    public void setM(Machine m) {
        this.m = m;
    }

    public int getRemoteFactor(){
        int distanceToAllNodes = 0;
        for(Edge e: from.getEdgeMap().values()){
            distanceToAllNodes += e.getDistance();
        }
        return distanceToAllNodes;
    }
}
