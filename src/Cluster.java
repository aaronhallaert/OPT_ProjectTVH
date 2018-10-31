import java.util.*;

/**
 * This class creates clusters of nodes based on the k-medoids algorithm (PAM algorithm more specifically) which uses a
 * point in the cluster as node center
 * 
 * https://www.youtube.com/watch?v=OWpRBCrx5-M
 */

public class Cluster {

    private static List<Location> allLocations = new ArrayList<>();
    private static List<Location> allLocationsNotMedoid = new LinkedList<>();
    private static List<Cluster> clusters = new ArrayList<>();
    private static HashMap<Location, Depot> locationDepotMap = new HashMap<>();
    private static HashMap<Location, Job> locationJobMap = new HashMap<>();
    private static boolean changed;

    Set<Location> members = new HashSet<>();
    Set<Location> oldMembers = new HashSet<>();
    Location medoid;


    List<MachineType> machinesNeeded = new LinkedList<>();

    /*
     * private constructor, because cluster class is singleton
     * we choose a random location for our medoid from the list of locations that aren't already a medoid.
     */
    private Cluster(){
        int randomLocation = (int) (Math.random()* allLocationsNotMedoid.size());
        medoid = allLocationsNotMedoid.get(randomLocation);
        allLocationsNotMedoid.remove(medoid);

        members.add(medoid);

    }

    public static List<Cluster> createClusters(int nClusters, HashMap<Location, Job> jobs, List<Depot> depotList){

        for(Job j: jobs.values()){
            allLocations.add(j.getLocation());
        }

        allLocationsNotMedoid.addAll(allLocations);

        //We deep copy the jobs;
        for(Job j: jobs.values()){
            locationJobMap.put(j.getLocation(), new Job(j));
        }

        //We deep copy the depot list
        for(Depot d: depotList){
            locationDepotMap.put(d.getLocation(),new Depot(d));
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
        int timesRun = 0;
        while(changed) {
            changed = false;
            assignCluster();
            findNewMedoids();
            timesRun++;
        }
        System.out.println("Cluster needed " + timesRun +" runs");
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
        for (Location loc : allLocations) {
            getClosestCluster(loc).members.add(loc);

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
     */
    private static Cluster getClosestCluster(Location loc){
        double minDistance = Double.POSITIVE_INFINITY;
        Cluster closest = null;

        for (Cluster cluster : clusters) {
            double distance = loc.getEdgeMap().get(cluster.medoid).distance;
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

    /**
     * This function gives an idea about the remoteness of a certain cluster. It takes the other medoids of other
     * clusters into account aswell as the locationDepotMap.
     * This method is used for sorting, the higher the returned number the more remote.
     * @param depots
     * @return
     */
    public int getRemoteness(List<Depot> depots){
        int remoteFactor = 0;
        for(Cluster c: clusters){
            remoteFactor =+ medoid.getEdgeMap().get(c.medoid).distance;
        }
        for(Depot d: depots){
            remoteFactor =+ medoid.getEdgeMap().get(d.getLocation()).distance;
        }
        return remoteFactor;
    }

    public void calculateMachinesNeeded(){
        for(Location loc: members){
            Job j = locationJobMap.get(loc);
            machinesNeeded.addAll(j.getToDropItems());
            for(Machine m: j.getToCollectItems()){
                machinesNeeded.remove(m.getType());
            }
        }
    }

    public void expand(){
        //We beginnen vanuit de medoid te zoeken naar nodes die dichtbijliggen die de machines hebben die nog tekort zijn.
        List<Edge> sortedEdgeList = medoid.getSortedEdgeList();
        for(Edge e: sortedEdgeList){
            Location loc = e.getTo();
            //Als deze locatie nog geen member is van de cluster:
            if(!members.contains(loc)) {
                //Zoeken als het om een job of een depot gaat:
                if (locationJobMap.containsKey(loc)) {
                    //Job opzoeken op deze locatie
                    Job job = locationJobMap.get(loc);
                    //Alle machines kijken die op deze locatie moeten worden opgenomen;
                    for (Machine m : job.getToCollectItems()) {
                        if (machinesNeeded.contains(m.getType())) {
                            //Als we een machine van dit type nodig hebben dan verwijderen we hem uit de needed lijst en job,
                            //We voegen vervolgens de job toe aan de cluster;
                            machinesNeeded.remove(m.getType());
                            job.removeFromCollectItems(m);
                            members.add(loc);
                        }
                    }
                }
                if (locationDepotMap.containsKey(loc)) {
                    Depot depot = locationDepotMap.get(loc);
                    //We overlopen alle machinetypes die we nodig hebben in de cluster
                    List<MachineType> machinesNeededCopy = new ArrayList<>(machinesNeeded);
                    for (MachineType mt : machinesNeededCopy) {
                        //Als een depot een machine in stock heeft:
                        if (depot.getMachines().containsKey(mt) && depot.getMachines().get(mt).size() > 0) {
                            //Machine verwijderen uit de needed lijst en het depot, locatie van depot toevoegen aan de cluster;
                            machinesNeeded.remove(mt);
                            depot.getMachines().get(mt).removeFirst();
                            members.add(depot.getLocation());
                        }
                    }
                }
            }
        }
    }

    public static List<Cluster> getClusters() {
        return clusters;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Location loc: members){
            sb.append(loc.getLocationID() + ","+loc.getLatitude()+","+loc.getLongitude()+"\n");
        }
        return sb.toString();
    }

}
