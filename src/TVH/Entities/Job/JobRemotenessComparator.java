package TVH.Entities.Job;

import java.util.Comparator;

//Comparator die jobs sorteert op basis van hun remotenessfactor
public class JobRemotenessComparator implements Comparator<Job> {
    @Override
    public int compare(Job j1, Job j2){
        return j2.getRemoteFactor() - j1.getRemoteFactor();
    }

}
