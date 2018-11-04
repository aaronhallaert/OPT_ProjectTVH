package SolutionEntities;

import Entities.Client;
import Entities.Depot;
import Entities.Location;
import Entities.MachineType;

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

            locationClientMap.put(clusterClient.getLocation(), clusterClient);
        }
        for (Depot clusterDepot : c.getClusterDepots()) {
            allLocations.add(clusterDepot.getLocation());

            locationDepotMap.put(clusterDepot.getLocation(), clusterDepot);
        }

        allLocationsNotMedoid.addAll(allLocations);
        allLocationsNotMedoid.remove(c.getMainDepot().getLocation());


    }


}
