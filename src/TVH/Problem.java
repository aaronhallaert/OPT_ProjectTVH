package TVH;

import TVH.Entities.Job.*;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.*;
import TVH.Entities.Truck.*;
import com.google.common.collect.HashMultimap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        //System.out.println("Input read");

    }

    /**
     * Los het probleem op
     *
     * @return
     */
    public Solution solve() {
        createInitialSolution();
        Solution init = new Solution();
//        for(Truck t: trucks){
//            t.optimizeTruck();
//        }
        System.out.println(init);
        try {
            init.writeToFile("temp.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Solution best = init;

        Solution best = simulatedAnnealingAaron(1000 * 60 * 1, 30, 8, 2, 2);
        //best.loadSolution();
        //System.out.println("start second annealing");
        //best= simulatedAnnealingJeroen(20000,100, Integer.MAX_VALUE,1);
        /*best.loadSolution();
        for (Truck truck : trucks) {
            truck.optimizeTruck();
        }
        best = new Solution();*/
        //Solution best = simulatedAnnealingJeroen(100000, 50, Integer.MAX_VALUE);
        //Solution best = testje(600000, 20);
        System.out.println(best);
        System.out.println("DEBUG:");
        System.out.println("Init: " + init.getTotalDistance());
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
                if (!assignJobToBestTruck(j, true)) System.out.println("error");
            }
        }
    }

    public Solution simulatedAnnealingJeroen(int duration, double temperature, int nJobsToRemove, int nMachineTypesToRemove, int nTrucksToRemove) {
        long endTime = System.currentTimeMillis() + duration;
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        //LinkedList<Integer> tabu = new LinkedList<>();
        double currentTemp = temperature;
        int counter = 0;
        Random r = new Random();
        Queue<Mode> modeQueue = Mode.createQueue();
        Mode mode = modeQueue.poll();

        while (System.currentTimeMillis() < endTime) {

            List<Job> selectedJobs = new ArrayList<>();

            switch (mode) {
                case MACHINETYPE:
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
                        selectedJobs.addAll(t.getJobMoveMap().keySet());
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
                    t.removeJob(j, true);
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

            boolean allJobsAdded = true;
            switch (mode) {
                case MACHINETYPE:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
                    break;
                case TRUCK:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                            if (!assignJobToBestTruck(j, true)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
                case NEARBY:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
            }
            if (allJobsAdded) {
                Solution candidate = new Solution();
                //Candidate is better than local
                if (candidate.getTotalDistance() < localOptimum.getTotalDistance()) {
                    //counter++;
                    localOptimum = new Solution();
                    long timestamp = System.currentTimeMillis() - (endTime - duration);
                    if (localOptimum.getTotalDistance() < best.getTotalDistance()) {
                        best = localOptimum;
                    }
                    System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + mode);
                } else {
                    //Candidate not better than local, but maybe it will be accepted with simulated annealing
                    if (localOptimum.getTotalDistance() < candidate.getTotalDistance()) {
                        double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance()) / currentTemp);
                        if (localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                        double random = r.nextDouble();
                        if (random <= acceptRate) {
                            counter++;
                            localOptimum = candidate;
                            long timestamp = System.currentTimeMillis() - (endTime - duration);
                            DecimalFormat df = new DecimalFormat("#.##");
                            System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + df.format(acceptRate * 100) + "%\t\t" + df.format(currentTemp));

                        }
                    }
                }
            }
            modeQueue.offer(mode);
            mode = modeQueue.poll();
            localOptimum.loadSolution();
            if (counter > 1) {
                counter = 0;
                currentTemp = 0.95 * currentTemp;
            }
        }
        best.loadSolution();
        return best;
    }

    public Solution simulatedAnnealingAaron(int duration, double temperature, int nJobsToRemove, int nMachineTypesToRemove, int nTrucksToRemove) {
        long endTime = System.currentTimeMillis() + duration;
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        //LinkedList<Integer> tabu = new LinkedList<>();
        double currentTemp = temperature;
        int counter = 0;
        Random r = new Random();
        Queue<Mode> modeQueue = Mode.createQueue();
        Mode mode = modeQueue.poll();

        while (System.currentTimeMillis() < endTime) {

            List<Job> selectedJobs = new ArrayList<>();

            switch (mode) {
                case MACHINETYPE:
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
                        addTruckToList(selectedJobs, t, false);
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
                    t.removeJob(j, true);
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

            boolean allJobsAdded = true;
            switch (mode) {
                case MACHINETYPE:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
                    break;
                case TRUCK:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                            if (!assignJobToBestTruck(j, true)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
                case NEARBY:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) {
                            if (!assignJobToBestTruck2(j)) {
                                allJobsAdded = false;
                                break;
                            }
                        }
                    }
            }
            if (allJobsAdded) {
                Solution candidate = new Solution();
                //Candidate is better than local
                if (candidate.getTotalDistance() < localOptimum.getTotalDistance()) {
                    //counter++;
                    localOptimum = new Solution();
                    long timestamp = System.currentTimeMillis() - (endTime - duration);
                    if (localOptimum.getTotalDistance() < best.getTotalDistance()) {
                        best = localOptimum;
                    }
                    System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + mode);
                } else {
                    //Candidate not better than local, but maybe it will be accepted with simulated annealing
                    if (localOptimum.getTotalDistance() < candidate.getTotalDistance()) {
                        double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance()) / currentTemp);
                        if (localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                        double random = r.nextDouble();
                        if (random <= acceptRate) {
                            counter++;
                            localOptimum = candidate;
                            long timestamp = System.currentTimeMillis() - (endTime - duration);
                            DecimalFormat df = new DecimalFormat("#.##");
                            System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance() + "\t\t" + df.format(acceptRate * 100) + "%\t\t" + df.format(currentTemp));

                        }
                    }
                }
            }
            modeQueue.offer(mode);
            mode = modeQueue.poll();
            localOptimum.loadSolution();
            if(counter>1) {
                currentTemp = 0.95 * currentTemp;
                counter=0;
            }
        }
        best.loadSolution();
        return best;
    }

    public void addTruckToList(List<Job> selectedJobs, Truck t, boolean stop) {


            ArrayList<Job> truckJobs = new ArrayList<>(t.getJobMoveMap().keySet());
            Random r = new Random();
            int begin = r.nextInt(t.getJobMoveMap().keySet().size());
            int eind = r.nextInt(t.getJobMoveMap().keySet().size());
            int verschil = eind - begin;
            double minVerschil = t.getJobMoveMap().keySet().size() / 7;
            double maxVerschil = t.getJobMoveMap().keySet().size() / 1;


            boolean distanceBool = false;
            while (eind < begin && verschil < minVerschil && verschil < maxVerschil && !distanceBool) {
                begin = r.nextInt(t.getJobMoveMap().keySet().size());
                eind = r.nextInt(t.getJobMoveMap().keySet().size());
                verschil = eind - begin;
                if (verschil > 0) {
                    distanceBool = isSubRouteCompact(truckJobs.subList(begin, eind));
                }
            }


            for (int i = begin; i < eind; i++) {
                selectedJobs.add(truckJobs.get(i));
            }

        if(!stop) {
            for (Edge edge : truckJobs.get(begin).getFixedLocation().getSortedEdgeList()) {
                Truck nextTruck= jobTruckMap.get(locationJobMap.get(edge.getTo()));
                if (nextTruck!=null && nextTruck != t) {

                    addTruckToList(selectedJobs, nextTruck, true);
                    break;


                }
            }
        }

    }

    /**
     * checkt of deel van route dicht bij elkaar ligt
     *
     * stel we hebben 3 jobs op een route, als de volgende node in de route niet behoort tot een van de *insert number*
     * dichtste nodes, return false
     * @param jobs sublist van een route
     * @return
     */
    public boolean isSubRouteCompact(List<Job> jobs){
        for (int i = 0; i < jobs.size(); i++) {
            Set<Location> nearestLocations=new HashSet<>();
            for (int i1 = 0; i1 < 30; i1++) {
                nearestLocations.add(jobs.get(i).getFixedLocation().getSortedEdgeList().getFirst().getTo());
            }

            if(i!=jobs.size()-1){
                if(!nearestLocations.contains(jobs.get(i+1).getFixedLocation())){
                    return false;
                }
            }
        }

        return true;
    }

    public boolean assignJobToRandomTruck(Job job, boolean bestMove) {
        ArrayList<Truck> trucksToCheck = new ArrayList<>(trucks);
        Random r = new Random();
        while (true) {
            Truck randomTruck = trucksToCheck.get(r.nextInt(trucksToCheck.size()));
            if (randomTruck.addJob(job, bestMove)) {
                jobTruckMap.put(job, randomTruck);
                break;
            } else trucksToCheck.remove(randomTruck);
            if (trucksToCheck.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean assignJobToBestTruck(Job j, boolean bestMove) {
        //TODO: random job combinatie uitkiezen vooralleer de proposals te vragen, zal voor veel meer snelheid zorgen
        List<Proposal> proposals = new ArrayList<>();
        for (Truck t : trucks) {
            proposals.addAll(t.getProposals(j));
        }
        if(!proposals.isEmpty()){
            //Beste proposal zoeken
            Proposal bestProposal = proposals.get(0);
            for(Proposal p: proposals){
                if(p.getCost() < bestProposal.getCost()){
                    bestProposal = p;
                }
            }
            //Toevoegen
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

    //TODO: uitleg schrijven hierbij
    public boolean assignJobToBestTruck2(Job j){
        //TODO: random job combinatie uitkiezen vooralleer de proposals te vragen, zal voor veel meer snelheid zorgen
        Random r = new Random();
        HashMultimap<Job, Move> secondJobMoveMap = HashMultimap.create();
        for(Move m: j.generatePossibleMoves()){
            secondJobMoveMap.put(m.completesSecondJob(j), m);
        }
        List<Job> secondaryJobs = new ArrayList<>(secondJobMoveMap.keySet());
        Job selectedSecondaryJob = secondaryJobs.get(r.nextInt(secondaryJobs.size()));
        Set<Move> toCheckMoves = secondJobMoveMap.get(selectedSecondaryJob);

        List<Proposal> proposals = new ArrayList<>();
        for (Truck t : trucks) {
            proposals.addAll(t.getProposals(j, toCheckMoves));
        }
        if (!proposals.isEmpty()) {
            //We willen van elk type proposal de beste overhouden.
            //Met andere woorden de beste proposal van elke mogelijk combinatie van primary en secondary job
            Proposal bestProposal = proposals.get(0);
            for(Proposal p: proposals){
                if(p.getCost() < bestProposal.getCost()){
                    bestProposal = p;
                }
            }

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

    /*public boolean assignRandomMoveToBestTruck(Job job) {
        Random r = new Random();
        List<Move> moves = job.generatePossibleMoves();
        Move move = moves.get(r.nextInt(moves.size()));
        Truck optimalTruck = null;
        Route optimalRoute = null;
        Move optimalMove = null;
        int minAddedCost = Integer.MAX_VALUE;
        for (Truck t : trucks) {
            int cost = t.getRoute().getCost();
            Route backup = new Route(t.getRoute(), false);
            if (t.addJob(job, move)) {
                int addedCost = t.getRoute().getCost() - cost;
                //We removen de job opnieuw en zetten de volgorde terug zoals voordien.
                //Dit om te voorkomen dat hij geen oplossing meer vindt een keer we hem echt willen toevoegen
                if (addedCost < minAddedCost) {
                    minAddedCost = addedCost;
                    optimalTruck = t;
                    optimalRoute = new Route(t.getRoute());
                    optimalMove = t.getJobMoveMap().get(job);
                }
                t.removeJob(job, false);
                t.setRoute(backup);
            }
        }
        if (optimalTruck != null) {
            //Route die we daarnet gevonden hebben terug toevoegen en de job en move terug toevoegen aan de hashmap;
            optimalTruck.addJob(job, optimalMove, optimalRoute);
            jobTruckMap.put(job, optimalTruck);
            return true;
        } else {
            return false;
        }
    }*/

    private static enum Mode{
        MACHINETYPE, TRUCK, NEARBY;

        //Methode om de volgende te vinden;
        public static Queue<Mode> createQueue(){
            Queue<Mode> modeQueue = new LinkedList<>();
            modeQueue.offer(Mode.MACHINETYPE);
            modeQueue.offer(Mode.NEARBY);
            modeQueue.offer(Mode.TRUCK);
           // modeQueue.offer(Mode.MACHINETYPE);
            modeQueue.offer(Mode.NEARBY);
            modeQueue.offer(Mode.TRUCK);
            return modeQueue;
        }
    }
}


