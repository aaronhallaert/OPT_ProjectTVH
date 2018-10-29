import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Problem {

    public String info;
    public int TRUCK_CAPACITY;
    public int TRUCK_WORKING_TIME;
    public int SERVICE_TIME;

    public ArrayList<Location> locations = new ArrayList<>();
    public ArrayList<Depot> depots = new ArrayList<>();
    public HashMap<Location,Job> jobs = new HashMap<>();
    public ArrayList<Truck> trucks = new ArrayList<>();
    public ArrayList<MachineType> machineTypes = new ArrayList<>();
    public ArrayList<Machine> machines = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();

    public Problem(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile);

        info = sc.nextLine().split(": ")[1];
        TRUCK_CAPACITY= Integer.parseInt(sc.nextLine().split(": ")[1]);
        TRUCK_WORKING_TIME=Integer.parseInt(sc.nextLine().split(": ")[1]);
        SERVICE_TIME=Integer.parseInt(sc.nextLine().split(": ")[1]);

        sc.nextLine(); //weggooilijn


        // LOCATIONS
        int aantalLocations = Integer.parseInt(sc.nextLine().split(" ")[1]);

        for (int i = 0; i < aantalLocations; i++) {
            int locationId =sc.nextInt();
            double latitude = sc.nextDouble();
            double longitude = sc.nextDouble();
            locations.add(new Location(locationId,latitude,longitude));
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
            String machineTypeName= sc.next();
            machineTypes.add(new MachineType(machineTypeId, machineTypeName, machineTypeVolume));
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
                jobs.put(location, new Job(location));
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
            Location location = locations.get(sc.nextInt());
            if(jobs.containsKey(location)){
                jobs.get(location).addToCollectItems(machine);
            }
            else{
                Job job = new Job(location);
                job.addToCollectItems(machine);
                jobs.put(location, new Job(location));
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

    public Solution createInitialSolution(){
        /*
        Eerst moeten we zien te vinden welke te leveren machines niet in de depots zitten.
        Deze machines zullen sowieso moeten opgehaald worden.
        */

        //We maken een hashmap aan met alle mogelijke modellen
        HashMap<MachineType, Integer> machinesInDepot = new HashMap<>();
        for(MachineType mt: machineTypes){
            machinesInDepot.put(mt, 0);
        }

        //We overlopen de depots en tellen de aantallen per type op;
        for(Depot d: depots){
            for(MachineType mt : d.getMachines().keySet()){
                machinesInDepot.put(mt , machinesInDepot.get(mt)+d.getNumberOfMachinesOfType(mt));
            };
        }

        //Kijken welke

        return null;

    }

}
