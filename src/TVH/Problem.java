package TVH;

import TVH.Entities.*;
import TVH.Entities.Job.CollectJob;
import TVH.Entities.Job.JobRemotenessComparator;
import TVH.Entities.Job.DropJob;
import TVH.Entities.Job.Job;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Problem {

    public static String info;
    public static int TRUCK_CAPACITY;
    public static int TRUCK_WORKING_TIME;

    public ArrayList<Location> locations = new ArrayList<>();
    public ArrayList<Depot> depots = new ArrayList<>();
    public HashMap<Location, Client> clientMap = new HashMap<>();
    public HashMap<Location, Depot> depotsMap = new HashMap<>();
    public HashMap<Machine, Location> machineLocations = new HashMap<>();
    public ArrayList<Truck> trucks = new ArrayList<>();
    public ArrayList<MachineType> machineTypes = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();
    public List<Cluster> clusters = new ArrayList<>();
    public List<Job> jobs = new ArrayList<>();

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
            Depot depot = new Depot(locations.get(locationId));
            depots.add(depot);
            depotsMap.put(locations.get(locationId), depot);
        }

        //TRUCKS
        /*
            Alle trucks inlezen en referenties meegeven naar hun run en stop locatie;
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
            if(clientMap.containsKey(location)){
                clientMap.get(location).addToDropItems(machineType);
            }
            else{
                Client client = new Client(location);
                client.addToDropItems(machineType);
                clientMap.put(location, client);
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
            if(clientMap.containsKey(location)){
                clientMap.get(location).addToCollectItems(machine);
            }
            else{
                Client client = new Client(location);
                client.addToCollectItems(machine);
                clientMap.put(location, client);
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

        //Edges aanmaken met time en distance info, en koppelen aan clientMap
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
        //System.out.println("Input read");

    }

    public Solution solve(int n_clusters){
        Solution Init = createInitialSolutionDeprecated(n_clusters);
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
    public Solution createInitialSolutionDeprecated(int n_clusters){
        //n_clusters =(int) Math.round((double) clientMap.size()/5);
        clusters = Cluster.createClusters(n_clusters, clientMap, depots);


        //Sort the clusters based on remoteness
        clusters.sort((Cluster c1, Cluster c2)->c2.getRemoteness(depots)-c1.getRemoteness(depots));

        //Calculate the machines that are still needed inside the cluster, and expand the cluster until it is self-sufficient;
        for(Cluster cluster : clusters){
            cluster.calculateMachinesNeeded();
            cluster.expand();
            //System.out.println(cluster);
            //System.out.println();
        }

        for(Cluster cluster: clusters){
            cluster.solve(trucks);
        }

        int totalDistance = 0;
        int totalTime = 0;
        for(Truck t: trucks){
            int time = t.getTotalTime();
            int distance = t.getTotalDistance();
            System.out.println("Truck "+t.getTruckId()+":\t"+time+"min\t"+distance+"km");
            totalDistance += distance;
            totalTime += time;
        }
        System.out.println("Total: \t\t"+totalTime+"min\t"+totalDistance+"km");

        for(Truck t: trucks){
            if(t.getRoute().getFirst().getLocation() != t.getRoute().getLast().getLocation()){
                //System.out.println("error");
            }
        }

        return new Solution(trucks);
    }

    public Solution createInitialSolution(){
        for(Client client: clientMap.values()){
            Location loc = client.getLocation();
            for(MachineType mt: client.getToDropItems()){
                //Search for each machineType needed what the closest location in this cluster is that has this machineType.
                //There are 2 possibilities: Depot contains machine of type, Client contains a machine of this type that needs to be collected
                List<Location> from = new ArrayList<>();
                for(Edge e: loc.getSortedEdgeList()){
                    //In case the location is a Client
                    if (clientMap.containsKey(e.getTo())) {
                        Client c = clientMap.get(e.getTo());
                        if (c.collectItemsContains(mt)) {
                            Machine m = c.getMachineToCollect(mt);
                            //Add the move to the list and delete machine from items that need to be collected.
                            from.add(c.getLocation());
                        }
                    }
                    //In case the location is a depot
                    if (depotsMap.containsKey(e.getTo())) {
                        Depot d = depotsMap.get(e.getTo());
                        if (d.hasMachine(mt)) {
                            Machine m = d.getMachineFromDepot(mt);
                            //Add the move to the list and delete machine from the depot.
                            from.add(d.getLocation());
                            //A location is found, break the for loop to go the next machine;
                        }
                    }
                }
                jobs.add(new DropJob(loc, from, mt);

            }
            for(Machine m: client.getToCollectItems()){
                List<Location> to = new ArrayList<>();
                for(Depot d: depots){
                    to.add(d.getLocation());
                }
                jobs.add(new CollectJob(loc, to, m));
            }
        }

        //Next step is to assign jobs to trucks;

        Collections.sort(jobs, new JobRemotenessComparator());

        //Assign each move to a truck
        for(Job j: jobs){
            LinkedList<Truck> sortedTruckList = new LinkedList<>();
            sortedTruckList.addAll(trucks);
            //Sort truck list based on it's route proximity to the location of the Job
            sortedTruckList.sort(Comparator.comparing(t->t.getDistanceToLocation(j.getJobLocation())));

            //Go through the sortedTruckList until a truck is found that is able to handle the move without breaking any
            //constraints.
            boolean truckFound = false;
            while(!sortedTruckList.isEmpty()){
                Truck selected = sortedTruckList.getFirst();
                //Make a deep copy backup to roll back the truck in case it can't handle the move;
                Truck backup = new Truck(selected);
                if(selected.doJob(j)){
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
                System.out.println(j);
            }


                //If we reach this part of the code, it means that the move can't be executed by any available trucks.
                //🤔🤔🤔🤔🤔🤔
            }




        }
        //System.out.println("done");

    }

}
