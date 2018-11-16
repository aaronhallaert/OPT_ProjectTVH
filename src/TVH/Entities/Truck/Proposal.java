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
    Job j1;
    Job j2;
    Move m;
    int cost;

    public Proposal(Truck truck, Job job, Move m, int cost) {
        this.truck = truck;
        this.j1 = job;
        this.m = m;
        this.cost = cost;
        this.j2 = null;

        if(j1 instanceof DropJob){
            DropJob dj = (DropJob) j1;
            for(Job j: Problem.getInstance().jobTypeMap.get(dj.getMachineType())){
                if(j instanceof CollectJob && j.getFixedLocation() == m.getCollect()){
                    j2 = j;
                    break;
                }
            }
        }
        if(j1 instanceof CollectJob){
            CollectJob cj = (CollectJob) j1;
            for(Job j: Problem.getInstance().jobTypeMap.get(cj.getMachineType())){
                if(j instanceof DropJob && j.getFixedLocation() == m.getDrop()){
                    j2 = j;
                    break;
                }
            }
        }
    }

    public Truck getTruck() {
        return truck;
    }

    public Job getJ1() {
        return j1;
    }

    public Job getJ2() {
        return j2;
    }

    public Move getM() {
        return m;
    }

    public int getCost() {
        if(j2 == null) return cost;
        //Indien deze proposal 2 jobs voltooid, delen we de kost in 2;
        else return cost/2;
    }
}
