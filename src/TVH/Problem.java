package TVH;

import TVH.Entities.Job.*;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.*;
import TVH.Entities.Truck.*;
import TVH.GUI.Listener;
import com.google.common.collect.HashMultimap;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
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
    public HashMap<Location, Job> locationJobMap = new HashMap<>();
    public double currentTemp = 0;
    public int minJobsNotAdded = 0;
    Random r = new Random();
    //public boolean feasibleSolution = false;


    public static Problem getInstance() {
        return instance;
    }

    public static Problem newInstance(File inputFile) throws FileNotFoundException {
        instance = new Problem(inputFile);
        instance.createJobs();
        return instance;
    }

    /**
     * Leest in en creëert een nieuw probleem
     *
     * @param inputFile
     * @throws FileNotFoundException
     */
    private Problem(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile).useLocale(Locale.US);

        info = sc.nextLine().split(": ")[1];
        TRUCK_CAPACITY = Integer.parseInt(sc.nextLine().split(": ")[1]);
        TRUCK_WORKING_TIME = Integer.parseInt(sc.nextLine().split(": ")[1]);
        Route.TRUCK_CAPACITY = TRUCK_CAPACITY;
        Route.TRUCK_WORKING_TIME  = TRUCK_WORKING_TIME;

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
                Location fromLoc = locations.get(from);
                Location toLoc = locations.get(to);
                Edge edge = new Edge(fromLoc, toLoc, time, distance);
                edges.add(edge);
                //We voegen ook nog een verwijzing naar de edge toe aan de "from location" (handig zoeken);
                fromLoc.addEdge(edge);

            }
        }
        Route.distanceMatrix = distanceMatrix;
        Route.timeMatrix = timeMatrix;
        //System.out.println("Input read");

    }

    /**
     * Los het probleem op
     *
     * @return
     */
    public Solution solve(Config config) {
        long endTime = System.currentTimeMillis() + config.getTime() * 1000;

        //Variabelen voor kost functie laden uit de config
        Route.DISTANCE_FACTOR = config.getDistancefactor();
        Route.FILL_RATE_VIOLATIONS_FACTOR = config.getFrviolationsfactor();
        Route.ORDER_FACTOR = config.getOrderfactor();
        Route.TIME_FACTOR = config.getTimefactor();

        //Initiële oplossing maken
        createInitialSolution();
        Solution initial = new Solution();

        //Optimaliseren
        Solution best = null;
        System.out.println(config);
        best = simulatedAnnealing(endTime, config.getTemperature(), config.getJobs(), config.getMachinetypes(), config.getTrucks());

        //System.out.println(best);
        System.out.println("DEBUG:");
        System.out.println("Init: " + initial.getTotalDistance());
        System.out.println("Best: " + best.getTotalDistance());

        return best;
    }

    /**
     * Deze methode maakt alle Jobs aan die moeten uitgevoerd worden op basis van de drop en collect requests van Clients
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
                    if(node != null && node.hasMachineAvailableOfType(mt)){
                        from.add(node.getLocation());
                    }
                }
                jobs.add(new DropJob(loc, from, mt));

            }
            for (Machine m : client.getToCollect()) {
                List<Location> to = new ArrayList<>();
                for (Edge e : loc.getSortedEdgeList()) {
                    Node node = nodesMap.get(e.getTo());
                    if (node != null && node.canPutMachineType(m.getType())) {
                        to.add(node.getLocation());
                    }
                }
                jobs.add(new CollectJob(loc, to, m));
            }
        }
        for (Job j : jobs) {
            jobTypeMap.put(j.getMachineType(), j);
            locationJobMap.put(j.getFixedLocation(), j);
        }
    }

    /**
     * Maak een initiële feasible oplossing die later kan geoptimaliseerd worden
     */
    public void createInitialSolution() {

        //Sorteer de jobs op een bepaalde manier;
        Collections.sort(jobs, new JobRemotenessComparator());


        //Wijs elke Job toe aan de Truck die hem het best kan uitvoeren met het minst extra afstand
        int i = 0;
        for (Job j : jobs) {
            System.out.println(i);
            i++;

            if (j.notDone()) {
                if (!assignJobToBestTruck(j)){
                    minJobsNotAdded++;
                }

            }
        }
    }

    public Solution simulatedAnnealing(long endTime, double temperature, int avgJobs, int nMachineTypesToRemove, int nTrucksToRemove) {
        long duration = endTime - System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        //LinkedList<Integer> tabu = new LinkedList<>();

        currentTemp = temperature;
        Listener.getInstance().updateTemperature(currentTemp);
        int counter = 0;
        int timesRun = 0;
        Queue<Mode> modeQueue = Mode.createQueue();
        Mode mode = modeQueue.poll();

        while (System.currentTimeMillis() < endTime) {

            int nJobsToRemove = r.nextInt(avgJobs*2);

            List<Job> selectedJobs = new ArrayList<>();

            switch (mode) {
                case MTYPE:
                    //We selecteren nMachineTypes om Jobs van te verwijderen

                    Set<MachineType> randomMachineTypes = new HashSet<>();
                    while (randomMachineTypes.size() < nMachineTypesToRemove) {
                        MachineType mt = machineTypes.get(r.nextInt(machineTypes.size()));
                        if (jobTypeMap.get(mt).size() > 1 && randomMachineTypes.add(mt)) {
                            selectedJobs.addAll(jobTypeMap.get(mt));
                        }
                    }
                    //Verwijder Jobs van deze lijst totdat de nJobsToRemove bereikt is
                    while (selectedJobs.size() > nJobsToRemove) {
                        selectedJobs.remove(r.nextInt(selectedJobs.size()));
                    }
                    break;
                case TRUCK:
                    Set<Truck> selectedTrucks = new HashSet<>();
                    while (selectedTrucks.size() < nTrucksToRemove) {
                        Truck t = trucks.get(r.nextInt(trucks.size()));
                        if (!t.isIdle()) {
                            selectedTrucks.add(t);
                        }
                    }
                    for (Truck t : selectedTrucks) {
                        selectedJobs.addAll(removePartOfRoute(t));
                    }
                    break;
                case NEARBY:
                    Job randomJob = jobs.get(r.nextInt(jobs.size()));
                    for (Edge e : randomJob.getFixedLocation().getSortedEdgeList()) {
                        if (locationJobMap.containsKey(e.getTo())) {
                            selectedJobs.add(locationJobMap.get(e.getTo()));
                            if (selectedJobs.size() == nJobsToRemove) {
                                break;
                            }
                        }
                    }
            }

            //Delete them
            for (Job j : selectedJobs) {
                Truck t = jobTruckMap.get(j);
                if (t != null) { //Het kan zijn dat bepaalde jobs niet worden uitgevoerd door een truck omdat ze al gecompleet worden door een andere job
                    t.removeJob(j);
                    jobTruckMap.remove(j);
                }
            }
            //Check if any other jobs are now uncompleted
            for (Job j : jobs) {
                if (j.notDone() && !selectedJobs.contains(j)) {
                    selectedJobs.add(j);
                }
            }

            Collections.shuffle(selectedJobs);

            int nJobsNotAdded = 0;
            switch (mode) {
                case MTYPE:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                nJobsNotAdded++;
                                if(minJobsNotAdded == 0) break;
                            }
                        }
                    }
                    break;
                case TRUCK:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                            if (!assignJobToBestTruck(j)) {
                                nJobsNotAdded++;
                                if(minJobsNotAdded == 0) break;
                            }
                        }
                    }
                case NEARBY:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                nJobsNotAdded++;
                                if(minJobsNotAdded == 0) break;
                            }
                        }
                    }
            }
            if (nJobsNotAdded <= minJobsNotAdded) {
                Solution candidate = new Solution();
                //candidate.writeToFile("temp.txt", Main.INPUT_FILE);
                //Candidate is better than local, or Candidate implements more jobs than best
                if (candidate.getTotalDistance() < localOptimum.getTotalDistance() || nJobsNotAdded < minJobsNotAdded) {
                    localOptimum = new Solution();
                    long timestamp = System.currentTimeMillis() - (endTime - duration);
                    if (localOptimum.getTotalDistance() < best.getTotalDistance() || nJobsNotAdded < minJobsNotAdded) {
                        best = localOptimum;
                    }
                    minJobsNotAdded = nJobsNotAdded;
                    System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + mode + "\t\t" + (nJobsNotAdded == 0 ? "f": "nf"));
                    Listener.getInstance().newSolutionFound(localOptimum);
                } else {
                    //Candidate not better than local, but maybe it will be accepted with simulated annealing
                    if (localOptimum.getTotalDistance() < candidate.getTotalDistance()) {
                        double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance()) / currentTemp);
                        if (localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                        double random = r.nextDouble();
                        if (random <= acceptRate) {
                            //counter++;
                            localOptimum = candidate;
                            long timestamp = System.currentTimeMillis() - (endTime - duration);
                            DecimalFormat df = new DecimalFormat("#.##");
                            System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + df.format(acceptRate * 100) + "%\t\t" + df.format(currentTemp));
                            Listener.getInstance().newSolutionFound(candidate);
                        }
                    }
                }
            }
            modeQueue.offer(mode);
            mode = modeQueue.poll();
            localOptimum.loadSolution();

            timesRun++;
            counter++;
            if (counter == 50) {
                double x_value = ((double)(System.currentTimeMillis() - startTime))/((double) duration)*750;
                currentTemp = temperature * Math.pow(0.995, x_value); //temperatuur functie
                Listener.getInstance().updateTemperature(currentTemp);
                counter = 0;
            }

        }
        System.out.println("Times run: " + timesRun);
        best.loadSolution();
        return best;
    }

    /**
     * Deze methode zoekt welke Jobs moeten verwijderd worden op een willekeurig deel van de Route van een Truck te verwijderen
     * @param t Truck waarvan een deel van de Route moet worden afgebroken worden
     * @return Lijst van Jobs die moeten verwijderd worden
     */
    public List<Job> removePartOfRoute(Truck t){
        List<Job> deletedJobs = new ArrayList<>();
        //Aantal stops die de Truck maakt (zonder start en eind)
        int nStops = t.getRoute().getStops().size()-2;
        //Aantal stops die verwijderd moeten worden
        int range = r.nextInt(nStops+1);
        int start;

        if(range == nStops) start = 1;
        else start = r.nextInt(nStops - range)+1;

        //We selecteren alle machines die op deze stops worden gedropt/gecollect
        Set<Machine> machinesToRemove = new HashSet<>();
        for (int i = start; i < (start+range); i++) {
            machinesToRemove.addAll(t.getRoute().getStops().get(i).getDrop());
            machinesToRemove.addAll(t.getRoute().getStops().get(i).getCollect());
        }

        //Kijken welke jobs hiermee overeen komen
        for(Map.Entry<Job, Move> entry: t.getJobMoveMap().entrySet()){
            if(machinesToRemove.contains(entry.getValue().getMachine())){
                deletedJobs.add(entry.getKey());
            }
        }

        return deletedJobs;

    }

    /**
     * Deze methode zoekt de beste Truck om een Job j uit te voeren en laat deze hem uitvoeren
     * @param j Job die moet uitgevoerd worden
     * @return true als de job succesvol afgehandeld is
     */
    public boolean assignJobToBestTruck(Job j) {
        List<Proposal> proposals = new ArrayList<>();
        //Proposals verzamelen
        for (Truck t : trucks) {
            proposals.addAll(t.getProposals(j));
        }
        if (!proposals.isEmpty()) {
            //Beste proposal zoeken
            Proposal bestProposal = proposals.get(0);
            for (Proposal p : proposals) {
                if (p.getCost() < bestProposal.getCost()) {
                    bestProposal = p;
                }
            }
            //Job toevoegen aan beste truck
            Truck truck = bestProposal.getTruck();
            Job job = bestProposal.getPrimaryJob();
            Move move = bestProposal.getMove();

            truck.addJob(job, move);
            jobTruckMap.put(job, truck);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deze methode voegt een Job toe aan de beste truck, maar houdt ook rekening met een eventuele 2de Job die tegelijk
     * kan gecomplete worden. Als iemand een beter naam voor de methode weet pas maar aan.
     * @param j Job die moet toegevoegd worden
     * @return true als de job succesvol is uitgevoerd
     */
    public boolean assignJobToBestTruck2(Job j) {
        //Alle mogelijke moves bekijken, en checken welke secondary job ze completen
        HashMultimap<Job, Move> secondJobMoveMap = HashMultimap.create();
        List<Move> possibleMoves = new ArrayList<>(j.generatePossibleMoves());
        for (Move m : possibleMoves) {
            secondJobMoveMap.put(m.completesSecondJob(j), m);
        }

        List<Job> secondaryJobs = new ArrayList<>(secondJobMoveMap.keySet());
        if(secondaryJobs.isEmpty()){
            //In sommige gevallen kan het zijn dat een dropjob geen mogelijke moves meer vindt.
            //Bijvoorbeeld als alle collects van dit type al terug toegevoegd zijn en er geen machines in het depot staan.
            //Vandaar deze check
            return false;
        }
        //Selecteer een willekeurige secondary job om te completen (kan ook null zijn)
        Job selectedSecondaryJob = secondaryJobs.get(r.nextInt(secondaryJobs.size()));
        Set<Move> toCheckMoves = secondJobMoveMap.get(selectedSecondaryJob);

        //Vraag proposals van alle trucks voor deze set van Moves
        List<Proposal> proposals = new ArrayList<>();
        for (Truck t : trucks) {
            proposals.addAll(t.getProposals(j, toCheckMoves));
        }
        if (!proposals.isEmpty()) {
            //Beste proposal zoeken
            Proposal bestProposal = proposals.get(0);
            for (Proposal p : proposals) {
                if (p.getCost() < bestProposal.getCost()) {
                    bestProposal = p;
                }
            }

            //Uitvoeren
            Truck truck = bestProposal.getTruck();
            Job job = bestProposal.getPrimaryJob();
            Move move = bestProposal.getMove();


            truck.addJob(job, move);
            jobTruckMap.put(job, truck);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Enum die de modus aangeeft van de simulated annealing
     */
    private enum Mode {
        MTYPE, TRUCK, NEARBY;

        public static Queue<Mode> createQueue() {
            Queue<Mode> modeQueue = new LinkedList<>();
            modeQueue.offer(Mode.MTYPE);
            modeQueue.offer(Mode.NEARBY);
            modeQueue.offer(Mode.TRUCK);
            modeQueue.offer(Mode.MTYPE);
            modeQueue.offer(Mode.NEARBY);
            modeQueue.offer(Mode.TRUCK);
            return modeQueue;
        }
    }
}


