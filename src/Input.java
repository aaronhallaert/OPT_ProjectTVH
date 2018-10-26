import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Input {

    public String info;
    public int TRUCK_CAPACITY;
    public int TRUCK_WORKING_TIME;
    public int SERVICE_TIME;

    public ArrayList<Node> locations;
    public ArrayList<Truck> trucks;
    public ArrayList<MachineType> machineTypes;
    public ArrayList<Machine> machines;






    public Input(){

    }


    public void fromTxt(File inputFile) throws FileNotFoundException {

        Scanner sc = new Scanner(inputFile);

        info = sc.nextLine().split(": ")[1];
        TRUCK_CAPACITY= Integer.parseInt(sc.nextLine().split(": ")[1]);
        TRUCK_WORKING_TIME=Integer.parseInt(sc.nextLine().split(": ")[1]);
        SERVICE_TIME=Integer.parseInt(sc.nextLine().split(": ")[1]);

        sc.nextLine(); //weggooilijn


        // LOCATIONS
        locations = new ArrayList<Node>();
        int aantalLocations = Integer.parseInt(sc.nextLine().split(" ")[1]);

        for (int i = 0; i < aantalLocations; i++) {
            int locationId =sc.nextInt();
            double latitude = sc.nextDouble();
            double longitude = sc.nextDouble();
            locations.add(new Node(locationId,latitude,longitude,false));
        }


        //DEPOTS
        sc.nextLine();
        sc.nextLine();
        int aantalDepots= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            int nodeId= sc.nextInt();
            int depotId=sc.nextInt();
            locations.get(depotId).setDepot();
        }

        //TRUCKS
        sc.nextLine();
        sc.nextLine();
        trucks= new ArrayList<>();
        int aantalTrucks= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalTrucks; i++) {
            int truckId=sc.nextInt();
            int startLocationId= sc.nextInt();
            int endLocationId= sc.nextInt();
            //@TODO controle constructor Truck
            trucks.add(new Truck(truckId, startLocationId, endLocationId));
        }


        //MACHINE_TYPES
        sc.nextLine();
        sc.nextLine();
        machineTypes=new ArrayList<>();
        int aantalMachineTypes= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachineTypes; i++) {
            int machineTypeId=sc.nextInt();
            int machineTypeVolume=sc.nextInt();
            String machineTypeName= sc.next();

            machineTypes.add(new MachineType(machineTypeId, machineTypeName, machineTypeVolume));
        }

        // MACHINES
        sc.nextLine();
        sc.nextLine();
        machines=new ArrayList<>();
        int aantalMachines= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalMachines; i++) {
            int machineId=sc.nextInt();
            int machineTypeId=sc.nextInt();
            int locationId=sc.nextInt();

            //@TODO constructor machine
            machines.add(new Machine());
        }

        //DROPS
        sc.nextLine();
        sc.nextLine();
        int aantalDrops=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDrops; i++) {
            int dropId=sc.nextInt();
            int machineTypeId=sc.nextInt();
            int locationId=sc.nextInt();

            //@TODO check handling van drop
            locations.get(locationId).getDropOffItems().add(machineTypes.get(machineTypeId));
        }

        //COLLECTS
        sc.nextLine();
        sc.nextLine();
        int aantalCollects=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId=sc.nextInt();
            int machineId=sc.nextInt();
            int locationId=sc.nextInt();

            //@TODO check handling van collect
            locations.get(locationId).getPickupItems().add(machines.get(machineId));
        }


        //@TODO inlezen van matrices



    }
}
