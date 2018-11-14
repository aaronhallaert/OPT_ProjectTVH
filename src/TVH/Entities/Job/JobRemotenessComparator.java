package TVH.Entities.Job;

import java.util.Comparator;

//Statische klasse
public class JobRemotenessComparator implements Comparator<Job> {
    @Override
    public int compare(Job j1, Job j2){
        return j2.getRemoteFactor() - j1.getRemoteFactor();
    }

}
