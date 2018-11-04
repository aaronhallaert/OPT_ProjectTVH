package Algorithm;

import Entities.*;
import SolutionEntities.Cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Problem {

    public String info;
    public int TRUCK_CAPACITY;
    public int TRUCK_WORKING_TIME;

    public ArrayList<Location> locations = new ArrayList<>();
    public HashMap<Location, Depot> depots = new HashMap<>();
    public HashMap<Location, Client> jobs = new HashMap<>();
    public HashMap<Machine, Location> machineLocations = new HashMap<>();
    public ArrayList<Truck> trucks = new ArrayList<>();
    public ArrayList<MachineType> machineTypes = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();



    //computed
    public HashMap<Depot, Cluster> clusters;

    /**
     * inlezen van het probleem uit file
     * @param inputFile txt file
     * @throws FileNotFoundException
     */
    public Problem(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile);

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
            depots.put(locations.get(locationId),new Depot(locations.get(locationId)));
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
            for (Map.Entry<Location, Depot> entry : depots.entrySet()) {
                if(entry.getKey() == location){
                    entry.getValue().addMachine(machine);
                }
            }
            //toevoegen in algemene lijst
            machines.add(machine);
            machineLocations.put(machine, location);
        }

        //DROPS
        /*
            Alle drops toevoegen aan de juiste job;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalDrops=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDrops; i++) {
            int dropId=sc.nextInt();
            MachineType machineType=machineTypes.get(sc.nextInt());
            Location location = locations.get(sc.nextInt());
            if(jobs.containsKey(location)){
                jobs.get(location).addToDropItems(machineType);
            }
            else{
                Client client = new Client(location);
                client.addToDropItems(machineType);
                jobs.put(location, client);
            }

        }

        //COLLECTS
        /*
            Alle collects toevoegen aan de juiste job;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalCollects=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId=sc.nextInt();
            Machine machine=machines.get(sc.nextInt());
            Location location = machineLocations.get(machine);
            if(jobs.containsKey(location)){
                jobs.get(location).addToCollectItems(machine);
            }
            else{
                Client client = new Client(location);
                client.addToCollectItems(machine);
                jobs.put(location, client);
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

        //Edges aanmaken met time en distance info, en koppelen aan jobs
        for (int from = 0; from < distanceMatrixSize; from++) {
            for (int to = 0; to < distanceMatrixSize; to++) {
                int time = timeMatrix[from][to];
                int distance = distanceMatrix[from][to];
                if(!(time == 0 && distance == 0)){
                    Location fromLoc = locations.get(from);
                    Location toLoc = locations.get(to);
                    Edge edge = new Edge(fromLoc, toLoc, time, distance);
                    edges.add(edge);
                    //We voegen ook nog een verwijzing naar de edge toe aan de "from location" (handig zoeken);
                    fromLoc.addEdge(edge);
                }

            }
        }
        System.out.println("Input read");

    }

    /**
     * zoekt een initiële oplossing
     * @return solution: lijst van trucks (die lijst van stops bevatten) en totaal aantal km
     */
    public Solution createInitialSolution(){


        HashMap<Depot, Cluster> clusters= setupClusters();

        Iterator it = clusters.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Depot, Cluster> item = (Map.Entry<Depot, Cluster>) it.next();
            if(item.getValue().getClusterClients().size()==0) {
                // voeg mainDepot toe aan andere dichtsbijzijnde cluster
                clusters.get(depots.get(item.getValue().getSortedEdgesToOtherDepots().get(0).getTo())).getClusterDepots().add(item.getKey());
                // verwijder cluster (aangezien deze is samengesmolten met andere)
                it.remove();
            }
        }


        Iterator it2 = clusters.entrySet().iterator();
        while (it2.hasNext())
        {
            // voeg alle clusters toe aan de cluster
            Map.Entry<Depot, Cluster> item = (Map.Entry<Depot, Cluster>) it2.next();

        }



        return null;

    }



    /**
     *  deze methode maakt clusters op basis van dichtste depot
     *  elke cluster bevat een depot
     *  daarnaast wordt bij elke cluster berekend welke machinetypes tekort zijn en welke te veel zijn
     *
     *  @return HashMap met als key depot, en als value cluster bestaande uit lijst van jobs
     */
    private HashMap<Depot, Cluster> setupClusters(){

        // DICHTSTE DEPOT VOOR ELKE JOB BEREKENEN
        HashMap<Client, Depot> nearestDepot=new HashMap<>();
        // itereren over alle jobs <Location, Client>
        for (Map.Entry<Location, Client> entry : jobs.entrySet()) {
            // voor elke job gesorteerde lijst van edges eruit halen en eerste depot = dichtste depot voor die job
            for (Edge edge : entry.getValue().getLocation().getSortedEdgeList()) {
                // edgeTo is een depot
                if(depots.containsKey(edge.getTo())){
                    nearestDepot.put(entry.getValue(), depots.get(edge.getTo()));
                    break;
                }
            }
        }


        // SET VAN CLUSTERS MAKEN PER DEPOT
        clusters= new HashMap<>();
        int clusterCount=1;


        // itereren over alle nearestDepot
        for (Map.Entry<Client, Depot> entry : nearestDepot.entrySet()) {

            if(clusters.get(entry.getValue())==null){
                clusters.put(entry.getValue(), new Cluster(entry.getValue(), clusterCount));
                System.out.println("clustercount " + clusterCount);
                clusterCount++;
            }
            clusters.get(entry.getValue()).getClusterClients().add(entry.getKey());
        }



        // OVERLOPEN VAN CLUSTERS
        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {

            Cluster huidigeCluster= entry.getValue();
            Depot huidigDepot= entry.getKey();




            huidigeCluster.setupBeschikbaarAfTeLeveren(this);
            huidigeCluster.computeNeeded(this);
            huidigeCluster.printBehoeftesOverbodige();
            huidigeCluster.fillNeeded(this, clusters);

        }

        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {
           entry.getValue().addEdgesToOtherClusters(clusters);
        }


        while(!checkClusterReq(clusters)) {
            Main.printGraph(this);
            for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {
                entry.getValue().reset();
                entry.getValue().setupBeschikbaarAfTeLeveren(this);
                entry.getValue().computeNeeded(this);
                entry.getValue().printBehoeftesOverbodige();
                entry.getValue().fillNeeded(this, clusters);
            }
        }



        return clusters;
    }


    private boolean checkClusterReq(HashMap<Depot, Cluster> clusters){
        int check= 0;
        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {
            if(entry.getValue().getNodigeMachineTypes().size()==0){
                check++;
            }
        }

        if(clusters.entrySet().size()==check){
            return true;
        }
        else{
            return false;
        }
    }
}


