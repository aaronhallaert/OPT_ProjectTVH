package SolutionEntities;

import Entities.*;
import com.google.common.collect.HashMultimap;

import java.util.*;

public class JernCluster {
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
    public JernCluster(Cluster c){
        for (Client clusterClient : c.getClusterClients()) {
            allLocations.add(clusterClient.getLocation());
            members.add(clusterClient.getLocation());
            locationClientMap.put(clusterClient.getLocation(), clusterClient);
        }
        for (Depot clusterDepot : c.getClusterDepots()) {
            allLocations.add(clusterDepot.getLocation());
            members.add(clusterDepot.getLocation());
            depots.add(clusterDepot.getLocation());
            locationDepotMap.put(clusterDepot.getLocation(), clusterDepot);
        }

        this.medoid= c.getMainDepot().getLocation();
        allLocationsNotMedoid.addAll(allLocations);
        allLocationsNotMedoid.remove(c.getMainDepot().getLocation());
        changed=false;


    }


    public static List<Location> getAllLocations() {
        return allLocations;
    }

    public static void setAllLocations(List<Location> allLocations) {
        JernCluster.allLocations = allLocations;
    }

    public static List<Location> getAllLocationsNotMedoid() {
        return allLocationsNotMedoid;
    }

    public static void setAllLocationsNotMedoid(List<Location> allLocationsNotMedoid) {
        JernCluster.allLocationsNotMedoid = allLocationsNotMedoid;
    }

    public static void setClusters(List<Cluster> clusters) {
        JernCluster.clusters = clusters;
    }

    public static HashMap<Location, Depot> getLocationDepotMap() {
        return locationDepotMap;
    }

    public static void setLocationDepotMap(HashMap<Location, Depot> locationDepotMap) {
        JernCluster.locationDepotMap = locationDepotMap;
    }

    public static HashMap<Location, Client> getLocationClientMap() {
        return locationClientMap;
    }

    public static void setLocationClientMap(HashMap<Location, Client> locationClientMap) {
        JernCluster.locationClientMap = locationClientMap;
    }

    public static boolean isChanged() {
        return changed;
    }

    public static void setChanged(boolean changed) {
        JernCluster.changed = changed;
    }

    public void setMembers(Set<Location> members) {
        this.members = members;
    }

    public Set<Location> getDepots() {
        return depots;
    }

    public void setDepots(Set<Location> depots) {
        this.depots = depots;
    }

    public Set<Location> getExpandedMembers() {
        return expandedMembers;
    }

    public void setExpandedMembers(Set<Location> expandedMembers) {
        this.expandedMembers = expandedMembers;
    }

    public Set<Location> getOldMembers() {
        return oldMembers;
    }

    public void setOldMembers(Set<Location> oldMembers) {
        this.oldMembers = oldMembers;
    }

    public Location getMedoid() {
        return medoid;
    }

    public void setMedoid(Location medoid) {
        this.medoid = medoid;
    }

    public List<MachineType> getMachinesNeeded() {
        return machinesNeeded;
    }

    public void setMachinesNeeded(List<MachineType> machinesNeeded) {
        this.machinesNeeded = machinesNeeded;
    }

    /**
     * This method solves a certain cluster
     * @param trucks list of trucks that can be used to accomplish this.
     */
    public void solve(List<Truck> trucks){

        /*
         * First step of the solving the cluster is determining which machine goes where. This is saved in a "Move" object.
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
        //After all the drop moves are made, the collect moves are made.
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

        //Next step is to assign moves to trucks;


        //Make a list of the available trucks to handle the moves;
        HashMultimap<Location, Truck> truckMap = HashMultimap.create();
        for(Truck t: trucks){
            //Check if truck is not yet being used by other cluster;
            if(!t.isUsed()){
                truckMap.put(t.getStartLocation(),t);
            }
        }
        //Assign each move to a truck
        for(Move m: movesList){
            Location from = m.getFrom();


            //First we check if we have any trucks that are passing in either both origin and destination of the move
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
            }

            //Go through the sortedTruckList until a truck is found that is able to handle the move without breaking any
            //constraints.
            boolean truckFound = false;
            while(!sortedTruckList.isEmpty()){
                Truck selected = sortedTruckList.getFirst();
                //Make a deep copy backup to roll back the truck in case it can't handle the move;
                Truck backup = new Truck(selected);
                if(selected.doMove(m)){
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
            if(truckFound) continue;


            //If we reach this part of the code, it means that the move can't be executed by any available trucks.
            //🤔🤔🤔🤔🤔🤔

            System.out.println("Wooooops");

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
