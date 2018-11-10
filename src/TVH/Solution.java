package TVH;

import TVH.Entities.Machine;
import TVH.Entities.Truck.Stop;
import TVH.Entities.Truck.Truck;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private int totalDistance = 0;
    private List<Truck> trucks;


    public Solution(List<Truck> trucks) {
        this.trucks = new ArrayList<>();
        //Current state van trucks kopiëren;
        for(Truck t: trucks){
            this.trucks.add(new Truck(t));
            totalDistance += t.getRoute().calculateDistance();
        }
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public int getTotalUsedTrucks(){
        int i = 0;
        for(Truck t: trucks){
            if(t.getRoute().calculateDistance() > 0) i++;
        }
        return i;
    }

    public void writeToFile(String outputfile) throws IOException {
        PrintWriter writer = new PrintWriter(outputfile);
        writer.println("PROBLEM: "+ Main.INPUT_FILE);
        writer.println("DISTANCE: "+String.valueOf(totalDistance));
        writer.println("TRUCKS: "+String.valueOf(getTotalUsedTrucks()));

        for(Truck t: trucks){
            if(t.getRoute().calculateDistance() > 0){
                writer.print(String.valueOf(t.getTruckId()) + " ");
                writer.print(String.valueOf(t.getRoute().calculateDistance()) + " ");
                writer.print(String.valueOf(t.getRoute().calculateTime()));
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
}
