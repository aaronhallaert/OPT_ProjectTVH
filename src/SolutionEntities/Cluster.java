package SolutionEntities;

import Algorithm.Problem;
import Entities.Depot;
import Entities.Job;
import Entities.MachineType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * elke cluster heeft een depot, dus cluster is depot gelinkt met lijst van jobs
 */
public class Cluster {

    private int id;
    private Depot depot;
    private Set<Job> clusterJobs;
    private HashMap<MachineType, Integer> beschikbaar;
    private HashMap<MachineType, Integer> afTeLeveren;
    private HashMap<MachineType, Integer> nodigeMachineTypes=new HashMap<>();
    private HashMap<MachineType, Integer> overbodigeMachineTypes=new HashMap<>();
    private Set<MachineType> genoegMachineTypes=new HashSet<>();


    public Cluster(Depot depot, int id){
        this.id=id;
        this.depot=depot;
        clusterJobs=new HashSet<>();
        beschikbaar=new HashMap<>();
        afTeLeveren= new HashMap<>();
        nodigeMachineTypes=new HashMap<>();
        overbodigeMachineTypes=new HashMap<>();
        genoegMachineTypes=new HashSet<>();
    }


    /**
     * print de behoeftes, overbodige en genoeg machine types
     */
    public void printBehoeftesOverbodige(){
        // uitprinten behoeftes en eigendommen van clusters
        System.out.println("NODIG machines van type");
        for(Map.Entry<MachineType, Integer> entry2: nodigeMachineTypes.entrySet()){
            System.out.println("\t"+entry2.getKey().toString()+": "+entry2.getValue());
        }

        System.out.println("OVERBODIG machines van type");
        for(Map.Entry<MachineType, Integer> entry3: overbodigeMachineTypes.entrySet()){
            System.out.println("\t"+entry3.getKey().toString()+": "+entry3.getValue());
        }

        System.out.println("GENOEG machines van deze types");
        System.out.print("\t");
        for (MachineType genoegMachineType : genoegMachineTypes) {
            System.out.print(genoegMachineType +" - ");
        }
        System.out.println("\n");
    }

    public void fillNeeded(HashMap<Depot, Cluster> clusters){
        //overlopen van nodige machine types
        for(Map.Entry<MachineType, Integer> nodigeMachineType: nodigeMachineTypes.entrySet()){

            //overlopen van alles clusters op zoek naar nodige machine type
            for(Map.Entry<Depot, Cluster> cluster: clusters.entrySet()){
                // TODO werkt niet
                if(cluster.getValue().getOverbodigeMachineTypes().containsKey(nodigeMachineType.getKey())){
                    System.out.println("gevonden voor cluster "+id+" => cluster "+cluster.getValue().getId()+" heeft een machinetype "+nodigeMachineType.getKey()+" over" );
                }
            }

        }
    }

    /**
     * berekend aantal nodige machine types en van welk type
     *                 overbodige
     *          van welke machines er net genoeg zijn
     * @param problem bevat alle machinetypes
     */
    public void computeNeeded(Problem problem){
        // behoeftes en eigendommen van clusters berekenen
        for (MachineType machineType : problem.machineTypes) {
            if (beschikbaar.get(machineType) < afTeLeveren.get(machineType)) {
                nodigeMachineTypes.putIfAbsent(machineType, 0);
                nodigeMachineTypes.replace(machineType, nodigeMachineTypes.get(machineType)+1);
            }
            else if (beschikbaar.get(machineType) > afTeLeveren.get(machineType)) {
                overbodigeMachineTypes.putIfAbsent(machineType,0);
                overbodigeMachineTypes.replace(machineType, overbodigeMachineTypes.get(machineType)+1);
            }
            else{
                genoegMachineTypes.add(machineType);
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<MachineType, Integer> getNodigeMachineTypes() {
        return nodigeMachineTypes;
    }

    public void setNodigeMachineTypes(HashMap<MachineType, Integer> nodigeMachineTypes) {
        this.nodigeMachineTypes = nodigeMachineTypes;
    }

    public HashMap<MachineType, Integer> getOverbodigeMachineTypes() {
        return overbodigeMachineTypes;
    }

    public void setOverbodigeMachineTypes(HashMap<MachineType, Integer> overbodigeMachineTypes) {
        this.overbodigeMachineTypes = overbodigeMachineTypes;
    }

    public Set<MachineType> getGenoegMachineTypes() {
        return genoegMachineTypes;
    }

    public void setGenoegMachineTypes(Set<MachineType> genoegMachineTypes) {
        this.genoegMachineTypes = genoegMachineTypes;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public Set<Job> getClusterJobs() {
        return clusterJobs;
    }

    public void setClusterJobs(Set<Job> clusterJobs) {
        this.clusterJobs = clusterJobs;
    }

    public HashMap<MachineType, Integer> getBeschikbaar() {
        return beschikbaar;
    }

    public void setBeschikbaar(HashMap<MachineType, Integer> beschikbaar) {
        this.beschikbaar = beschikbaar;
    }

    public HashMap<MachineType, Integer> getAfTeLeveren() {
        return afTeLeveren;
    }

    public void setAfTeLeveren(HashMap<MachineType, Integer> afTeLeveren) {
        this.afTeLeveren = afTeLeveren;
    }
}
