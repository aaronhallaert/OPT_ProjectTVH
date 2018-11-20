package TVH.Entities.Job;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;
import TVH.Problem;

import java.util.Objects;

/**
 * Een Move geeft aan dat een machine van locatie "collect" naar locatie "drop" moet vervoerd worden.
 * Het geeft geen info over welke job het daarmee voltooid, of welke truck deze Move moet uitvoeren.
 */
//Statische klasse

public class Move {
    Machine machine;
    Location collect;
    Location drop;

    public Move(Machine machine, Location collect, Location drop) {
        this.machine = machine;
        this.collect = collect;
        this.drop = drop;
    }

    public Machine getMachine() {
        return machine;
    }

    public Location getCollect() {
        return collect;
    }

    public Location getDrop() {
        return drop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return Objects.equals(machine, move.machine) &&
                Objects.equals(collect, move.collect) &&
                Objects.equals(drop, move.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machine, collect, drop);
    }

    public Job completesSecondJob(Job primaryJob){
        Job secondaryJob = null;
        //Check if primairy job fulfills a secondary one
        if (primaryJob instanceof DropJob) {
            DropJob dj = (DropJob) primaryJob;
            for (Job j : Problem.getInstance().jobTypeMap.get(dj.getMachineType())) {
                if (j instanceof CollectJob && j.getFixedLocation() == collect) {
                    secondaryJob = j;
                    break;
                }
            }
        }
        if (primaryJob instanceof CollectJob) {
            CollectJob cj = (CollectJob) primaryJob;
            for (Job j : Problem.getInstance().jobTypeMap.get(cj.getMachineType())) {
                if (j instanceof DropJob && j.getFixedLocation() == drop) {
                    secondaryJob = j;
                    break;
                }
            }
        }
        return secondaryJob;
    }
}
