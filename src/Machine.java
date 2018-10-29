import java.util.Objects;

public class Machine {
    private int id;
    private MachineType type;
    private Node location;

    public Machine(int id, MachineType type, Node location) {
        this.id = id;
        this.type = type;
        this.location = location;
    }

    public Machine(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Machine)) return false;
        Machine machine = (Machine) o;
        return id == machine.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MachineType getType() {
        return type;
    }

    public void setType(MachineType type) {
        this.type = type;
    }

    public Node getLocation() {
        return location;
    }

    public void setLocation(Node location) {
        this.location = location;
    }
}
