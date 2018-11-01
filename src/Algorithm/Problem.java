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
    public HashMap<Location,Job> jobs = new HashMap<>();
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
                Job job = new Job(location);
                job.addToDropItems(machineType);
                jobs.put(location, job);
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
                Job job = new Job(location);
                job.addToCollectItems(machine);
                jobs.put(location, job);
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

        // DICHTSTE DEPOT VOOR ELKE JOB BEREKENEN
        HashMap<Job, Depot> nearestDepot=new HashMap<>();
        // itereren over alle jobs <Location, Job>
        for (Map.Entry<Location, Job> entry : jobs.entrySet()) {
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
        // itereren over alle nearestDepot
        for (Map.Entry<Job, Depot> entry : nearestDepot.entrySet()) {
            clusters.putIfAbsent(entry.getValue(), new Cluster(entry.getValue()));
            clusters.get(entry.getValue()).getClusterJobs().add(entry.getKey());
        }



        // OVERLOPEN VAN CLUSTERS
        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {

            Cluster huidigeCluster= entry.getValue();
            Depot huidigDepot= entry.getKey();

            for (MachineType machineType : machineTypes) {
                huidigeCluster.getBeschikbaar().put(machineType,0);
                huidigeCluster.getAfTeLeveren().put(machineType, 0);
            }


            // machine types beschikbaar in depot
            for(Map.Entry<MachineType, LinkedList<Machine>> entry1: huidigDepot.getMachines().entrySet()){
                for (int i = 0; i < entry1.getValue().size(); i++) {
                    huidigeCluster.getBeschikbaar().putIfAbsent(entry1.getKey(), 0);
                    huidigeCluster.getBeschikbaar().replace(entry1.getKey(), huidigeCluster.getBeschikbaar().get(entry1.getKey())+1);
                }
            }

            // machine types beschikbaar op locaties en af te leveren op locatie
            for (Job job : huidigeCluster.getClusterJobs()) {
                for (Machine toCollectItem : job.getToCollectItems()) {
                    huidigeCluster.getBeschikbaar().putIfAbsent(toCollectItem.getType(), 0);
                    huidigeCluster.getBeschikbaar().replace(toCollectItem.getType(), huidigeCluster.getBeschikbaar().get(toCollectItem.getType())+1);
                }

                for (MachineType toDropItem : job.getToDropItems()) {
                    huidigeCluster.getAfTeLeveren().putIfAbsent(toDropItem, 0);
                    huidigeCluster.getAfTeLeveren().replace(toDropItem, huidigeCluster.getAfTeLeveren().get(toDropItem)+1);
                }

            }



            huidigeCluster.computeNeeded(this);
            huidigeCluster.printBehoeftesOverbodige();









        }




        return null;

    }





}
