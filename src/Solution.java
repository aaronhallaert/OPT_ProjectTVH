import java.util.ArrayList;
import java.util.List;

public class Solution {
    private int totalkm;
    private List<Truck> trucks;


    public Solution(List<Truck> trucks) {
        this.trucks = new ArrayList<>();
        //Current state van trucks kopiëren;
        for(Truck t: trucks){
            trucks.add(new Truck(t));
        }

    }
}
