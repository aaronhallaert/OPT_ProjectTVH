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

        Solution best = simulatedAnnealingJeroen(20000, 20, 5, 1, 0);
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
                if (!assignJobToBestTruck(j,true)) System.out.println("error");
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
        boolean doMachineTypes = false;
        Mode mode = Mode.MACHINETYPE;
        while (System.currentTimeMillis() < endTime) {

            List<Job> selectedJobs = new ArrayList<>();

            switch(mode){
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
                    for(Edge e: randomJob.getFixedLocation().getSortedEdgeList()){
                        if(locationJobMap.containsKey(e.getTo())){
                            selectedJobs.add(locationJobMap.get(e.getTo()));
                            if(selectedJobs.size() == nJobsToRemove){
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
            switch(mode){
                case MACHINETYPE:
                    for(Job j: selectedJobs){
                        if(j.notDone()){
                            allJobsAdded = assignJobToBestTruck2(j);
                        }
                    }
                    break;
                case TRUCK:
                    for (Job j : selectedJobs) {
                        if (j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                            allJobsAdded = assignJobToBestTruck2(j);
                            //allJobsAdded = assignJobToRandomTruck(j, true);
                        }
                    }
                case NEARBY:
                    for(Job j: selectedJobs){
                        if(j.notDone()){
                            allJobsAdded = assignJobToBestTruck2(j);
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
                    System.out.println(timestamp + "\t\t" + localOptimum.getTotalDistance()+"\t\t"+mode);
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
            mode = mode.next();
            localOptimum.loadSolution();
            if (counter > 1) {
                counter = 0;
                currentTemp = 0.95 * currentTemp;
            }
        }
        best.loadSolution();
        return best;
    }

    public Solution simulatedAnnealingAaron(int duration, double temperature, int nJobsToRemove, int nTrucksToRemove) {

        long endTime = System.currentTimeMillis() + duration;
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        //LinkedList<Integer> tabu = new LinkedList<>();
        double currentTemp = temperature;
        int counter = 0;
        Random r = new Random();
        while (System.currentTimeMillis() < endTime) {
            //System.out.println("simulated annealing aaron");
            /* truck losmaken ipv machinetypes --------------------------------------*/
            Set<Integer> randomIndices = new HashSet<>();
            for (int i = 0; i < nTrucksToRemove; i++) {
                int randomIndex = r.nextInt(trucks.size());
                while (randomIndices.contains(randomIndex) || trucks.get(randomIndex).getJobMoveMap().size() == 0) {
                    randomIndex = r.nextInt(trucks.size());
                }
                randomIndices.add(randomIndex);
            }

            List<Job> allJobsOfTruck = new ArrayList<>();
            for (Integer random : randomIndices) {
                allJobsOfTruck.addAll(new ArrayList<>(trucks.get(random).getJobMoveMap().keySet()));
            }


            Collections.shuffle(allJobsOfTruck);
            //Take n random jobs of this type
            List<Job> deletedJobs = new ArrayList<>(allJobsOfTruck.subList(0, (allJobsOfTruck.size() < nJobsToRemove ? allJobsOfTruck.size() : nJobsToRemove)));
            //Remove them from their trucks
            for (Job j : deletedJobs) {
                Truck t = jobTruckMap.get(j);
                if (t != null) { //Het kan zijn dat bepaalde jobs niet worden uitgevoerd door een truck omdat ze al gecompleet worden door een andere job
                    t.removeJob(j, true);
                    jobTruckMap.remove(j);
                }
            }
            //Check if any other jobs are now uncompleted
            for (Job j : jobs) {
                if (j.notDone() && !deletedJobs.contains(j)) {
                    deletedJobs.add(j);
                }
            }

            Collections.shuffle(deletedJobs);

            boolean allJobsAdded = true;
            for (Job j : deletedJobs) {
                if (j.notDone()) { //Enkel als je job nog niet vervolledigd is willen we hem opnieuw toevoegen
                    //List<Move> allMoves = j.generatePossibleMoves();
                    //allJobsAdded = assignRandomMoveToBestTruck(j, allMoves.get((int) (Math.random()*allMoves.size())));
                    allJobsAdded = assignJobToBestTruck(j, true);
                }
            }
            if (allJobsAdded) {
                Solution candidate = new Solution();
                //System.out.println(candidate.getTotalDistance());
                //Candidate is better than local
                if (candidate.getTotalDistance() < localOptimum.getTotalDistance()) {
                    //if(!tabu.contains(candidate.getHash())) {
                    //counter++;
                    localOptimum = new Solution();
                    //tabu.add(candidate.getHash());
                    long timestamp = System.currentTimeMillis() - (endTime - duration);
                    if (localOptimum.getTotalDistance() < best.getTotalDistance()) {
                        best = localOptimum;
                        System.out.println(timestamp + "\t" + localOptimum.getTotalDistance());
                    }
                    System.out.println(timestamp + "\t" + localOptimum.getTotalDistance());
                } else {
                    //Candidate not better than local, but maybe it will be accepted with simulated annealing
                    if (localOptimum.getTotalDistance() < candidate.getTotalDistance()) {
                        double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance()) / currentTemp);
                        if (localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                        double random = r.nextDouble();
                        if (random <= acceptRate) {
                            counter++;
                            //System.out.println("worse candidate accepted with " + acceptRate + " (" + currentTemp + ")");
                            localOptimum = candidate;
                            //tabu.add(candidate.getHash());
                            long timestamp = System.currentTimeMillis() - (endTime - duration);
                            DecimalFormat df = new DecimalFormat("#.##");
                            System.out.println(timestamp + "\t" + localOptimum.getTotalDistance() + "\t" + df.format(acceptRate * 100) + "%\t" + df.format(currentTemp));

                        }
                    }
                }
            }
            localOptimum.loadSolution();
            if (counter > 1) {
                counter = 0;
                currentTemp = 0.7 * currentTemp;
            }
        }
        best.loadSolution();
        return best;
    }

    public Solution testje(int duration, double temperature) {
        long endTime = System.currentTimeMillis() + duration;
        Solution best = new Solution();
        Solution localOptimum = new Solution();
        //LinkedList<Integer> tabu = new LinkedList<>();
        int counter = 0;
        Random r = new Random();
        while (System.currentTimeMillis() < endTime) {
            Job randomJob = jobs.get(r.nextInt(jobs.size()));
            Truck oldTruck = jobTruckMap.get(randomJob);
            int oldDistance = localOptimum.getTotalDistance();
            if (oldTruck != null) { //Het kan zijn dat bepaalde jobs niet worden uitgevoerd door een truck omdat ze al gecompleet worden door een andere job
                oldTruck.removeJob(randomJob, true);
                jobTruckMap.remove(randomJob);

                for(Truck t: trucks){

                }
                //Kijken welke jobs er misschien nog "affected" zijn door het verwijderen van deze job
                ArrayList<Job> affectedJobs = new ArrayList<>();
                boolean allAffectedJobsAdded = false;
                while(!allAffectedJobsAdded) {
                    allAffectedJobsAdded = true;
                    for (Job j : jobs) {
                        if (j.notDone() && !affectedJobs.contains(j)) {
                            allAffectedJobsAdded = false;
                            affectedJobs.add(j);
                            //Als deze job nog in een Truck zat moeten we hem deletne
                            if (jobTruckMap.containsKey(j)) {
                                oldTruck.removeJob(j, true);
                                jobTruckMap.remove(j);
                            }
                            break;
                        }
                    }
                }
                HashMultimap<Job, Proposal> proposals = HashMultimap.create();
                //We voegen hier ook alle proposals toe van de affected jobs
                for (Job j : affectedJobs) {
                    for (Truck t : trucks) {
                        proposals.putAll(j, t.getProposals(j));
                    }
                }
                //Bepalen welke proposals we zullen uitvoeren
                ArrayList<Proposal> acceptedProposals = new ArrayList<>();
                HashMap<Job, Proposal> bestProposalPerJob = new HashMap<>();
                for (Proposal p : acceptedProposals) {
                    if (p.getCost() < bestProposalPerJob.get(p.getPrimaryJob()).getCost()) {
                        bestProposalPerJob.put(p.getPrimaryJob(), p);
                    }
                }


                boolean allJobsFullfilled = true;
                allJobsFullfilled = assignJobToRandomTruck(randomJob, true);
                for (Job j : affectedJobs) {
                    allJobsFullfilled = assignJobToRandomTruck(j, true);
                }
                Solution candidate = new Solution();
                if (allJobsFullfilled && candidate.getTotalDistance() < localOptimum.getTotalDistance()) {
                    counter++;
                    localOptimum = candidate;
                    if (localOptimum.getTotalDistance() < best.getTotalDistance()) {
                        best = new Solution();
                    }
                    System.out.println(localOptimum.getTotalDistance());
                } else {
                    if (allJobsFullfilled && candidate.getTotalDistance() > localOptimum.getTotalDistance() + 30) {
                        double acceptRate = Math.exp((localOptimum.getTotalDistance() - candidate.getTotalDistance()) / temperature);
                        if (localOptimum.getTotalDistance() == candidate.getTotalDistance()) acceptRate = 0;
                        double random = r.nextDouble();
                        if (random <= acceptRate) {
                            counter++;
                            System.out.println("worse candidate accepted with " + acceptRate + " (" + temperature + ")");
                            localOptimum = candidate;
                            System.out.println(localOptimum.getTotalDistance());

                        }
                    }
                    localOptimum.loadSolution();
                }
                if (counter > 1) {
                    counter = 0;
                    temperature = 0.8 * temperature;
                }
            }

        }
        return localOptimum;
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

    public boolean assignJobToBestTruck(Job job, boolean bestMove) {
        Truck optimalTruck = null;
        Route optimalRoute = null;
        Move optimalMove = null;
        int minAddedCost = Integer.MAX_VALUE;
        for (Truck t : trucks) {
            int cost = t.getRoute().getCost();
            LinkedList<Stop> previousOrder = new LinkedList<>(t.getRoute().getStops());
            if (t.addJob(job, bestMove)) {
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
                t.getRoute().setStops(previousOrder);
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
    }

    //TODO: uitleg schrijven hierbij
    public boolean assignJobToBestTruck2(Job j){
        //TODO: random move kiezen vooralleer de proposals te vragen
        List<Proposal> proposals = new ArrayList<>();
        for (Truck t : trucks) {
            proposals.addAll(t.getProposals(j));
        }
        Random r = new Random();
        if (!proposals.isEmpty()) {
            //We willen van elk type proposal de beste overhouden.
            //Met andere woorden de beste proposal van elke mogelijk combinatie van primary en secondary job
            proposals = Proposal.getBestProposalPerJobCombination(proposals);

            Proposal randomProposal = proposals.get(r.nextInt(proposals.size()));
            //Proposal randomProposal = proposals.get(0);
            for(Proposal p: proposals){
                if(p.getCost() < randomProposal.getCost()){
                    randomProposal = p;
                }
            }
            Truck truck = randomProposal.getTruck();
            Job job = randomProposal.getPrimaryJob();
            Move move = randomProposal.getMove();
            Route route = randomProposal.getRoute();


            truck.addJob(job, move, route);
            jobTruckMap.put(job, truck);
            return true;
        } else {
            return false;
        }
    }

    public boolean assignRandomMoveToBestTruck(Job job) {
        Random r = new Random();
        List<Move> moves = job.generatePossibleMoves();
        Move move = moves.get(r.nextInt(moves.size()));
        Truck optimalTruck = null;
        Route optimalRoute = null;
        Move optimalMove = null;
        int minAddedCost = Integer.MAX_VALUE;
        for (Truck t : trucks) {
            int cost = t.getRoute().getCost();
            LinkedList<Stop> previousOrder = new LinkedList<>(t.getRoute().getStops());
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
                t.getRoute().setStops(previousOrder);
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
    }

    private static enum Mode{
        MACHINETYPE, TRUCK, NEARBY;

        //Methode om de volgende te vinden;
        public Mode next(){
            switch (this){
                case MACHINETYPE: return TRUCK;
                case TRUCK: return NEARBY;
                case NEARBY: return MACHINETYPE;
                default: return MACHINETYPE;
            }
        }
    }
}


