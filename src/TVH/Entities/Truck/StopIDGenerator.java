package TVH.Entities.Truck;

public class StopIDGenerator {
    static StopIDGenerator instance = new StopIDGenerator();
    int i = 0;

    private StopIDGenerator(){

    }
    public int getID(){
        i++;
        return i;
    }

    public static StopIDGenerator getInstance(){
        return instance;
    }
}
