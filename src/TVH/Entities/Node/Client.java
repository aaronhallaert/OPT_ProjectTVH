package TVH.Entities.Node;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;

import java.util.*;

/**
 * De klasse Client bevat elke Node waar we jobs moeten bij uitvoeren. Ze bevatten lijsten met machines die nog moeten
 * gecollect of gedropt worden. Dit is een dynamische klasse.
 */
public class Client implements Node{

    private Location location;
    private ArrayList<Machine> toCollect;
    private ArrayList<MachineType> toDrop;

    public Client(Location location) {
        this.location = location;
        this.toCollect = new ArrayList<>();
        this.toDrop = new ArrayList<>();
        //this.dropped = new LinkedList<>();
    }

    //Copy constructor
    public Client(Client n){
        location = n.location;
        toCollect = new ArrayList<>(n.toCollect);
        toDrop = new ArrayList<>(n.toDrop);
    }

    public void addToCollect(Machine machine) {
        toCollect.add(machine);
    }

    public void addToDrop(MachineType machineType){
        toDrop.add(machineType);
    }

    public void takeMachine(Machine machine){
        toCollect.remove(machine);
    }

    public void undoTakeMachine(Machine m){
        addToCollect(m);
    }

    public void putMachine(Machine machine){
        toDrop.remove(machine.getType());
    }

    public void undoPutMachine(Machine m){
        addToDrop(m.getType());
    }



    public Machine viewMachineOfType(MachineType mt){
        for(Machine m: toCollect){
            if(m.getType() == mt){
                return m;
            }
        }
        return null;
    }

    public boolean needsCollect(Machine m){
        return (toCollect.contains(m));
    }

    public boolean hasMachineAvailableOfType(MachineType mt){
        for(Machine m: toCollect){
            if(m.getType() == mt) return true;
        }
        return false;
    }

    public List<Machine> getAvailableMachines(){
        return toCollect;
    }

    public boolean needsDrop(MachineType machineType){
        for(MachineType mt: toDrop){
            if(mt == machineType) return true;
        }
        return false;
    }

    public boolean canPutMachineType(MachineType mt){
        return needsDrop(mt);
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Machine> getToCollect() {
        return toCollect;
    }

    public ArrayList<MachineType> getToDrop() {
        return toDrop;
    }
}
