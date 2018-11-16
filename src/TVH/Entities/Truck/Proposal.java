package TVH.Entities.Truck;

import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Problem;

/**
 * NEGEER VOORLOPIG
 */

public class Proposal {
    Truck truck;
    Job primaryJob;
    Job secondaryJob;
    Move move;
    Route route;
    int cost;

    public Proposal(Truck truck, Job job, Move move, Route route, int cost) {
        this.truck = truck;
        this.primaryJob = job;
        this.move = move;
        this.route = route;
        this.cost = cost;
        this.secondaryJob = null;

        //Check if primairy job fulfills a secondary one
        if (primaryJob instanceof DropJob) {
            DropJob dj = (DropJob) primaryJob;
            for (Job j : Problem.getInstance().jobTypeMap.get(dj.getMachineType())) {
                if (j instanceof CollectJob && j.getFixedLocation() == move.getCollect()) {
                    secondaryJob = j;
                    break;
                }
            }
        }
        if (primaryJob instanceof CollectJob) {
            CollectJob cj = (CollectJob) primaryJob;
            for (Job j : Problem.getInstance().jobTypeMap.get(cj.getMachineType())) {
                if (j instanceof DropJob && j.getFixedLocation() == move.getDrop()) {
                    secondaryJob = j;
                    break;
                }
            }
        }
    }

    public Truck getTruck() {
        return truck;
    }

    public Job getPrimaryJob() {
        return primaryJob;
    }

    public Job getSecondaryJob() {
        return secondaryJob;
    }

    public Move getMove() {
        return move;
    }

    public int getCost() {
        return cost;
    }


    public Route getRoute() {
        return route;
    }
}
