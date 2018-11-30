package TVH.Entities.Machine;

import java.util.Objects;

//Statische klasse
public class MachineType {
    private final int id;
    private final String name;
    private final int volume;
    private final int serviceTime;

    public MachineType(int id, String name, int volume, int serviceTime) {
        this.id = id;
        this.name = name;
        this.volume = volume;
        this.serviceTime = serviceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MachineType)) return false;
        MachineType that = (MachineType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public String toString(){
        return name;
    }
}
