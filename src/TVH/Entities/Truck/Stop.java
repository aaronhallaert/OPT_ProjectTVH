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
    private int timeSpend = 0;
    boolean changed;


    public Stop(Location location, LinkedList<Machine> collect, LinkedList<Machine> drop) {
        this.location = location;
        this.collect = collect;
        this.drop = drop;
        changed = true;
    }

    public Stop(Location location) {
        this.location = location;
        this.collect = new LinkedList<>();
        this.drop = new LinkedList<>();
        changed = true;
    }

    //Copy constructor
    public Stop(Stop s) {
        this.location = s.location;
        this.collect = new LinkedList<>(s.collect);
        this.drop = new LinkedList<>(s.drop);
        this.changed = s.changed;
        this.timeSpend = s.timeSpend;
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
        changed = true;
    }

    public void addToDrop(Machine m) {
        drop.add(m);
        changed = true;
    }

    public void removeFromCollect(Machine m) {
        collect.remove(m);
        changed = true;

    }

    public void removeFromDrop(Machine m) {
        drop.remove(m);
        changed = true;
    }


    public int getTimeSpend() {
        if(changed) {
            timeSpend = 0;
            for (Machine m : collect) {
                timeSpend += m.getType().getServiceTime();
            }
            for (Machine m : drop) {
                timeSpend += m.getType().getServiceTime();
            }
            changed = false;
        }
        return timeSpend;
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

    /*public void setCollect(LinkedList<Machine> collect) {
        this.collect = collect;
    }*/

    public LinkedList<Machine> getDrop() {

        return drop;
    }

    /*public void setDrop(LinkedList<Machine> drop) {
        this.drop = drop;
    }*/

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
