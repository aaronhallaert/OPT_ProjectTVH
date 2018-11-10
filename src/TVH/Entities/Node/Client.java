package TVH.Entities.Node;

import TVH.Entities.Machine;
import TVH.Entities.MachineType;

import java.util.*;

public class Client implements Node {

    private Location location;
    private ArrayList<Machine> toCollect;
    private ArrayList<MachineType> toDrop;
    private boolean finished;

    public Client(Location location) {
        this.location = location;
        this.toCollect = new ArrayList<>();
        this.toDrop = new ArrayList<>();
        //this.dropped = new LinkedList<>();
        this.finished = false;
    }

    //Copy constructor
    public Client(Client n){
        location = n.location;
        finished = n.finished;
        toCollect = new ArrayList<>(n.toCollect);
        toDrop = new ArrayList<>(n.toDrop);
        //dropped = new LinkedList<>(n.dropped);
    }

    public void addToCollect(Machine machine) {
        //dropped.add(machine);
        //Speciaal voor locatie 88 van probleem 4, waarbij je een machine moet komen collecten die ook gedropt moet worden >:(
        //if (!toDrop.contains(machine.getMachineType())) {
        toCollect.add(machine);
        //}
    }

    public void addToDrop(MachineType machineType){
        toDrop.add(machineType);
    }

    public void takeMachine(Machine machine){
        //dropped.remove(machine);
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
        //We returnen true als de machine moet gecollect worden en zich ook nog bij de client bevat.
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
        //We nemen het verschil van de dropped die bij de client staan en diegene die gecollect moeten worden.
        //Hierdoor houden we enkel de dropped over die al gedropt zijn.

        //We tellen hoeveel dropped we nodig hebben van een bepaald type
        int nMachinesNeeded = 0;
        for(MachineType mt: toDrop){
            if(mt == machineType) nMachinesNeeded++;
        }
        //return true als we er nog minstens 1 nodig hebben
        return nMachinesNeeded > 0;
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
