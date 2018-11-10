package TVH.Entities.Node;

import TVH.Entities.Machine;
import TVH.Entities.MachineType;
import com.google.common.collect.HashMultimap;

import java.util.ArrayList;
import java.util.List;

public class Depot implements Node{

    private Location location;
    private HashMultimap<MachineType, Machine> machines;

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

    public void putMachine(Machine m){
        machines.put(m.getType(), m);
    }

    public void takeMachine(Machine m){
        machines.get(m.getType()).remove(m);
    }

    public Machine viewMachineOfType(MachineType mt){
        for(Machine m: machines.get(mt)){
            if(!m.isMoved()) return m;
        }
        return null;
    }
    public boolean canPutMachineType(MachineType mt){
        return true;
    }


    public boolean hasMachineAvailableOfType(MachineType mt){
        for(Machine m: machines.get(mt)){
            if(!m.isMoved()) return true;
        }
        return false;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMultimap<MachineType, Machine> getTypeMachineMap() {
        return machines;
    }
    public List<Machine> getAvailableMachines(){
        return new ArrayList<>(machines.values());
    }
}
