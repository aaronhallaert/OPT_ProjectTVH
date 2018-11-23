package TVH;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Config {

    private int temperature;

    private int trucks;

    private int machinetypes;

    private int jobs;

    private String type;

    private int timefactor;

    private int orderfactor;

    private int frviolationsfactor;

    private int distancefactor;

    private int time;

    private String problem;

    public void update(int temperature, int trucks, int machinetypes, int jobs, String type, int timefactor, int orderfactor, int frviolationsfactor, int distancefactor, int time, String problem) {
        this.temperature = temperature;
        this.trucks = trucks;
        this.machinetypes = machinetypes;
        this.jobs = jobs;
        this.type = type;
        this.timefactor = timefactor;
        this.orderfactor = orderfactor;
        this.frviolationsfactor = frviolationsfactor;
        this.distancefactor = distancefactor;
        this.time = time;
        this.problem = problem;
    }

    public Config(){

    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTrucks() {
        return trucks;
    }

    public void setTrucks(int trucks) {
        this.trucks = trucks;
    }

    public int getMachinetypes() {
        return machinetypes;
    }

    public void setMachinetypes(int machinetypes) {
        this.machinetypes = machinetypes;
    }

    public int getJobs() {
        return jobs;
    }

    public void setJobs(int jobs) {
        this.jobs = jobs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimefactor() {
        return timefactor;
    }

    public void setTimefactor(int timefactor) {
        this.timefactor = timefactor;
    }

    public int getOrderfactor() {
        return orderfactor;
    }

    public void setOrderfactor(int orderfactor) {
        this.orderfactor = orderfactor;
    }

    public int getFrviolationsfactor() {
        return frviolationsfactor;
    }

    public void setFrviolationsfactor(int frviolationsfactor) {
        this.frviolationsfactor = frviolationsfactor;
    }

    public int getDistancefactor() {
        return distancefactor;
    }

    public void setDistancefactor(int distancefactor) {
        this.distancefactor = distancefactor;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void writeToFile(String file){
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print(temperature +
                    ", "+trucks +
                    ", "+machinetypes +
                    ", "+ jobs +
                    ", " + type +
                    ", " + timefactor +
                    ", " + orderfactor +
                    ", " + frviolationsfactor +
                    ", " + distancefactor +
                    ", " + problem+
                    ", " + time);

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "temperature=" + temperature +
                ", trucks=" + trucks +
                ", machinetypes=" + machinetypes +
                ", jobs=" + jobs +
                ", type='" + type + '\'' +
                ", timefactor=" + timefactor +
                ", orderfactor=" + orderfactor +
                ", frviolationsfactor=" + frviolationsfactor +
                ", distancefactor=" + distancefactor +
                ", problem="+problem+
                ", time="+time+ '\''+
                '}';
    }
}
