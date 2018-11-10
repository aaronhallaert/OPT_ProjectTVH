package TVH.Entities.Job;

import java.util.Comparator;

public class JobVolumeComparator implements Comparator<Job>{
    @Override
    public int compare(Job j1, Job j2){
        return j2.getVolume() - j1.getVolume();

    }
}
