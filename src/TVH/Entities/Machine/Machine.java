package TVH.Entities.Machine;

//Statische klasse
public class Machine {
    private final int id;
    private final MachineType type;

    public Machine(int id, MachineType type) {
        this.id = id;
        this.type = type;
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
        return id;
    }

    public int getId() {
        return id;
    }

    public MachineType getType() {
        return type;
    }

    @Override
    public String toString() {
        return id + " ("+type.getName()+")";
    }



}
