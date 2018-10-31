import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Cluster {

    private static double latMin;
    private static double latMax;
    private static double lonMin;
    private static double lonMax;
    private static List<Cluster> clusters = new LinkedList<>();
    private static boolean changed = false;

    double longitude;
    double latitude;
    List<Location> locations;



    /**
     * This constructor will create an initial random point in the space defined by the furthest locations.
     * This point will calculate all the nodes that are closest to it, add these to a list, and based on that list it will calculate a new center.
     */
    private Cluster(){
        /*
        A cluster will start at a random point in the space.
         */
        Random rand = new Random();
        latitude = rand.nextDouble() * (latMax-latMin) + latMin;
        longitude = rand.nextDouble() * (lonMax-lonMin) + lonMin;

        clusters.add(this);
    }

    public static List<Cluster> createClusters(int nrOfClusters, double lonMin, double lonMax, double latMin, double latMax, List<Location> allLocations){
        Cluster.lonMin = lonMin;
        Cluster.lonMax = lonMax;
        Cluster.latMin = latMin;
        Cluster.latMax = latMax;
        for (int i = 0; i < nrOfClusters; i++) {
            clusters.add(new Cluster());
        }
//        Form the clusters;
        do {
            calculateLocations(allLocations);
            calculateNewCenters();
        } while (changed);

        return clusters;
    }

    /**
     * This method calculates a new center based on a list of locations.
     * When the calculated location differs from the previous location it indicates that the list with location
     * can still change. So we use a boolean to track this.
     */
    private static void calculateNewCenters(){
        double tempLat, tempLon;

        for (Cluster cluster : clusters) {
            tempLat = 0.0;
            tempLon = 0.0;
            List<Location> locations = cluster.locations;

            for (Location location : locations) {
                tempLat += location.getLatitude();
                tempLon += location.getLongitude();
            }
            tempLat /= locations.size();
            tempLon /= locations.size();

            if (tempLat != cluster.latitude || tempLon != cluster.longitude)
                changed = true;

            cluster.latitude = tempLat;
            cluster.longitude = tempLon;

            System.out.println(cluster);
        }
    }

    /**
     * This method serves to update the list of locations of each cluster.
     * We will look at each Location and calculate which cluster center is closest.
     */
    private static void calculateLocations(List<Location> locations){
        for (Location location : locations) {
            getClosestCluster(location).locations.add(location);
        }
//        Reset the changed property as to detect when the system will find a stable location
        changed = false;
    }

    /**
     * Calculate which cluster is closest to a given location.
     * @param location
     * @return
     */
    private static Cluster getClosestCluster(Location location){
        double distance = Double.POSITIVE_INFINITY;
        double tempLat, tempLon, tempDistance = 0.0;
        Cluster closest = clusters.get(0);

        for (Cluster cluster : clusters) {
            tempLat = Math.abs(location.getLatitude() - cluster.latitude);
            tempLon = Math.abs(location.getLongitude() - cluster.longitude);
            tempDistance = Math.hypot(tempLat, tempLon);
            if (tempDistance < distance){
                distance = tempDistance;
                closest = cluster;
            }
        }
        return closest;
    }

    public static List<Cluster> getClusters() {
        return clusters;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", locations=" + locations +
                '}';
    }
}
