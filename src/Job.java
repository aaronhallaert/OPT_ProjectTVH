import java.util.*;

public class Job {

    //zaken die moeten gebeuren op 1 bepaalde location;
    private Location location;
    private LinkedList<Machine> toCollectItems;
    private LinkedList<MachineType> toDropItems;
    private boolean finished;

    public Job(Location location) {
        this.location = location;
        this.toCollectItems = new LinkedList<>();
        this.toDropItems = new LinkedList<>();
        this.finished = false;
    }

    //Copy constructor
    public Job(Job n){
        location = n.location;
        finished = n.finished;
        toCollectItems = new LinkedList<>(n.toCollectItems);
        toDropItems = new LinkedList<>(n.toDropItems);
    }

    public void addToCollectItems(Machine machine){
        toCollectItems.add(machine);
    }

    public void addToDropItems(MachineType machineType){
        toDropItems.add(machineType);
    }

    public boolean hasMachine(MachineType mt){
        for(Machine m: toCollectItems){
            if(m.getType() == mt) return true;
        }
        return false;
    }



    public LinkedList<Machine> getToCollectItems() {
        return toCollectItems;
    }

    public void setToCollectItems(LinkedList<Machine> toCollectItems) {
        this.toCollectItems = toCollectItems;
    }

    public LinkedList<MachineType> getToDropItems() {
        return toDropItems;
    }

    public void setToDropItems(LinkedList<MachineType> toDropItems) {
        this.toDropItems = toDropItems;
    }


    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
