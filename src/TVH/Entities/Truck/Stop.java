package TVH.Entities.Truck;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Each truck has stops. These stops define it's route. Stop objects give information about the state of the truck when
 * it is ready to leave stop.
 */
//Dynamische klasse
public class Stop {

    private Location location;
    private LinkedList<Machine> collect;
    private LinkedList<Machine> drop;
    private int ID;


    public Stop(Location location, LinkedList<Machine> collect, LinkedList<Machine> drop) {
        this.location = location;
        this.collect = collect;
        this.drop = drop;
        ID = StopIDGenerator.getInstance().getID();
    }

    //Make single collect stop
    public Stop(Location location){
        this.location = location;
        this.collect = new LinkedList<>();
        this.drop = new LinkedList<>();
        ID = StopIDGenerator.getInstance().getID();

    }

    //Copy constructor
    public Stop(Stop s){
        this.location = s.location;
        this.collect = new LinkedList<>(s.collect);
        this.drop = new LinkedList<>(s.drop);
        ID = StopIDGenerator.getInstance().getID();
    }


    public boolean isEmpty(){
        if(collect.isEmpty() && drop.isEmpty()) return true;
        return false;
    }

    public void addToCollect(Machine m){
        collect.add(m);
    }

    public void addToDrop(Machine m){
        drop.add(m);
    }

    public void removeFromCollect(Machine m){
        collect.remove(m);
    }
    public void removeFromDrop(Machine m){
        drop.remove(m);
    }


    public int getTimeSpend(){
        //TODO:Efficienter maken
        int time = 0;
        for(Machine m: collect){
            time += m.getType().getServiceTime();
        }
        for(Machine m: drop){
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

    public LinkedList<Machine> getCollect() {
        return collect;
    }

    public void setCollect(LinkedList<Machine> collect) {
        this.collect = collect;
    }

    public LinkedList<Machine> getDrop() {
        return drop;
    }

    public void setDrop(LinkedList<Machine> drop) {
        this.drop = drop;
    }

    public String toString(){
        return location.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop stop = (Stop) o;
        return Objects.equals(location, stop.location) &&
                Objects.equals(collect, stop.collect) &&
                Objects.equals(drop, stop.drop);
    }

    @Override
    public int hashCode() {
        return ID;
    }

}
