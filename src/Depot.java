import java.util.HashMap;
import java.util.LinkedList;

public class Depot {

    private Location location;
    private HashMap<MachineType, LinkedList<Machine>> machines;

    public Depot(Location location) {
        this.location = location;
        machines = new HashMap<>();
    }

    public void addMachine(Machine m){
        LinkedList<Machine> list = machines.get(m.getType());
        if(machines.get(m.getType()) != null){
            list.add(m);
        }
        else{
            machines.put(m.getType(), new LinkedList<>());
            machines.get(m.getType()).add(m);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<MachineType, LinkedList<Machine>> getMachines() {
        return machines;
    }

    public void setMachines(HashMap<MachineType, LinkedList<Machine>> machines) {
        this.machines = machines;
    }
}
