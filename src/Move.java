public class Move {
    Machine machine;
    Location from;
    Location to;

    public Move(Machine machine, Location from, Location to) {
        this.machine = machine;
        this.from = from;
        this.to = to;
    }
}
