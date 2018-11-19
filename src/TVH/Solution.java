package TVH;

import TVH.Entities.Job.Job;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Client;
import TVH.Entities.Node.Depot;
import TVH.Entities.Truck.Stop;
import TVH.Entities.Truck.Truck;
import TVH.GUI.SolutionListener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Solution implements Serializable{
    private int totalDistance = 0;
    private ArrayList<Truck> trucks = new ArrayList<>();
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Depot> depots = new ArrayList<>();
    private int hash = 0;

    public Solution() {
        Problem problem = Problem.getInstance();
        //Current state van trucks kopiëren;
        for(Truck t: problem.trucks){
            //Diepe kopie nemen van een truck
            Truck copy = new Truck(t);
            trucks.add(copy);
            totalDistance += copy.getRoute().getTotalDistance();
        }
        //diepe kopies nemen van clients en depots
        for(Client c: problem.clientMap.values()){
            clients.add(new Client(c));
        }
        for(Depot d: problem.depotMap.values()){
            depots.add(new Depot(d));
        }

        //Hash maken
        for(Truck t: trucks){
            hash += Objects.hash(t.getTruckId(),t.getRoute().getStops());
        }

        // hier moet ik de listener triggeren
        // inderdaad tibo, maar dat deed je niet grt
        SolutionListener.getInstance().newSolutionFound(this);
    }

    public void loadSolution(){
        Problem problem = Problem.getInstance();
        problem.nodesMap = new HashMap<>();
        problem.clientMap = new HashMap<>();
        problem.depotMap = new HashMap<>();
        for(Client c: clients){
            Client copy = new Client(c);
            problem.clientMap.put(c.getLocation(), copy);
            problem.nodesMap.put(c.getLocation(), copy);
        }
        for(Depot d: depots){
            Depot copy = new Depot(d);
            problem.depotMap.put(d.getLocation(), copy);
            problem.nodesMap.put(d.getLocation(), copy);
        }
        //De trucks inladen
        //De links tussen Jobs en trucks opnieuw maken in de hashmap
        problem.trucks = new ArrayList<>();
        problem.jobTruckMap = new HashMap<>();
        for(Truck t: trucks){
            Truck copy = new Truck(t);
            problem.trucks.add(copy);
            for(Job j: t.getJobMoveMap().keySet()){
                problem.jobTruckMap.put(j, copy);
            }
        }
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void recalculateDistance(){
        totalDistance = 0;
        for(Truck t: trucks){
            totalDistance += t.getRoute().getTotalDistance();
        }
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public int getTotalUsedTrucks(){
        int i = 0;
        for(Truck t: trucks){
            if(t.getRoute().getTotalDistance() > 0) i++;
        }
        return i;
    }

    public void writeToFile(String outputfile) throws IOException {
        PrintWriter writer = new PrintWriter(outputfile);
        writer.println("PROBLEM: "+ Main.INPUT_FILE);
        writer.println("DISTANCE: "+String.valueOf(totalDistance));
        writer.println("TRUCKS: "+String.valueOf(getTotalUsedTrucks()));

        for(Truck t: trucks){
            if(t.getRoute().getTotalDistance() > 0){
                writer.print(String.valueOf(t.getTruckId()) + " ");
                writer.print(String.valueOf(t.getRoute().getTotalDistance()) + " ");
                writer.print(String.valueOf(t.getRoute().getTotalTime()));
                for(Stop s: t.getRoute().getStops()){
                    writer.print(" ");
                    writer.print(s.getLocation().getLocationID());
                    for(Machine m: s.getCollect()){
                        writer.print(':');
                        writer.print(m.getId());
                    }
                    for(Machine m: s.getDrop()){
                        writer.print(':');
                        writer.print(m.getId());
                    }
                }
                writer.println();
            }
        }
        writer.close();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(totalDistance+"\n");
        for (Truck t : trucks) {
            sb.append(t);
        }
        return sb.toString();
    }

    public int getHash() {
        return hash;
    }

    public void writeBytesToFile(String fileName){

        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(fileName);
            ObjectOutputStream oos= new ObjectOutputStream(fos);

            //int totaldistance
            oos.writeObject(totalDistance);

            //arraylist<Truck> trucks
            oos.writeObject(trucks);

            //arrayList<Client> clients
            oos.writeObject(clients);

            //arrayList<Depot> depots
            oos.writeObject(depots);

            oos.writeObject(hash);

            oos.close();
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void readBytesFromFile(String fileName) {


        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);

            ObjectInputStream ois = new ObjectInputStream(fis);

            this.totalDistance = (int) ois.readObject();

            this.trucks = (ArrayList) ois.readObject();

            this.clients = (ArrayList) ois.readObject();

            this.depots = (ArrayList) ois.readObject();

            this.hash = (int) ois.readObject();

            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void setNull() {
        totalDistance = 0;
        trucks = null;
        clients = null;
        depots = null;
    }
}
