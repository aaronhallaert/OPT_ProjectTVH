package TVH.Entities.Truck;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Elke Route bevat een lijst van Stops. Deze bepalen waar de truck rijdt en wat hij doet op die locatie.
 * Het is een dynamische klasse
 */

public class Stop {

    private Location location;
    private LinkedList<Machine> collect;
    private LinkedList<Machine> drop;
    private int timespend = 0;
    private int deltaFillRate = 0;

    public Stop(Location location) {
        this.location = location;
        this.collect = new LinkedList<>();
        this.drop = new LinkedList<>();

    }

    //Copy constructor
    public Stop(Stop s) {
        this.location = s.location;
        this.collect = new LinkedList<>(s.collect);
        this.drop = new LinkedList<>(s.drop);
        this.timespend = s.timespend;
        this.deltaFillRate = s.deltaFillRate;
    }

    public void merge(Stop s){
        collect.addAll(s.getCollect());
        drop.addAll(s.getDrop());
        timespend += s.getTimeSpend();
        deltaFillRate += s.getDeltaFillRate();
    }

    /**
     * Checkt als er iets gebeurt op deze stop
     * @return true als er niets wordt uitgevoerd
     */
    public boolean isEmpty() {
        if (collect.isEmpty() && drop.isEmpty()) return true;
        return false;
    }

    public void addToCollect(Machine m) {
        collect.add(m);

        timespend += m.getType().getServiceTime();
        deltaFillRate += m.getType().getVolume();
    }

    public void addToDrop(Machine m) {
        drop.add(m);

        timespend += m.getType().getServiceTime();
        deltaFillRate -= m.getType().getVolume();
    }

    public void removeFromCollect(Machine m) {
        if(collect.remove(m)) {

            timespend -= m.getType().getServiceTime();
            deltaFillRate -= m.getType().getVolume();
        }
    }

    public void removeFromDrop(Machine m) {
        if(drop.remove(m)) {

            timespend -= m.getType().getServiceTime();
            deltaFillRate += m.getType().getVolume();
        }
    }


    public int getTimeSpend() {
        return timespend;
    }

    public int getDeltaFillRate(){
        return deltaFillRate;
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


    public String toString() {
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
        return Objects.hash(location, collect, drop);
    }
}
