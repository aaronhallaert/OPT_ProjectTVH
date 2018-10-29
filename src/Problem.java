import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Problem {

    public String info;
    public int TRUCK_CAPACITY;
    public int TRUCK_WORKING_TIME;
    public int SERVICE_TIME;

    public ArrayList<Node> nodes = new ArrayList<>();
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
        /*
            Alle nodes inlezen met lat en long
         */
        int aantalLocations = Integer.parseInt(sc.nextLine().split(" ")[1]);

        for (int i = 0; i < aantalLocations; i++) {
            int locationId =sc.nextInt();
            double latitude = sc.nextDouble();
            double longitude = sc.nextDouble();
            nodes.add(new Node(locationId,latitude,longitude,false));
        }


        //DEPOTS

        /*
            Depots zien wij ook gewoon als nodes, het enige wat we moeten doen is een boolean op true zetten;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalDepots= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            int depotId=sc.nextInt();
            int nodeId= sc.nextInt();
            nodes.get(nodeId).setDepot();
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
            Node startLocation = nodes.get(sc.nextInt());
            Node endLocation= nodes.get(sc.nextInt());
            //@TODO controle constructor Truck
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
            Node node= nodes.get(sc.nextInt());
            Machine machine = new Machine(machineId, machineType, node);

            //machine toevoegen aan de juiste node en ook aan de algemene lijst;
            node.addMachine(machine);
            machines.add(machine);
        }

        //DROPS
        /*
            Alle drops toevoegen aan de juiste node;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalDrops=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDrops; i++) {
            int dropId=sc.nextInt();
            MachineType machineType=machineTypes.get(sc.nextInt());
            Node node = nodes.get(sc.nextInt());

            node.getDropOffItems().add(machineType);
        }

        //COLLECTS
        /*
            Alle collects toevoegen aan de juiste node;
         */
        sc.nextLine();
        sc.nextLine();
        int aantalCollects=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId=sc.nextInt();
            Machine machine=machines.get(sc.nextInt());
            Node node = nodes.get(sc.nextInt());

            //@TODO check handling van collect
            node.getPickupItems().add(machine);
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

        //Edges aanmaken met time en distance info, en koppelen aan nodes
        for (int from = 0; from < distanceMatrixSize; from++) {
            for (int to = 0; to < distanceMatrixSize; to++) {
                int time = timeMatrix[from][to];
                int distance = distanceMatrix[from][to];
                if(!(time == 0 && distance == 0)){
                    Node fromNode = nodes.get(from);
                    Node toNode = nodes.get(to);
                    Edge edge = new Edge(fromNode, toNode, time, distance);
                    edges.add(edge);
                    //We voegen ook nog een verwijzing naar de edge toe aan de "from edge" (handig zoeken);
                    fromNode.addEdge(edge);
                }

            }
        }
        System.out.println("Input read");

    }
}
