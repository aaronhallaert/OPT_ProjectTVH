import java.util.ArrayList;
import java.util.LinkedList;

public class Stop {

    private Job location;
    private int fillRate; //na stop
    private ArrayList<Machine> collectItems;
    private ArrayList<Machine> dropItems;
    private LinkedList<Machine> onTruck;


    public Stop(Job location, ArrayList<Machine> collectItems, ArrayList<Machine> dropItems, LinkedList<Machine> onTruck) {
        this.location = location;
        this.collectItems = collectItems;
        this.dropItems = dropItems;
        this.onTruck = onTruck;
        calculateFillRate();
    }

    //Make single collect stop
    public Stop(Job location, Machine m){
        this.location = location;
        this.collectItems = new ArrayList<>();
        collectItems.add(m);
        this.dropItems = new ArrayList<>();
        this.onTruck = new LinkedList<>();
    }
    public void addCollectItem(Machine m){
        collectItems.add(m);
    }

    public Stop(Stop s){
        this.location = new Job(s.location);
        this.fillRate = s.fillRate;
        this.collectItems = new ArrayList<>(s.collectItems);
        this.dropItems = new ArrayList<>(s.dropItems);
        this.onTruck = new LinkedList<>(s.onTruck);
    }

    public void removeFromTruck(Machine machine){
        onTruck.remove(machine);
        calculateFillRate();
    }

    public void addToTruck(Machine machine){
        onTruck.add(machine);
        calculateFillRate();
    }

    private void calculateFillRate(){
        fillRate = 0;
        for(Machine m: onTruck){
            fillRate =+ m.getType().getVolume();
        }
    }
}
