import java.util.ArrayList;
import java.util.List;

public class Stop {

    private Job locatie;
    private double gevuldPercentage;
    private List<Machine> collectItems;
    private List<Machine> dropItems;
    private List<Machine> onTruck;


    public Stop(Job locatie, double gevuldPercentage, List<Machine> collectItems, List<Machine> dropItems, List<Machine> onTruck) {
        this.locatie = locatie;
        this.gevuldPercentage = gevuldPercentage;
        this.collectItems = collectItems;
        this.dropItems = dropItems;
        this.onTruck = onTruck;
    }

    public Stop(Stop s){
        this.locatie = new Job(s.locatie);
        this.gevuldPercentage = s.gevuldPercentage;
        this.collectItems = new ArrayList<>(s.collectItems);
        this.dropItems = new ArrayList<>(s.dropItems);
        this.onTruck = new ArrayList<>(s.onTruck);
    }
}
