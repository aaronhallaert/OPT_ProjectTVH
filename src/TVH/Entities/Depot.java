package TVH.Entities;

import com.google.common.collect.HashMultimap;

import java.util.HashMap;
import java.util.LinkedList;

public class Depot {

    private Location location;
    private HashMultimap<MachineType,Machine> machines;

    public Depot(Location location) {
        this.location = location;
        machines = HashMultimap.create();
    }

    //Copy constructor
    public Depot(Depot toCopy){
        this.location = toCopy.location;
        machines = HashMultimap.create();
        for(Machine m: toCopy.machines.values()){
            machines.put(m.getType(), m);
        }
    }

    public void addMachine(Machine m){
        machines.put(m.getType(), m);
    }
    public void removeMachine(Machine m){
        machines.get(m.getType()).remove(m);
    }
    public void removeMachine(MachineType mt){
        for(Machine m: machines.get(mt)){
            machines.get(mt).remove(m);
            return;
        }
    }

    public Machine getMachineFromDepot(MachineType mt){
        for(Machine m: machines.get(mt)){
            return m;
        }
        return null;
    }

    public boolean hasMachine(MachineType mt){
        return machines.containsKey(mt);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMultimap<MachineType, Machine> getMachines() {
        return machines;
    }
}
