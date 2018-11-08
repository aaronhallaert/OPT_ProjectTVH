package TVH;

import TVH.Entities.*;

import java.util.*;

/**
 * This class creates clusters of nodes based on the K-medoids algorithm (PAM algorithm more specifically) which uses a
 * point in the cluster as cluster center.
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
     * private constructor
     * We choose a random location for our medoid from the list of locations that aren't already a medoid.
     */
    private Cluster(){
        int randomLocation = (int) (Math.random()* allLocationsNotMedoid.size());
        medoid = allLocationsNotMedoid.get(randomLocation);
        allLocationsNotMedoid.remove(medoid);

        members.add(medoid);

    }

    /**
     * This method creates nClusters amount of clusters based on a list of clientMap and depots.
     * @param nClusters amount of clusters needed
     * @param clients list of clientMap
     * @param depotList list of depots
     * @return the newly created clusters;
     */
    public static List<Cluster> createClusters(int nClusters, HashMap<Location, Client> clients, List<Depot> depotList){

        //Copying the clientMap and depots
        for(Client j: clients.values()){
            allLocations.add(j.getLocation());
        }
        allLocationsNotMedoid.addAll(allLocations);

        locationClientMap = clients;
        for(Depot d: depotList){
            locationDepotMap.put(d.getLocation(), d);
        }


        //Creating the clusters
        for (int i = 0; i < nClusters; i++) {
            clusters.add(new Cluster());
        }
        /**
         * Balancing the clusters consists of 2 steps:
         * - Assign all locations to a cluster based on proximity to the medoid.
         * - Calculate new medoid location of each cluster.
         * - Repeat if changes are detected.
         */
        changed = true;
        int timesRun = 0;
        while(changed) {
            changed = false;
            assignCluster();
            searchNewMedoids();
            timesRun++;
        }
        //System.out.println("TVH.Cluster needed " + timesRun +" runs");
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
     * This method calculates which Cluster is closest to a given Location.
     * @param loc given Location
     * @return closest Cluster
     */
    private static Cluster getClosestCluster(Location loc){
        int minDistance = Integer.MAX_VALUE;
        Cluster closest = null;

        for (Cluster cluster : clusters) {
            int distance = loc.getEdgeMap().get(cluster.medoid).getDistance();
            if (distance < minDistance){
                minDistance = distance;
                closest = cluster;
            }
        }
        return closest;
    }

    /**
     *  This method searches the new mediod of a cluster. Every member of a cluster is a candidate to be the new medoid.
     *  The member with the smallest overall distance from each other member to itself becomes the new mediod.
     */
    private static void searchNewMedoids(){
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
     * @return The futher a cluster lies from other clusters and from depots the higher this number will be.
     */
    public int getRemoteness(List<Depot> depots){
        int remoteFactor = 0;
        for(Cluster c: clusters){
            //How far away a given mediod in a cluster is from other mediods.
            remoteFactor += medoid.getEdgeMap().get(c.medoid).getDistance();
        }
        for(Depot d: depots){
            //How far away a given mediod is from the depots on the map.
            remoteFactor += medoid.getEdgeMap().get(d.getLocation()).getDistance();
        }
        return remoteFactor;
    }

    /**
     * This method calculates which machinesTypes are deficit.
     * For a cluster to be self sufficient, it needs to have every machine it needs inside it.
     */
    public void calculateMachinesNeeded(){
        for(Location loc: members){
            Client j = locationClientMap.get(loc);
            machinesNeeded.addAll(j.getToDropItems());
            for(Machine m: j.getToCollectItems()){
                machinesNeeded.remove(m.getType());
            }
        }
    }

    /**
     * This method expands a cluster until it is self sufficient (it doesn't need any machines from the outside).
     */
    public void expand(){
        //Deep copy the clientMap and depots into hashmaps so certain machines can deleted from clientMap/depots temporarily.
        HashMap<Location, Client> locationClientMapCopy = new HashMap<>();
        HashMap<Location, Depot> locationDepotMapCopy = new HashMap<>();

        for(Client j: locationClientMap.values()){
            locationClientMapCopy.put(j.getLocation(), new Client(j));
        }

        for(Depot d: locationDepotMap.values()){
            locationDepotMapCopy.put(d.getLocation(),new Depot(d));
        }


        //Start by looking at the closest Locations that are not part of the cluster
        List<Edge> sortedEdgeList = medoid.getSortedEdgeList();
        for(Edge e: sortedEdgeList){
            Location loc = e.getTo();
            //Check if this location is not yet part of the cluster
            if(!members.contains(loc)) {
                //Check if this location is a Client or a Depot.
                if (locationClientMapCopy.containsKey(loc)) {
                    Client client = locationClientMapCopy.get(loc);
                    //If it's a client, check if the cluster needs any of the machines that need to be collected.
                    for (Machine m : client.getToCollectItems()) {
                        //If a machine is this type is needed:
                        if (machinesNeeded.contains(m.getType())) {
                            //Delete it from the client job (temporarily) and machinesNeeded
                            machinesNeeded.remove(m.getType());
                            client.removeFromCollectItems(m);
                            //Add the client to the expandedMembers list of the cluster;
                            expandedMembers.add(loc);
                        }
                    }
                }
                if (locationDepotMapCopy.containsKey(loc)) {
                    Depot depot = locationDepotMapCopy.get(loc);
                    //We go through every machineType we still need inside the cluster
                    List<MachineType> machinesNeededCopy = new ArrayList<>(machinesNeeded);
                    for (MachineType mt : machinesNeededCopy) {
                        //If the depot still has a machine of this type in stock
                        if (depot.getMachines().containsKey(mt)) {
                            //Delete it from the depot (temporarily) and machinesNeeded;
                            machinesNeeded.remove(mt);
                            depot.removeMachine(mt);
                            //Add the depot to the depots of the cluster;
                            depots.add(depot.getLocation());
                        }
                    }
                }
            }
        }
    }

    /**
     * This method solves a certain cluster
     * @param trucks list of trucks that can be used to accomplish this.
     */
    public void solve(List<Truck> trucks){

        /*
         * First step of the solving the cluster is determining which machine goes where. This is saved in a "Job" object.
         * Since every cluster is self sufficient we only need to move machines to and from other members of the cluster.
         *
         * Second step is to determine which Truck can do which move.
         */


        //Sort the members based on their distance to the medoid. Members who are further away from the medoid get priority.
        ArrayList<Location> membersList = new ArrayList<>(members);
        membersList.sort((Location l1, Location l2)->l2.distanceTo(medoid)-l1.distanceTo(medoid));
        //Go over every location where a drop has to be made.
        List<Move> movesList = new ArrayList<>();
        for(Location drop: membersList){
            for(MachineType mt: locationClientMap.get(drop).getToDropItems()){
                //Search for each machineType needed what the closest location in this cluster is that has this machineType.
                //There are 2 possibilities: Depot contains machine of type, Client contains a machine of this type that needs to be collected
                for(Edge e: drop.getSortedEdgeList()){
                    if(members.contains(e.getTo()) || expandedMembers.contains(e.getTo()) || depots.contains(e.getTo())) {
                        //In case the location is a Client
                        if (locationClientMap.containsKey(e.getTo())) {
                            Client c = locationClientMap.get(e.getTo());
                            if (c.collectItemsContains(mt)) {
                                Machine m = c.getMachineToCollect(mt);
                                //Add the move to the list and delete machine from items that need to be collected.
                                movesList.add(new Move(m, c.getLocation(), drop));
                                c.removeFromCollectItems(m);
                                //A location is found, break the for loop to go the next machine;
                                break;
                            }
                            continue;
                        }
                        //In case the location is a depot
                        if (locationDepotMap.containsKey(e.getTo())) {
                            Depot d = locationDepotMap.get(e.getTo());
                            if (d.hasMachine(mt)) {
                                Machine m = d.getMachineFromDepot(mt);
                                //Add the move to the list and delete machine from the depot.
                                movesList.add(new Move(d.getMachineFromDepot(mt), d.getLocation(), drop));
                                d.removeMachine(m);
                                //A location is found, break the for loop to go the next machine;
                                break;
                            }
                        }
                    }

                }

            }
        }
        //After all the drop jobs are made, the collect jobs are made.
        for(Location collect: membersList){
            for(Machine m: locationClientMap.get(collect).getToCollectItems()){
                for(Edge e: collect.getSortedEdgeList()){
                    //We search the closest depot to drop the machine
                    if(locationDepotMap.containsKey(e.getTo())){
                        movesList.add(new Move(m, collect, e.getTo()));
                        break;
                    }
                }
            }
        }

        //Next step is to assign jobs to trucks;

        //Sort jobs
        movesList.sort(Comparator.comparing(Move::getRemoteFactor).reversed());

        Set<Machine> machinesMoved = new HashSet<>();
        for(Move m: movesList){
            if(!machinesMoved.add(m.getMachine())){
                System.out.println("howla");
            }
        }

        //Assign each move to a truck
        for(Move m: movesList){

            /*//First we check if we have any trucks that are passing in either both origin and destination of the move
            //or one of the two. We check these trucks first, because they are already passing at one of both locations.
            ArrayList<Truck> trucksPassingBoth = new ArrayList<>();
            ArrayList<Truck> trucksPassingOne = new ArrayList<>();
            for(Truck t: truckMap.values()){
                if(t.doesTruckPass(m.getFrom()) && t.doesTruckPass(m.getTo())){
                    trucksPassingBoth.add(t);
                }else if(t.doesTruckPass(m.getFrom()) || t.doesTruckPass(m.getTo())){
                    trucksPassingOne.add(t);
                }
            }
            //We merge them together in a list, trucks that pass both locations are in the front of the list;
            LinkedList<Truck> sortedTruckList = new LinkedList<>();
            sortedTruckList.addAll(trucksPassingBoth);
            sortedTruckList.addAll(trucksPassingOne);

            //All other available trucks (not passing) are added to
            //the list sorted by their proximity to the "from" Location.
            for(Edge e: from.getSortedEdgeList()){
                if (truckMap.containsKey(e.getTo())){
                    for(Truck t: truckMap.get(e.getTo())){
                        if(!sortedTruckList.contains(t)) sortedTruckList.add(t);
                    }
                }
            }*/
            LinkedList<TruckPreference> sortedTruckList = new LinkedList<>();
            for(Truck t: trucks){
                sortedTruckList.add(new TruckPreference(t, m));
            }
            sortedTruckList.sort(Comparator.comparing(TruckPreference::getPreference));

            //Go through the sortedTruckList until a truck is found that is able to handle the move without breaking any
            //constraints.
            boolean truckFound = false;
            while(!sortedTruckList.isEmpty()){
                Truck selected = sortedTruckList.getFirst().getTruck();
                //Make a deep copy backup to roll back the truck in case it can't handle the move;
                Truck backup = new Truck(selected);
                if(selected.doDropMove(m)){
                    //Truck was able to handle the move without breaking any constraints;
                    truckFound = true;
                    break;
                }
                else {
                    //Truck was not able to handle the move without breaking any constraints;
                    sortedTruckList.removeFirst();
                    //Truck needs to be rolled back to previous state;
                    selected.rollBack(backup);
                }
            }
            //If a truck was found to do the move, we go to the next move
            if(sortedTruckList.isEmpty() && !truckFound) {
                System.out.println(m);
            }


            //If we reach this part of the code, it means that the move can't be executed by any available trucks.
            //🤔🤔🤔🤔🤔🤔



        }
        //System.out.println("done");

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

    public Set<Location> getMembers() {
        return members;
    }

    public static void resetStaticFields(){
        //TODO: beter oplossing zoeken hiervoor
        allLocations = new ArrayList<>();
        allLocationsNotMedoid = new LinkedList<>();
        clusters = new ArrayList<>();
        locationDepotMap = new HashMap<>();
        locationClientMap = new HashMap<>();
    }
}
