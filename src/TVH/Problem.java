package TVH;

import TVH.Entities.Job.*;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.*;
import TVH.Entities.Truck.*;
import com.google.common.collect.HashMultimap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Problem {

    private static Problem instance = null;
    public String info;
    public int TRUCK_CAPACITY;
    public int TRUCK_WORKING_TIME;
    public ArrayList<Location> locations = new ArrayList<>();                   //statisch object
    public HashMap<Location, Node> nodesMap = new HashMap<>();                  //niet-statisch object
    public HashMap<Location, Client> clientMap = new HashMap<>();               //niet-statisch object
    public HashMap<Location, Depot> depotMap = new HashMap<>();                 //niet-statisch object
    public HashMap<Machine, Location> machineLocations = new HashMap<>();       //statisch object
    public ArrayList<Truck> trucks = new ArrayList<>();                         //niet statisch object
    public ArrayList<MachineType> machineTypes = new ArrayList<>();             //statisch object
    public ArrayList<Machine> machines = new ArrayList<>();                     //statisch object
    public ArrayList<Edge> edges = new ArrayList<>();                           //statisch object
    public List<Job> jobs = new ArrayList<>();                                  //statisch object
    public HashMultimap<MachineType, Job> jobTypeMap = HashMultimap.create();   //statisch object
    public HashMap<Job, Truck> jobTruckMap = new HashMap<>();                   //niet-statisch object

    public static Problem getInstance() {
        return instance;
    }

    public static Problem newInstance(File inputFile) throws FileNotFoundException {
        instance = new Problem(inputFile);
        return instance;
    }

    private Problem(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile).useLocale(Locale.US);
        ;

        info = sc.nextLine().split(": ")[1];
        TRUCK_CAPACITY = Integer.parseInt(sc.nextLine().split(": ")[1]);
        TRUCK_WORKING_TIME = Integer.parseInt(sc.nextLine().split(": ")[1]);

        sc.nextLine(); //weggooilijn


        // LOCATIONS
        int aantalLocations = Integer.parseInt(sc.nextLine().split(" ")[1]);

        for (int i = 0; i < aantalLocations; i++) {
            int locationId = sc.nextInt();
            double latitude = sc.nextDouble();
            double longitude = sc.nextDouble();
            String name = sc.next();
            locations.add(new Location(locationId, name, latitude, longitude));
        }


        //DEPOTS
        sc.nextLine();
        sc.nextLine();
        int aantalDepots = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            int depotId = sc.nextInt();
            int locationId = sc.nextInt();
            Depot depot = new Depot(locations.get(locationId));
            depotMap.put(locations.get(locationId), depot);
            nodesMap.put(locations.get(locationId), depot);
        }

        //TRUCKS
        /*
            Alle trucks inlezen en referenties meegeven naar hun run en stop locatie;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalTrucks = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalTrucks; i++) {
            int truckId = sc.nextInt();
            Location startLocation = locations.get(sc.nextInt());
            Location endLocation = locations.get(sc.nextInt());

            trucks.add(new Truck(truckId, startLocation, endLocation));
        }


        //MACHINE_TYPES
        /*
            Alle verschillende machine types inlezen
         */
        sc.nextLine();
        sc.nextLine();
        int aantalMachineTypes = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachineTypes; i++) {
            int machineTypeId = sc.nextInt();
            int machineTypeVolume = sc.nextInt();
            int serviceTime = sc.nextInt();
            String machineTypeName = sc.next();
            machineTypes.add(new MachineType(machineTypeId, machineTypeName, machineTypeVolume, serviceTime));
        }

        // MACHINES
        /*
            Machines inlezen en een referentie maken van machine naar node en omgekeerd
         */
        sc.nextLine();
        sc.nextLine();
        int aantalMachines = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachines; i++) {
            int machineId = sc.nextInt();
            MachineType machineType = machineTypes.get(sc.nextInt());
            Location location = locations.get(sc.nextInt());
            Machine machine = new Machine(machineId, machineType);

            //Indien een machine in een depot staat moet deze worden toegevoegd aan het depot;
            for (Depot d : depotMap.values()) {
                if (d.getLocation() == location) {
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
        int aantalDrops = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDrops; i++) {
            int dropId = sc.nextInt();
            MachineType machineType = machineTypes.get(sc.nextInt());
            Location location = locations.get(sc.nextInt());
            if (clientMap.containsKey(location)) {
                clientMap.get(location).addToDrop(machineType);
            } else {
                Client client = new Client(location);
                client.addToDrop(machineType);
                clientMap.put(location, client);
                nodesMap.put(location, client);

            }

        }

        //COLLECTS
        /*
            Alle collects toevoegen aan de juiste Client;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalCollects = Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId = sc.nextInt();
            Machine machine = machines.get(sc.nextInt());
            Location location = machineLocations.get(machine);
            if (clientMap.containsKey(location)) {
                clientMap.get(location).addToCollect(machine);
            } else {
                Client client = new Client(location);
                client.addToCollect(machine);
                clientMap.put(location, client);
                nodesMap.put(location, client);
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
        int[][] distanceMatrix = new int[distanceMatrixSize][distanceMatrixSize];

        for (int from = 0; from < distanceMatrixSize; from++) {
            for (int to = 0; to < distanceMatrixSize; to++) {
                distanceMatrix[from][to] = sc.nextInt();
                ;
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

    public Solution solve() {
        createJobs();
        createInitialSolution();
        Solution init = new Solution();
//        for(Truck t: trucks){
//            t.optimizeTruck();
//        }
        System.out.println(init);
        Solution best = simulatedAnnealing(60000, 200);
        System.out.println(best);
        System.out.println("DEBUG:");
        System.out.println("Init: "+init.getTotalDistance());
        System.out.println("Best: " + best.getTotalDistance());
        init.recalculateDistance();
        System.out.println("Init(re): "+init.getTotalDistance());
        best.recalculateDistance();
        System.out.println("Best(re): "+best.getTotalDistance());

        Solution test = new Solution();
        System.out.println("Current: "+test.getTotalDistance());
        return best;
    }

    /**
     * This will use clusters to decide initial routes of trucks.
     *
     * @return
     */

    public void createJobs() {
        for (Client client : clientMap.values()) {
            Location loc = client.getLocation();
            for (MachineType mt : client.getToDrop()) {
                //Search for each machineType needed what the closest location in this cluster is that has this machineType.
                //There are 2 possibilities: Depot contains machine of type, Client contains a machine of this type that needs to be collected
                List<Location> from = new ArrayList<>();
                for (Edge e : loc.getSortedEdgeList()) {
                    Node node = nodesMap.get(e.getTo());
                    if (node.hasMachineAvailableOfType(mt)) {
                        from.add(node.getLocation());
                    }
                }
                jobs.add(new DropJob(loc, from, mt));

            }
            for (Machine m : client.getToCollect()) {
                List<Location> to = new ArrayList<>();
                for (Edge e : loc.getSortedEdgeList()) {
                    Node node = nodesMap.get(e.getTo());
                    if (node.canPutMachineType(m.getType())) {
                        to.add(node.getLocation());
                    }
                }
                jobs.add(new CollectJob(loc, to, m));
            }
        }
        Collections.sort(jobs, new JobRemotenessComparator());
        for(Job j: jobs){
            jobTypeMap.put(j.getMachineType(), j);
        }
    }


    public void createInitialSolution() {


        //Next step is to assign jobs to trucks;


        //Assign each move to a truck
        List<Job> HandledJobs = new ArrayList<>();
        int i = 0;
        for (Job j : jobs) {
            System.out.println(i);
            i++;

            if (j.notDone()) {
                Truck optimalTruck = null;
                Route optimalRoute = null;
                Move optimalMove = null;
                int minAddedCost = Integer.MAX_VALUE;
                for (Truck t : trucks) {
                    int cost = t.getRoute().getCost();
                    LinkedList<Stop> previousOrder = new LinkedList<>(t.getRoute().getStops());
                    if (t.addJob(j)) {
                        int addedCost = t.getRoute().getCost() - cost;
                        //We removen de job opnieuw en zetten de volgorde terug zoals voordien.
                        //Dit om te voorkomen dat hij geen oplossing meer vindt een keer we hem echt willen toevoegen
                        if (addedCost < minAddedCost) {
                            minAddedCost = addedCost;
                            optimalTruck = t;
                            optimalRoute = new Route(t.getRoute());
                            optimalMove = t.getJobMoveMap().get(j);
                        }
                        t.removeJob(j, false);
                        t.getRoute().setStops(previousOrder);
                    }
                }
                if (optimalTruck != null) {
                    //Route die we daarnet gevonden hebben terug toevoegen en de job en move terug toevoegen aan de hashmap;
                    optimalTruck.addJob(j, optimalMove, optimalRoute);
                    HandledJobs.add(j);
                    jobTruckMap.put(j, optimalTruck);
                }
                else{
                    System.out.println("ERROR: No truck found");
                }
            }
            else{
                HandledJobs.add(j);
            }
        }
    }

    public Solution simulatedAnnealing(int duration, double temperature){
        //TODO: werkt voorlopig niet omdat de hashmap van locations in route blijkbaar hier niets delete...
        long endTime = System.currentTimeMillis()+duration;
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        double currentTemp = temperature;
        int counter = 0;
        Random r = new Random();
        while(System.currentTimeMillis() < endTime) {
            for (MachineType mt : machineTypes) {
                ArrayList<Job> jobsOfType = new ArrayList<>(jobTypeMap.get(mt));
                Collections.shuffle(jobsOfType);
                for (Job j : jobsOfType) {
                    Truck t = jobTruckMap.get(j);
                    if(t != null){ //Het kan zijn dat bepaalde jobs niet worden uitgevoerd door een truck omdat ze al gecompleet worden door een andere job
                        t.removeJob(j, true);
                        jobTruckMap.remove(j);
                    }
                }
                for (Job j : jobsOfType) {
                    if(j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                        Truck optimalTruck = null;
                        Route optimalRoute = null;
                        Move optimalMove = null;
                        int minAddedCost = Integer.MAX_VALUE;
                        for (Truck t : trucks) {
                            int cost = t.getRoute().getCost();
                            LinkedList<Stop> previousOrder = new LinkedList<>(t.getRoute().getStops());
                            if (t.addJob(j)) {
                                int addedCost = t.getRoute().getCost() - cost;
                                //We removen de job opnieuw en zetten de volgorde terug zoals voordien.
                                //Dit om te voorkomen dat hij geen oplossing meer vindt een keer we hem echt willen toevoegen
                                if (addedCost < minAddedCost) {
                                    minAddedCost = addedCost;
                                    optimalTruck = t;
                                    optimalRoute = new Route(t.getRoute());
                                    optimalMove = t.getJobMoveMap().get(j);
                                }
                                t.removeJob(j, false);
                                t.getRoute().setStops(previousOrder);
                            }
                        }
                        if (optimalTruck != null) {
                            //Route die we daarnet gevonden hebben terug toevoegen en de job en move terug toevoegen aan de hashmap;
                            optimalTruck.addJob(j, optimalMove, optimalRoute);
                            jobTruckMap.put(j, optimalTruck);
                        } else {
                            //We vinden geen truck meer die deze job wil uitvoeren, we zullen moeten teruggaan naar de beste oplossing tot nu toe
                            localOptimum.loadSolution();
                            break;
                        }
                    }
                }
                Solution candidate = new Solution();
                if(candidate.getTotalDistance() < localOptimum.getTotalDistance()){
                    localOptimum = new Solution();
                    if(localOptimum.getTotalDistance() < best.getTotalDistance()){
                        best = localOptimum;
                    }
                    System.out.println(localOptimum.getTotalDistance());
                    break;
                }
                else{
                    double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance())/currentTemp);
                    if(localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                    double random = r.nextDouble();
                    if(random <= acceptRate){
                        counter++;
                        System.out.println("worse candidate accepted with "+acceptRate+" ("+currentTemp+")");
                        localOptimum = candidate;
                        System.out.println(localOptimum.getTotalDistance() + "\t"+acceptRate+"\t"+currentTemp);

                    }
                    localOptimum.loadSolution();
                }
                if(counter < 10){
                    counter=0;
                    currentTemp = 0.99 * currentTemp;
                }

            }
        }
        best.loadSolution();
        return best;
    }


    /*public Solution localSearch(int duration, Solution init){
        long endTime = System.currentTimeMillis() + duration;
        Solution currentBest = init;
        System.out.println(currentBest.getTotalDistance());
        while(System.currentTimeMillis() < endTime){
            for(Truck t: trucks){
                ArrayList<Job> jobs = new ArrayList<>(t.getJobMoveMap().keySet());
                if(!jobs.isEmpty()) {
                    int index = (int) (Math.random() * jobs.size());
                    t.removeJob(jobs.get(index), false);
                    t.addJob(jobs.get(index));
                }
            }
            Solution candidate = new Solution(trucks);
            if(candidate.getTotalDistance() < currentBest.getTotalDistance()){
                System.out.println(currentBest.getTotalDistance());
                currentBest = candidate;
            }
        }
        return currentBest;

    }*/

}


