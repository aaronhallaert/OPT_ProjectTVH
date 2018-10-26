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
            int a =sc.nextInt();
            double b = sc.nextDouble();
            double c = sc.nextDouble();
            locations.add(new Node(a,b,c,false));
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
            //@TODO constructor Truck
            trucks.add(new Truck());
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

            //@TODO constructor Machine types
            machineTypes.add(new MachineType());
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

            //@TODO handle drops
        }

        //COLLECTS
        sc.nextLine();
        sc.nextLine();
        int aantalCollects=Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalCollects; i++) {
            int collectId=sc.nextInt();
            int machineId=sc.nextInt();
            int locationId=sc.nextInt();

            //@TODO handle collects
        }


        //@TODO inlezen van matrices



    }
}
