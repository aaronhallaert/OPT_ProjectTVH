package TVH.Entities.Truck;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Elke Route bevat een lijst van Stops. Deze bepalen waar de truck passeert en wat hij doet op die locatie.
 */

public class Stop {

    private final Location location;
    private final LinkedList<Machine> collect;
    private final LinkedList<Machine> drop;
    private int timespend = 0;
    private int deltaFillRate = 0; //Hoeveelheid waarmee de fillrate van de truck verandert na deze stop

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

    /**
     * Merge 2 stops met elkaar
     * @param s Stop die in deze stop gemerged moet worden
     */
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
        return collect.isEmpty() && drop.isEmpty();
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

    public boolean removeFromCollect(Machine m) {
        if(collect.remove(m)) {

            timespend -= m.getType().getServiceTime();
            deltaFillRate -= m.getType().getVolume();
            return true;
        }
        return false;
    }

    public boolean removeFromDrop(Machine m) {
        if(drop.remove(m)) {

            timespend -= m.getType().getServiceTime();
            deltaFillRate += m.getType().getVolume();
            return true;
        }
        return false;
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

    public LinkedList<Machine> getCollect() {
        return collect;
    }

    public LinkedList<Machine> getDrop() {
        return drop;
    }


    public String toString() {
        return location.toString();
    }

}
