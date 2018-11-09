package TVH.Entities.Node;

import TVH.Entities.Machine;
import TVH.Entities.MachineType;

import java.util.*;

public class Client implements Node {

    private Location location;
    private ArrayList<Machine> toCollect;
    private ArrayList<MachineType> toDrop;
    private LinkedList<Machine> machines;
    private boolean finished;

    public Client(Location location) {
        this.location = location;
        this.toCollect = new ArrayList<>();
        this.toDrop = new ArrayList<>();
        this.machines = new LinkedList<>();
        this.finished = false;
    }

    //Copy constructor
    public Client(Client n){
        location = n.location;
        finished = n.finished;
        toCollect = new ArrayList<>(n.toCollect);
        toDrop = new ArrayList<>(n.toDrop);
        machines = new LinkedList<>(n.machines);
    }

    public void addToCollect(Machine machine){
        toCollect.add(machine);
        machines.add(machine);
    }

    public void addToDrop(MachineType machineType){
        toDrop.add(machineType);
    }

    public void takeMachine(Machine machine){
        machines.remove(machine);
    }
    public Machine takeMachine(MachineType mt){
        for(Machine m: machines){
            if(m.getType() == mt){
                machines.remove(m);
                return m;
            }
        }
        return null;
    }

    public void putMachine(Machine machine){
        machines.add(machine);
    }

    public boolean needsCollect(MachineType mt){
        for(Machine m: toCollect){
            if(machines.contains(m) && m.getType() == mt){
                return true;
            }
        }
        return false;
    }

    public Machine getMachineToCollect(MachineType mt){
        for(Machine m: toCollect){
            if(m.getType() == mt){
                return m;
            }
        }
        return null;
    }

    public boolean needsCollect(Machine m){
        //We returnen true als de machine moet gecollect worden en zich ook nog bij de client bevat.
        return (toCollect.contains(m) && machines.contains(m));
    }
    public boolean hasMachineAvailableOfType(MachineType mt){
        for(Machine m: getAvailableMachines()){
            if(m.getType() == mt) return true;
        }
        return false;
    }

    public List<Machine> getAvailableMachines(){
        //De beschikbare machines zijn al diegene die moeten opgehaald worden en nog bij de client staan
        ArrayList<Machine> availableMachines = new ArrayList<>(toCollect);
        availableMachines.retainAll(machines);
        return availableMachines;
    }

    public boolean needsDrop(MachineType machineType){
        //We nemen het verschil van de machines die bij de client staan en diegene die gecollect moeten worden.
        //Hierdoor houden we enkel de machines over die al gedropt zijn.
        ArrayList<Machine> alreadyDropped = new ArrayList<>(machines);
        alreadyDropped.removeAll(toCollect);

        //We tellen hoeveel machines we nodig hebben van een bepaald type
        int nMachinesNeeded = 0;
        for(MachineType mt: toDrop){
            if(mt == machineType) nMachinesNeeded++;
        }
        //We tellen hoeveel machines er al gedropt zijn van een bepaald type
        for(Machine m: alreadyDropped){
            if(m.getType() == machineType) nMachinesNeeded--;
        }
        //return true als we er nog minstens 1 nodig hebben
        return nMachinesNeeded > 0;
    }

    public boolean canPutMachine(Machine m){
        return needsDrop(m.getType());
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
