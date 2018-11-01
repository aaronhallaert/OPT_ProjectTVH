import com.google.common.collect.HashMultimap;

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
    private static HashMap<Location, Client> locationClientMap = new HashMap<>();
    private static boolean changed;

    Set<Location> members = new HashSet<>();
    Set<Location> depots = new HashSet<>();
    Set<Location> expandedMembers = new HashSet<>();
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

    public static List<Cluster> createClusters(int nClusters, HashMap<Location, Client> jobs, List<Depot> depotList){

        for(Client j: jobs.values()){
            allLocations.add(j.getLocation());
        }

        allLocationsNotMedoid.addAll(allLocations);

        locationClientMap = jobs;
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
            Client j = locationClientMap.get(loc);
            machinesNeeded.addAll(j.getToDropItems());
            for(Machine m: j.getToCollectItems()){
                machinesNeeded.remove(m.getType());
            }
        }
    }

    public void expand(){
        //We deep copy the jobs and depots so we can delete certain machines from jobs temporarily;
        HashMap<Location, Client> locationJobMapCopy = new HashMap<>();
        HashMap<Location, Depot> locationDepotMapCopy = new HashMap<>();

        for(Client j: locationClientMap.values()){
            locationJobMapCopy.put(j.getLocation(), new Client(j));
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
                    //Client opzoeken op deze locatie
                    Client client = locationJobMapCopy.get(loc);
                    //Alle machines kijken die op deze locatie moeten worden opgenomen;
                    for (Machine m : client.getToCollectItems()) {
                        if (machinesNeeded.contains(m.getType())) {
                            //Als we een machine van dit type nodig hebben dan verwijderen we hem uit de needed lijst en client,
                            //We voegen vervolgens de client toe aan de cluster;
                            machinesNeeded.remove(m.getType());
                            client.removeFromCollectItems(m);
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
        /*
         * First step of the solution is determining which machine goes where. This is saved in a "Move" object.
         * Since every cluster is self sufficient we only need to move machines to and from other members of the cluster.
         *
         * Second step is to determine which Truck does which move.
         */


        //Sort the members based on their distance to the medoid.
        ArrayList<Location> membersList = new ArrayList<>(members);
        membersList.sort((Location l1, Location l2)->l2.distanceTo(medoid)-l1.distanceTo(medoid));
        //Go over every location where a drop must be made
        List<Move> movesList = new ArrayList<>();
        for(Location drop: membersList){
            for(MachineType mt: locationClientMap.get(drop).getToDropItems()){
                //Search for each machineType needed, the closest member of the cluster that has this machineType
                for(Edge e: drop.getSortedEdgeList()){
                    if(members.contains(e.getTo()) || expandedMembers.contains(e.getTo()) || depots.contains(e.getTo())) {
                        //In case the location is a Client
                        if (locationClientMap.containsKey(e.getTo())) {
                            Client j = locationClientMap.get(e.getTo());
                            if (j.collectItemsContains(mt)) {
                                Machine m = j.getMachineToCollect(mt);
                                movesList.add(new Move(m, j.getLocation(), drop));
                                j.removeFromCollectItems(m);
                                break;
                            }
                            continue;
                        }
                        //In case the location is a depot
                        if (locationDepotMap.containsKey(e.getTo())) {
                            Depot d = locationDepotMap.get(e.getTo());
                            if (d.hasMachine(mt)) {
                                Machine m = d.getMachineFromDepot(mt);
                                movesList.add(new Move(d.getMachineFromDepot(mt), d.getLocation(), drop));
                                d.removeMachine(m);
                                break;
                            }
                        }
                    }

                }

            }
        }
        //After all the drop moves are made, the collect moves are made.
        for(Location collect: membersList){
            for(Machine m: locationClientMap.get(collect).getToCollectItems()){
                for(Edge e: collect.getSortedEdgeList()){
                    //We search the closest depot to drop the machine
                    if(depots.contains(e.getTo())){
                        movesList.add(new Move(m, collect, e.getTo()));
                        break;
                    }
                }
            }
        }
        System.out.println("Moves created");

        //Next step is to let the trucks handle the moves
        //First step is making a list of the available trucks to handle the moves;
        HashMultimap<Location, Truck> trucks = HashMultimap.create();
        for(Truck t: Problem.trucks){
            //Check if truck is not yet being used by other cluster;
            if(!t.isUsed()){
                trucks.put(t.getStartLocation(),t);
            }
        }
        for(Move m: movesList){
            Location from = m.getFrom();
            //First we check if we have any trucks that are passing in either both origin and destination of the move
            //Or one of the two
            ArrayList<Truck> trucksPassingBoth = new ArrayList<>();
            ArrayList<Truck> trucksPassingOne = new ArrayList<>();
            for(Truck t: trucks.values()){
                if(t.doesTruckPass(m.getFrom()) && t.doesTruckPass(m.getTo())){
                    trucksPassingBoth.add(t);
                }else if(t.doesTruckPass(m.getFrom()) || t.doesTruckPass(m.getTo())){
                    trucksPassingOne.add(t);
                }
            }
            //We merge them together in a list;
            LinkedList<Truck> sortedTruckList = new LinkedList<>();
            sortedTruckList.addAll(trucksPassingBoth);
            sortedTruckList.addAll(trucksPassingOne);
            boolean truckFound = false;
            while(!sortedTruckList.isEmpty()){
                //First we need to select a truck to do the move;
                Truck selected = sortedTruckList.getFirst();
                //Make a deep copy backup to roll back the truck in case it can't handle the move;
                Truck backup = new Truck(selected);
                if(selected.doMove(m)){
                    truckFound = true;
                    break;
                }
                else {
                    sortedTruckList.removeFirst();
                    selected = backup;
                }
            }
            //If a truck was found to do the move, we go to the next move
            if(truckFound) continue;

            /*
                If we reach this part of the code, it means that the move can't be executed by any of the trucks that
                are passing through one or both of the locations of the move. The next step is to make a sortedlist
                of the trucks who are passing somewhere near the origin or destination;
             */

            //TODO: dit hier fixen grt
            System.out.println("Wooooops");

        }
        System.out.println("done");

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
