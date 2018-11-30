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
 * Een proposal kan vergeleken worden met een offerte die wordt opgemaakt door een truck. Een bepaalde truck zegt dat
 * het een bepaalde Job (en eventueel een 2de Job) kan vervolledigen door een bepaalde Move te implementeren in zijn
 * Route en geeft ook aan hoeveel dit zal kosten.
 */

public class Proposal {
    private final Truck truck;
    private final Job primaryJob;
    private final Job secondaryJob; //Soms vervolledigd een bepaalde move ook een 2de Job
    private final Move move;
    private final int cost;

    public Proposal(Truck truck, Job job, Move move, int cost) {
        this.truck = truck;
        this.primaryJob = job;
        this.move = move;
        this.cost = cost;
        this.secondaryJob = move.completesSecondJob(job);

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
}
