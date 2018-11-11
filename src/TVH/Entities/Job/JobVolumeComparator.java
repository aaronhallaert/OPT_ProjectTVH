package TVH.Entities.Job;

import java.util.Comparator;

//Statische klasse
public class JobVolumeComparator implements Comparator<Job>{
    @Override
    public int compare(Job j1, Job j2){
        return j2.getMachineType().getVolume() - j1.getMachineType().getVolume();
    }
}
