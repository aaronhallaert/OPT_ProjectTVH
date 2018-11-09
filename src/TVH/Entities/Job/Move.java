package TVH.Entities.Job;

import TVH.Entities.Machine;
import TVH.Entities.Node.Location;

public class Move {
    Machine machine;
    Location collect;
    Location drop;

    public Move(Machine machine, Location collect, Location drop) {
        this.machine = machine;
        this.collect = collect;
        this.drop = drop;
    }
}
