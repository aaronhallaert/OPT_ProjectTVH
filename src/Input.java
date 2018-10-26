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
            String nodeLine=sc.nextLine();
            String [] eigenschappenNode= nodeLine.split(" ");
            locations.add(new Node(Integer.parseInt(eigenschappenNode[0]),Double.parseDouble(eigenschappenNode[1]),Double.parseDouble(eigenschappenNode[2]),false));
        }


        //DEPOTS
        sc.nextLine();
        int aantalDepots= Integer.parseInt(sc.nextLine().split(" ")[1]);
        for (int i = 0; i < aantalDepots; i++) {
            String depotLine= sc.nextLine();
            String [] eigenschappenDepot= depotLine.split(" ");
            locations.get(Integer.parseInt(eigenschappenDepot[1])).setDepot();
        }

        //TRUCKS
        sc.nextLine();
        trucks= new ArrayList<>();
        int aantalTrucks= Integer.parseInt(sc.nextLine().split(" ")[1]);


        //MACHINE_TIMES






    }
}
