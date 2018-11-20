package TVH.Entities.Truck;

import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;
import TVH.Entities.Job.Move;
import TVH.Problem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * NEGEER VOORLOPIG
 */

public class Proposal {
    Truck truck;
    Job primaryJob;
    Job secondaryJob;
    Move move;;
    int cost;

    public Proposal(Truck truck, Job job, Move move, int cost) {
        this.truck = truck;
        this.primaryJob = job;
        this.move = move;
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

        //return secondaryJob == null ? cost : cost/2;
        return cost;
    }

    public static List<Proposal> getBestProposalPerJobCombination(List<Proposal> allProposals){
        HashMap<Job, Proposal> bestProposalPerSecondJob = new HashMap<>();
        for(Proposal p: allProposals){
            if(p.getSecondaryJob() == null){
                if(!bestProposalPerSecondJob.containsKey(p.primaryJob)){
                    bestProposalPerSecondJob.put(p.primaryJob, p);
                }
                else if(bestProposalPerSecondJob.get(p.primaryJob).cost > p.cost){
                    bestProposalPerSecondJob.put(p.primaryJob, p);
                }
            }
            else{
                if(!bestProposalPerSecondJob.containsKey(p.secondaryJob)){
                    bestProposalPerSecondJob.put(p.secondaryJob, p);
                }
                else if(bestProposalPerSecondJob.get(p.secondaryJob).cost > p.cost){
                    bestProposalPerSecondJob.put(p.secondaryJob, p);
                }
            }
        }
        return new ArrayList<>(bestProposalPerSecondJob.values());
    }
}
