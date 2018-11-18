package TVH.Entities.Job;

import TVH.Entities.Node.Location;

import java.util.Comparator;

//Comparator die jobs sorteert op basis van hun afstand van een bepaalde locatie "toCompare"
public class JobLocationComparator implements Comparator<Job> {
    Location toCompare;

    public JobLocationComparator(Location toCompare) {
        this.toCompare = toCompare;
    }

    @Override
    public int compare(Job j1, Job j2){
        return j1.getFixedLocation().distanceTo(toCompare) - j2.getFixedLocation().distanceTo(toCompare);
    }

}
