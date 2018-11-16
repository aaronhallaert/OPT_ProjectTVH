package TVH.Entities.Job;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;

import java.util.Objects;

//Statische klasse, maar er worden er wel aangemaakt en verwijderd
public class Move { //een egde (een pijl tussen 2 Nodes)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return Objects.equals(machine, move.machine) &&
                Objects.equals(collect, move.collect) &&
                Objects.equals(drop, move.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machine, collect, drop);
    }
}
