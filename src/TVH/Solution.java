package TVH;

import TVH.Entities.Machine;
import TVH.Entities.Stop;
import TVH.Entities.Truck;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private int totalkm = 0;
    private List<Truck> trucks;


    public Solution(List<Truck> trucks) {
        this.trucks = new ArrayList<>();
        //Current state van trucks kopiëren;
        for(Truck t: trucks){
            this.trucks.add(new Truck(t));
            totalkm+= t.getTotalDistance();
        }
    }

    public int getTotalkm() {
        return totalkm;
    }

    public List<Truck> getTrucks() {
        return trucks;
    }

    public int getTotalUsedTrucks(){
        int i = 0;
        for(Truck t: trucks){
            if(t.getTotalDistance() > 0) i++;
        }
        return i;
    }

    public void writeToFile(String outputfile) throws IOException {
        PrintWriter writer = new PrintWriter(outputfile);
        writer.println("PROBLEM: "+ Main.INPUT_FILE);
        writer.println("DISTANCE: "+String.valueOf(totalkm));
        writer.println("TRUCKS: "+String.valueOf(getTotalUsedTrucks()));

        for(Truck t: trucks){
            if(t.getTotalDistance() > 0){
                writer.print(String.valueOf(t.getTruckId()) + " ");
                writer.print(String.valueOf(t.getTotalDistance()) + " ");
                writer.print(String.valueOf(t.getTotalTime()));
                for(Stop s: t.getRoute()){
                    writer.print(" ");
                    writer.print(s.getLocation().getLocationID());
                    for(Machine m: s.getCollectItems()){
                        writer.print(':');
                        writer.print(m.getId());
                    }
                    for(Machine m: s.getDropItems()){
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
