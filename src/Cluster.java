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
    Set<Location> depots = new HashSet<>();
    Set<Location> expandedMembers = new HashSet<>();
    Set<Location> oldMembers = new HashSet<>();
    Location medoid;


    List<MachineType> machinesNeeded = new LinkedList<>();
    Map<Location, Stop> certainStops = new HashMap<>();

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

        locationJobMap = jobs;
        for(Depot d: depotList){
            locationDepotMap.put(d.getLocation(), d);
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
            cluster.members = new HashSet<>();
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
        int minDistance = Integer.MAX_VALUE;
        Cluster closest = null;

        for (Cluster cluster : clusters) {
            int distance = loc.getEdgeMap().get(cluster.medoid).distance;
            if (distance < minDistance){
                minDistance = distance;
                closest = cluster;
            }
        }
        return closest;
    }

    /**
     *  This method finds a new mediod of a cluster. Every member of a cluster is a candidate to be the new medoid.
     *  The member with the smallest overal distance from each other member to itself becomes the new mediod.
     */
    private static void findNewMedoids(){
        int minDistance, distance;

        for (Cluster cluster : clusters) {
            minDistance = Integer.MAX_VALUE;
            Location newMedoid = cluster.medoid;

            for (Location medoidCandidate : cluster.members) {
                distance = 0;
                for(Location member : cluster.members) {
                    distance += medoidCandidate.getEdgeMap().get(member).getDistance();
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
     * clusters into account as well as the locationDepotMap.
     * This method is used for sorting, the higher the returned number the more remote.
     * @param depots
     * @return
     */
    public int getRemoteness(List<Depot> depots){
        int remoteFactor = 0;
        for(Cluster c: clusters){
//            How far a given mediod in a cluster is from other mediods.
            remoteFactor += medoid.getEdgeMap().get(c.medoid).distance;
        }
        for(Depot d: depots){
//            How far a given mediod is from the depots on the map.
            remoteFactor += medoid.getEdgeMap().get(d.getLocation()).distance;
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
        //We deep copy the jobs and depots so we can delete certain machines from jobs temporarily;
        HashMap<Location, Job> locationJobMapCopy = new HashMap<>();
        HashMap<Location, Depot> locationDepotMapCopy = new HashMap<>();

        for(Job j: locationJobMap.values()){
            locationJobMapCopy.put(j.getLocation(), new Job(j));
        }

        for(Depot d: locationDepotMap.values()){
            locationDepotMapCopy.put(d.getLocation(),new Depot(d));
        }


        //We beginnen vanuit de medoid te zoeken naar nodes die dichtbijliggen die de machines hebben die nog tekort zijn.
        List<Edge> sortedEdgeList = medoid.getSortedEdgeList();
        for(Edge e: sortedEdgeList){
            Location loc = e.getTo();
            //Als deze locatie nog geen member is van de cluster:
            if(!members.contains(loc)) {
                //Zoeken als het om een job of een depot gaat:
                if (locationJobMapCopy.containsKey(loc)) {
                    //Job opzoeken op deze locatie
                    Job job = locationJobMapCopy.get(loc);
                    //Alle machines kijken die op deze locatie moeten worden opgenomen;
                    for (Machine m : job.getToCollectItems()) {
                        if (machinesNeeded.contains(m.getType())) {
                            //Als we een machine van dit type nodig hebben dan verwijderen we hem uit de needed lijst en job,
                            //We voegen vervolgens de job toe aan de cluster;
                            machinesNeeded.remove(m.getType());
                            job.removeFromCollectItems(m);
                            expandedMembers.add(loc);
                        }
                    }
                }
                if (locationDepotMapCopy.containsKey(loc)) {
                    Depot depot = locationDepotMapCopy.get(loc);
                    //We overlopen alle machinetypes die we nodig hebben in de cluster
                    List<MachineType> machinesNeededCopy = new ArrayList<>(machinesNeeded);
                    for (MachineType mt : machinesNeededCopy) {
                        //Als een depot een machine in stock heeft:
                        if (depot.getMachines().containsKey(mt) && depot.getMachines().get(mt).size() > 0) {
                            //Machine verwijderen uit de needed lijst en het depot, locatie van depot toevoegen aan de cluster;
                            machinesNeeded.remove(mt);
                            depot.getMachines().get(mt).removeFirst();
                            depots.add(depot.getLocation());
                        }
                    }
                }
            }
        }
    }

    public void solve(){
        //We sorteren de members op basis van hun afstand van de medoid.
        ArrayList<Location> membersList = new ArrayList<>(members);
        membersList.sort((Location l1, Location l2)->l2.distanceTo(medoid)-l1.distanceTo(medoid));
        //We overlopen alle locations die moeten gedropt worden
        List<Move> machineMoves = new ArrayList<>();
        for(Location drop: membersList){
            for(MachineType mt: locationJobMap.get(drop).getToDropItems()){
                for(Edge e: drop.getSortedEdgeList()){
                    if(members.contains(e.getTo()) || expandedMembers.contains(e.getTo()) || depots.contains(e.getTo())) {
                        if (locationJobMap.containsKey(e.getTo())) {
                            Job j = locationJobMap.get(e.getTo());
                            if (j.collectItemsContains(mt)) {
                                Machine m = j.getMachineToCollect(mt);
                                machineMoves.add(new Move(m, j.getLocation(), drop));
                                j.removeFromCollectItems(m);
                                break;
                            }
                            continue;
                        }
                        if (locationDepotMap.containsKey(e.getTo())) {
                            Depot d = locationDepotMap.get(e.getTo());
                            if (d.hasMachine(mt)) {
                                Machine m = d.getMachineFromDepot(mt);
                                machineMoves.add(new Move(d.getMachineFromDepot(mt), d.getLocation(), drop));
                                d.removeMachine(m);
                                break;
                            }
                        }
                    }

                }
                System.out.println("ERROR, NO MACHINE FOUND");

            }
        }
        System.out.println("memberslist");


    }

    public static List<Cluster> getClusters() {
        return clusters;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Members: \n");
        for(Location loc: members){
            sb.append(loc.getLocationID() + ","+loc.getLatitude()+","+loc.getLongitude()+"\n");
        }
        sb.append("Depots: \n");
        for(Location loc: depots){
            sb.append(loc.getLocationID() + ","+loc.getLatitude()+","+loc.getLongitude()+"\n");
        }
        sb.append("Expanded: \n");
        for(Location loc: expandedMembers){
            sb.append(loc.getLocationID() + ","+loc.getLatitude()+","+loc.getLongitude()+"\n");
        }

        return sb.toString();
    }

}
