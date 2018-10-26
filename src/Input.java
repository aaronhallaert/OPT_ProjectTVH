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
    public ArrayList<Node> depots;
    public ArrayList<Truck> trucks;





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
            double b = sc.nextDouble(); //hier zit de fout
            double c = sc.nextDouble();
            locations.add(new Node(a,b,c,false));
        }


        //DEPOTS
        sc.nextLine();
        int aantalDepots= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            int depotId= sc.nextInt();
            locations.get(sc.nextInt()).setDepot();
        }

        //TRUCKS
        sc.nextLine();
        trucks= new ArrayList<>();
        int aantalTrucks= Integer.parseInt(sc.nextLine().split(" ")[1]);


        //MACHINE_TIMES






    }
}
