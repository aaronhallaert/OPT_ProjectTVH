package TVH.Entities;

import TVH.Entities.Location;
import TVH.Entities.Machine;

/**
 * This class tells where a machine needs to go and from where.
 * It doesn't give any information about how to accomplish this.
 */
public class Move {
    private Machine machine;
    private Location from;
    private Location to;

    public Move(Machine machine, Location from, Location to) {
        this.machine = machine;
        this.from = from;
        this.to = to;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }
}