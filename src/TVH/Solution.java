package TVH;

import TVH.Entities.Truck;

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
}
