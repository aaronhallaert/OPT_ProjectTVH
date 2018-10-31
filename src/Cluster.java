import java.util.*;

/**
 * This class creates clusters of nodes based on the k-medoids algorithm (PAM algorithm more specifically) which uses a
 * point in the cluster as node center
 * 
 * https://www.youtube.com/watch?v=OWpRBCrx5-M
 */

public class Cluster {

    private static List<Location> allLocations = new ArrayList<>();
    private static List<Location> allLocationsNonMedoid = new LinkedList<>();
    private static List<Cluster> clusters = new ArrayList<>();
    private static boolean changed;

    List<Location> members = new ArrayList<>();
    List<Location> oldMembers = new ArrayList<>();
    Location medoid;

    /*
     * private constructor, because cluster class is singleton
     * we choose a random location for our medoid from the list of locations that aren't already a medoid.
     */
    private Cluster(){
        int randomLocation = (int) (Math.random()*allLocationsNonMedoid.size());
        medoid = allLocationsNonMedoid.get(randomLocation);
        allLocationsNonMedoid.remove(medoid);

    }

    public static List<Cluster> createClusters(int nClusters, List<Job> jobs){
        //Each job has a location has to be in our cluster;
        for(Job job: jobs){
            Location loc = job.getLocation();
            allLocations.add(loc);
            allLocationsNonMedoid.add(loc);
        }
        
        for (int i = 0; i < nClusters; i++) {
            clusters.add(new Cluster());
        }
        /*
            Creating the clusters consists of 2 steps:
                - Assign all locations to a cluster.
                - Calculate new medoid location of each cluster.
                - Repeat if changes are detected.
         */
        changed = true;
        while(changed) {
            changed = false;
            assignCluster();
            findNewMedoids();
        }
        return clusters;
    }

    /**
     * This method will assign each location to a cluster based on it's proximity to the medoid of that cluster;
     */
    private static void assignCluster(){
        //Save the old members to detect changes later on;
        for (Cluster cluster : clusters) {
            cluster.oldMembers = cluster.members;
            cluster.members.clear();
        }

        //Redistribute the members based on new medoids
        for (Location location : allLocations) {
            getClosestCluster(location).members.add(location);

        }

        //Detect changes in members of cluster
        for (Cluster cluster : clusters) {
            if (!cluster.oldMembers.equals(cluster.members)){
                changed = true;
            }
        }
    }

    /**
     * Calculate which cluster is closest to a given location.
     * @param location
     * @return
     */
    private static Cluster getClosestCluster(Location location){
        double minDistance = Double.POSITIVE_INFINITY;
        Cluster closest = null;

        for (Cluster cluster : clusters) {
            double distance = location.getEdgeMap().get(cluster.medoid).distance;
            if (distance < minDistance){
                minDistance = distance;
                closest = cluster;
            }
        }
        return closest;
    }

    /**
     *  This method finds a new mediods of a cluster. Every member of a cluster is a candidate to be the new medoid.
     *  The member with the smallest overal distance from each other member to itself becomes the new mediod.
     */
    private static void findNewMedoids(){

        for (Cluster cluster : clusters) {
            int minDistance = Integer.MAX_VALUE;
            Location newMedoid = cluster.medoid;

            for (Location medoidCandidate : cluster.members) {
                int distance = 0;
                for(Location member: cluster.members) {
                    Edge edge = medoidCandidate.getEdgeMap().get(member);
                    distance =+ edge.getDistance();
                }
                if(distance < minDistance){
                    minDistance = distance;
                    newMedoid = medoidCandidate;
                    //Detect changes in medoid location
                }
            }
            if(cluster.medoid != newMedoid){
                changed = true;
                cluster.medoid = newMedoid;
            }
        }
    }

    public static List<Cluster> getClusters() {
        return clusters;
    }
}
