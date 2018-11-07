package TVH.Entities;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Each truck has stops. These stops define it's route. Stop objects give information about the state of the truck when
 * it is ready to leave stop.
 */

public class Stop {

    private Location location;
    private int fillRate; //na stop
    private ArrayList<Machine> collectItems;
    private ArrayList<Machine> dropItems;
    private LinkedList<Machine> onTruck;


    public Stop(Location location, ArrayList<Machine> collectItems, ArrayList<Machine> dropItems, LinkedList<Machine> onTruck) {
        this.location = location;
        this.collectItems = collectItems;
        this.dropItems = dropItems;
        this.onTruck = onTruck;
        calculateFillRate();
    }

    //Make single collect stop
    public Stop(Location location){
        this.location = location;
        this.collectItems = new ArrayList<>();
        this.dropItems = new ArrayList<>();
        this.onTruck = new LinkedList<>();
    }
    public void addCollectItem(Machine m){
        collectItems.add(m);
    }

    public void addDropItem(Machine m){
        dropItems.add(m);
    }

    public Stop(Stop s){
        this.location = s.location;
        this.fillRate = s.fillRate;
        this.collectItems = new ArrayList<>(s.collectItems);
        this.dropItems = new ArrayList<>(s.dropItems);
        this.onTruck = new LinkedList<>(s.onTruck);
    }

    public void removeFromTruck(Machine machine){
        onTruck.remove(machine);
        calculateFillRate();
    }

    public boolean addToTruck(Machine machine){
        onTruck.add(machine);
        return calculateFillRate();
    }

    public boolean calculateFillRate(){
        //TODO:Efficienter maken;
        fillRate = 0;
        for(Machine m: onTruck){
            fillRate += m.getType().getVolume();
        }
        return fillRate <= 100;
    }
    public int getTimeSpend(){
        //TODO:Efficienter maken
        int time = 0;
        for(Machine m: collectItems){
            time += m.getType().getServiceTime();
        }
        for(Machine m: dropItems){
            time += m.getType().getServiceTime();
        }
        return time;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getFillRate() {
        return fillRate;
    }

    public void setFillRate(int fillRate) {
        this.fillRate = fillRate;
    }

    public ArrayList<Machine> getCollectItems() {
        return collectItems;
    }

    public void setCollectItems(ArrayList<Machine> collectItems) {
        this.collectItems = collectItems;
    }

    public ArrayList<Machine> getDropItems() {
        return dropItems;
    }

    public void setDropItems(ArrayList<Machine> dropItems) {
        this.dropItems = dropItems;
    }

    public LinkedList<Machine> getOnTruck() {
        return onTruck;
    }

    public void setOnTruck(LinkedList<Machine> onTruck) {
        this.onTruck = onTruck;
    }

}
