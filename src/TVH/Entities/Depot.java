package TVH.Entities;

import java.util.HashMap;
import java.util.LinkedList;

public class Depot {

    private Location location;
    private HashMap<MachineType, LinkedList<Machine>> machines;

    public Depot(Location location) {
        this.location = location;
        machines = new HashMap<>();
    }

    //Copy constructor
    public Depot(Depot toCopy){
        this.location = toCopy.location;
        machines = new HashMap<>();
        for(LinkedList<Machine> machinelist: toCopy.machines.values()){
            machines.put(machinelist.getFirst().getType(), new LinkedList<>(machinelist));
        }
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
    public void removeMachine(Machine m){
        machines.get(m.getType()).remove(m);
    }

    public Machine getMachineFromDepot(MachineType mt){
        if(hasMachine(mt)){
            return machines.get(mt).getFirst();
        }
        return null;
    }

    public boolean hasMachine(MachineType mt){
        return machines.containsKey(mt) && machines.get(mt).size() > 0;
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
