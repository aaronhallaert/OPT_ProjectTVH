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
