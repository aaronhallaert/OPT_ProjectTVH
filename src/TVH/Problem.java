package TVH;

import TVH.Entities.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Problem {

    public static String info;
    public static int TRUCK_CAPACITY;
    public static int TRUCK_WORKING_TIME;

    public ArrayList<Location> locations = new ArrayList<>();
    public ArrayList<Depot> depots = new ArrayList<>();
    public HashMap<Location, Client> clients = new HashMap<>();
    public HashMap<Machine, Location> machineLocations = new HashMap<>();
    public ArrayList<Truck> trucks = new ArrayList<>();
    public ArrayList<MachineType> machineTypes = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();
    public List<Cluster> clusters = new ArrayList<>();

    public Problem(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile).useLocale(Locale.US);;

        info = sc.nextLine().split(": ")[1];
        TRUCK_CAPACITY= Integer.parseInt(sc.nextLine().split(": ")[1]);
        TRUCK_WORKING_TIME=Integer.parseInt(sc.nextLine().split(": ")[1]);

        sc.nextLine(); //weggooilijn


        // LOCATIONS
        int aantalLocations = Integer.parseInt(sc.nextLine().split(" ")[1]);

        for (int i = 0; i < aantalLocations; i++) {
            int locationId =sc.nextInt();
            double latitude = sc.nextDouble();
            double longitude = sc.nextDouble();
            String name = sc.next();
            locations.add(new Location(locationId, name, latitude,longitude));
        }


        //DEPOTS
        sc.nextLine();
        sc.nextLine();
        int aantalDepots= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            int depotId=sc.nextInt();
            int locationId= sc.nextInt();
            depots.add(new Depot(locations.get(locationId)));
        }

        //TRUCKS
        /*
            Alle trucks inlezen en referenties meegeven naar hun start en stop locatie;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalTrucks= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalTrucks; i++) {
            int truckId=sc.nextInt();
            Location startLocation = locations.get(sc.nextInt());
            Location endLocation= locations.get(sc.nextInt());

            trucks.add(new Truck(truckId, startLocation, endLocation));
        }


        //MACHINE_TYPES
        /*
            Alle verschillende machine types inlezen
         */
        sc.nextLine();
        sc.nextLine();
        int aantalMachineTypes= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachineTypes; i++) {
            int machineTypeId=sc.nextInt();
            int machineTypeVolume=sc.nextInt();
            int serviceTime = sc.nextInt();
            String machineTypeName= sc.next();
            machineTypes.add(new MachineType(machineTypeId, machineTypeName, machineTypeVolume, serviceTime));
        }

        // MACHINES
        /*
            Machines inlezen en een referentie maken van machine naar node en omgekeerd
         */
        sc.nextLine();
        sc.nextLine();
        int aantalMachines= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachines; i++) {
            int machineId=sc.nextInt();
            MachineType machineType=machineTypes.get(sc.nextInt());
            Location location = locations.get(sc.nextInt());
            Machine machine = new Machine(machineId, machineType);

            //Indien een machine in een depot staat moet deze worden toegevoegd aan het depot;
            for(Depot d: depots){
                if(d.getLocation() == location){
                    d.addMachine(machine);
                }
            }
            //toevoegen in algemene lijst
            machines.add(machine);
            machineLocations.put(machine, location);
        }

        //DROPS
        /*
            Alle drops toevoegen aan de juiste Client;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalDrops=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDrops; i++) {
            int dropId=sc.nextInt();
            MachineType machineType=machineTypes.get(sc.nextInt());
            Location location = locations.get(sc.nextInt());
            if(clients.containsKey(location)){
                clients.get(location).addToDropItems(machineType);
            }
            else{
                Client client = new Client(location);
                client.addToDropItems(machineType);
                clients.put(location, client);
            }

        }

        //COLLECTS
        /*
            Alle collects toevoegen aan de juiste Client;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalCollects=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId=sc.nextInt();
            Machine machine=machines.get(sc.nextInt());
            Location location = machineLocations.get(machine);
            if(clients.containsKey(location)){
                clients.get(location).addToCollectItems(machine);
            }
            else{
                Client client = new Client(location);
                client.addToCollectItems(machine);
                clients.put(location, client);
            }
        }


        //TIME_MATRIX inlezen
        sc.nextLine();
        sc.nextLine();
        int timeMatrixSize = Integer.parseInt(sc.nextLine().split(" ")[1]);
        int[][] timeMatrix = new int[timeMatrixSize][timeMatrixSize];

        for (int from = 0; from < timeMatrixSize; from++) {
            for (int to = 0; to < timeMatrixSize; to++) {
                timeMatrix[from][to] = sc.nextInt();
            }
        }

        //DISTANCE_MATRIX inlezen
        sc.nextLine();
        sc.nextLine();
        int distanceMatrixSize = Integer.parseInt(sc.nextLine().split(" ")[1]);
        int [][] distanceMatrix = new int[distanceMatrixSize][distanceMatrixSize];

        for (int from = 0; from < distanceMatrixSize; from++) {
            for (int to = 0; to < distanceMatrixSize; to++) {
                distanceMatrix[from][to] = sc.nextInt();;
            }
        }

        //Edges aanmaken met time en distance info, en koppelen aan clients
        for (int from = 0; from < distanceMatrixSize; from++) {
            for (int to = 0; to < distanceMatrixSize; to++) {
                int time = timeMatrix[from][to];
                int distance = distanceMatrix[from][to];
                //if(!(time == 0 && distance == 0)){
                    Location fromLoc = locations.get(from);
                    Location toLoc = locations.get(to);
                    Edge edge = new Edge(fromLoc, toLoc, time, distance);
                    edges.add(edge);
                    //We voegen ook nog een verwijzing naar de edge toe aan de "from location" (handig zoeken);
                    fromLoc.addEdge(edge);
                //}

            }
        }
        System.out.println("Input read");

    }

    public Solution solve(){
        Solution Init = createInitialSolution();
        /*
        * The algorithm consists of two parts. Creating the initial solution and improving that solution with local search.
        * 1) Initial result:
        *   We make clusters which contain stops. These clusters have a net list of needed items ot provided items.
        *   From this list we search for the nearest depot or the nearest stop from another cluster that contains the needed items.
        *   This will be a fairly fixed result where local search cant get out, so we increase the number of clusters until no improvement is found.
        *
        * 2) Optimisation phase
        *
        * */

        return Init;
    }

    /**
     * This will use clusters to decide initial routes of trucks.
     * @return
     */
    public Solution createInitialSolution(){
        int n_clusters =(int) Math.round((double) clients.size()/5);
        clusters = Cluster.createClusters(n_clusters, clients, depots);


        //Sort the clusters based on remoteness
        clusters.sort((Cluster c1, Cluster c2)->c2.getRemoteness(depots)-c1.getRemoteness(depots));

        //Calculate the machines that are still needed inside the cluster, and expand the cluster until it is self-sufficient;
        for(Cluster cluster : clusters){
            cluster.calculateMachinesNeeded();
            cluster.expand();
            System.out.println(cluster);
            System.out.println();
        }

        for(Cluster cluster: clusters){
            cluster.solve(trucks);
        }

        int totalDistance = 0;
        for(Truck t: trucks){
            int distance = t.getTotalDistance();
            System.out.println("Truck "+t.getTruckId()+":\t"+distance);
            totalDistance += distance;
        }
        System.out.println("Total Distance: "+totalDistance);

        for(Truck t: trucks){
            if(t.getRoute().getFirst().getLocation() != t.getRoute().getLast().getLocation()){
                System.out.println("error");
            }
        }

        return new Solution(trucks);
    }

}
