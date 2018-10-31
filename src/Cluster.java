import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Cluster {

    double longitude;
    double latitude;
    List<Location> locations;
    boolean changed = false;
    private static List<Cluster> clusters = new LinkedList<>();

    /**
     * This constructor will create an initial random point in the space defined by the furthest locations.
     * This point will calculate all the nodes that are closest to it, add these to a list, and based on that list it will calculate a new center.
     * @param lonMin
     * @param lonMax
     * @param latMin
     * @param latMax
     */
    public Cluster(double lonMin, double lonMax, double latMin, double latMax){
        /*
        A cluster will start at a random point in the space.
         */
        Random rand = new Random();
        latitude = rand.nextDouble() * (latMax-latMin) + latMin;
        longitude = rand.nextDouble() * (lonMax-lonMin) + lonMin;

        clusters.add(this);
//        System.out.println("lat: " + latitude + ", lon: " + longitude);
    }

    /**
     * This method calculates a new center based on a list of locations.
     * When the calculated location differs from the previous location it indicates that the list with location
     * can still change. So we use a boolean to track this.
     */
    private void calculateNewCenter(){
        double tempLat = 0.0;
        double tempLon = 0.0;
        for (Location location : locations) {
            tempLat += location.getLatitude();
            tempLon += location.getLongitude();
        }
        tempLat /= locations.size();
        tempLon /= locations.size();

        if (tempLat != latitude || tempLon != longitude)
            changed = true;

        latitude = tempLat;
        longitude = tempLon;
    }

    /**
     * This method serves to update the list of locations of each cluster.
     * We will look at each Location and calculate which cluster center is closest.
     */
    private void calculateLocations(List<Location> locations){
        for (Location location : locations) {
            calculateClosestCluster(location).locations.add(location);
        }
//        Reset the changed property as to detect when the system will find a stable location
        changed = false;
    }

    /**
     * Calculate which cluster is closest to a a g
     * @param location
     * @return
     */
    private Cluster calculateClosestCluster(Location location){
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
}
