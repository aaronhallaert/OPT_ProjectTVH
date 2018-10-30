import java.util.Objects;

public class MachineType {
    private int id;
    private String name;
    private int volume;
    private int serviceTime;

    public MachineType(int id, String name, int volume, int serviceTime) {
        this.id = id;
        this.name = name;
        this.volume = volume;
        this.serviceTime = serviceTime;
    }

    //Constructor die enkel dient om machinetypes op te zoeken
    public MachineType(int id) {
        this.id = id;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }


}
