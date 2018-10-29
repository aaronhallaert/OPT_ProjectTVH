import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Stop {

    private Job locatie;
    private int fillRate; //na stop
    private ArrayList<Machine> collectItems;
    private ArrayList<Machine> dropItems;
    private LinkedList<Machine> onTruck;


    public Stop(Job locatie, ArrayList<Machine> collectItems, ArrayList<Machine> dropItems, LinkedList<Machine> onTruck) {
        this.locatie = locatie;
        this.collectItems = collectItems;
        this.dropItems = dropItems;
        this.onTruck = onTruck;
        calculateFillRate();
    }

    public Stop(Stop s){
        this.locatie = new Job(s.locatie);
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
