package TVH.Entities.Job;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;

//Statische klasse
public class Move {
    Machine machine;
    Location collect;
    Location drop;

    public Move(Machine machine, Location collect, Location drop) {
        this.machine = machine;
        this.collect = collect;
        this.drop = drop;
    }

    public Machine getMachine() {
        return machine;
    }

    public Location getCollect() {
        return collect;
    }

    public Location getDrop() {
        return drop;
    }
}
