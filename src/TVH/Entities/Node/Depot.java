package TVH.Entities.Node;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;
import com.google.common.collect.HashMultimap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//Dynamische klasse
public class Depot implements Node{

    private Location location;
    private HashMultimap<MachineType, Machine> machines;
    private LinkedList<Machine> dropped;

    public Depot(Location location) {
        this.location = location;
        machines = HashMultimap.create();
        dropped = new LinkedList<>();
    }

    //Copy constructor
    public Depot(Depot toCopy){
        this.location = toCopy.location;
        machines = HashMultimap.create();
        for(Machine m: toCopy.machines.values()){
            machines.put(m.getType(), m);
        }
        dropped = new LinkedList<>(toCopy.dropped);
    }
    public void addMachine(Machine m){
        machines.put(m.getType(), m);
    }

    public void putMachine(Machine m){
        dropped.add(m);
    }

    public void undoPutMachine(Machine m){
        dropped.remove(m);
    }

    public void takeMachine(Machine m){
        machines.remove(m.getType(), m);
    }

    public void undoTakeMachine(Machine m){
        machines.put(m.getType(), m);
    }

    public Machine viewMachineOfType(MachineType mt){
        for(Machine m: machines.get(mt)){
            return m;
        }
        return null;
    }
    public boolean canPutMachineType(MachineType mt){
        return true;
    }


    public boolean hasMachineAvailableOfType(MachineType mt){
        for(Machine m: machines.get(mt)){
            return true;
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
