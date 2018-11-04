package SolutionEntities;

import Algorithm.Problem;
import Entities.*;

import java.util.*;

/**
 * elke cluster heeft een depot, dus cluster is depot gelinkt met lijst van jobs
 */
public class Cluster {

    private int id;
    private Depot depot;
    private Set<Job> clusterJobs;
    private Set<Depot> clusterDepots;
    private HashMap<MachineType, Integer> beschikbaar;
    private HashMap<MachineType, Integer> afTeLeveren;
    private HashMap<MachineType, Integer> nodigeMachineTypes;
    private HashMap<MachineType, Integer> overbodigeMachineTypes;
    private Set<MachineType> genoegMachineTypes;
    private LinkedList<Edge> sortedEdgesToOtherDepots;

    public Cluster(Depot depot, int id){
        this.id=id;
        this.depot=depot;
        clusterJobs=new HashSet<>();
        beschikbaar=new HashMap<>();
        afTeLeveren= new HashMap<>();
        nodigeMachineTypes=new HashMap<>();
        overbodigeMachineTypes=new HashMap<>();
        genoegMachineTypes=new HashSet<>();
        sortedEdgesToOtherDepots=new LinkedList<>();
    }

    public Set<Depot> getClusterDepots() {
        return clusterDepots;
    }

    public void setClusterDepots(Set<Depot> clusterDepots) {
        this.clusterDepots = clusterDepots;
    }

    /**
     * print de behoeftes, overbodige en genoeg machine types
     */
    public void printBehoeftesOverbodige(){
        // uitprinten behoeftes en eigendommen van clusters
        System.out.println("EIGENSCHAPPEN CLUSTER "+ id);
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


    /**
     * zoekt bij andere clusters nodige machinetypes
     * @param clusters alle andere clusters
     */
    public void fillNeeded(Problem problem, HashMap<Depot, Cluster> clusters){
        //overlopen van nodige machine types
        for(Map.Entry<MachineType, Integer> nodigeMachineType: nodigeMachineTypes.entrySet()){

            //overlopen van alles clusters op zoek naar nodige machine type
            // dit doen we via edges om zo eerst bij de dichtste clusters te zoeken en dan pas bij verdere
            for (Edge edge : sortedEdgesToOtherDepots) {
                if(clusters.get(problem.depots.get(edge.getTo())).getOverbodigeMachineTypes().containsKey(nodigeMachineType.getKey())){
                    System.out.println("gevonden voor cluster "+id+" => cluster "+clusters.get(problem.depots.get(edge.getTo())).getId()+" heeft een machinetype "+nodigeMachineType.getKey()+" over" );
                    // ga nu op zoek naar een node die niet ver ligt van huidige cluster depot en die missende machinetype bezit

                    // zoek node die machinetype nodig heeft in huidige cluster
                    Job behoevendeJob=null;
                    for (Job clusterJob : this.getClusterJobs()) {
                        if(clusterJob.getToDropItems().contains(nodigeMachineType.getKey())){
                           behoevendeJob=clusterJob;
                           break;
                        }
                    }


                    Job gevendeJob=null;
                    // doorzoek eerst alle nodes van andere cluster op zoek naar nodige machinetype => verplaats die node naar huidige cluster
                    for (Job clusterJob : clusters.get(problem.depots.get(edge.getTo())).getClusterJobs()) {
                        for (Machine toCollectItem : clusterJob.getToCollectItems()) {
                            if(toCollectItem.getType().equals(nodigeMachineType.getKey())){
                                gevendeJob=clusterJob;
                                break;
                            }
                        }
                    }

                    // als geen nodes gevonden, voeg behoevende node toe aan andere cluster
                    if(gevendeJob==null){
                        this.getClusterJobs().remove(behoevendeJob);
                        clusters.get(problem.depots.get(edge.getTo())).getClusterJobs().add(behoevendeJob);
                    }
                    else{
                        this.getClusterJobs().add(gevendeJob);
                        clusters.get(problem.depots.get(edge.getTo())).getClusterJobs().remove(gevendeJob);
                    }
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

    public LinkedList<Edge> getSortedEdgesToOtherDepots() {
        return sortedEdgesToOtherDepots;
    }

    public void setSortedEdgesToOtherDepots(LinkedList<Edge> sortedEdgesToOtherDepots) {
        this.sortedEdgesToOtherDepots = sortedEdgesToOtherDepots;
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

    public Depot getMainDepot() {
        return depot;
    }

    public void setMainDepot(Depot depot) {
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

    public void addEdgesToOtherClusters(HashMap<Depot, Cluster> clusters) {
        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {
            if(entry.getValue().getId()!=this.getId()){

                // vraag de edge op van het huidige depot naar het depot van de andere cluster en sorteer
                sortedEdgesToOtherDepots.add(depot.getLocation().getEdgeMap().get(entry.getKey().getLocation()));
                sortedEdgesToOtherDepots.sort(Comparator.comparing(Edge::getDistance));



            }
        }
    }

    public void reset() {

        beschikbaar.clear();
        afTeLeveren.clear();
        nodigeMachineTypes.clear();
        overbodigeMachineTypes.clear();
        genoegMachineTypes.clear();
    }

    public void setupBeschikbaarAfTeLeveren(Problem problem) {
        for (MachineType machineType : problem.machineTypes) {
            this.getBeschikbaar().put(machineType,0);
            this.getAfTeLeveren().put(machineType, 0);
        }
        // machine types beschikbaar in depot
        for(Map.Entry<MachineType, LinkedList<Machine>> entry1: depot.getMachines().entrySet()){
            for (int i = 0; i < entry1.getValue().size(); i++) {
                this.getBeschikbaar().putIfAbsent(entry1.getKey(), 0);
                this.getBeschikbaar().replace(entry1.getKey(), this.getBeschikbaar().get(entry1.getKey())+1);
            }
        }

        // machine types beschikbaar op locaties en af te leveren op locatie
        for (Job job : this.getClusterJobs()) {
            for (Machine toCollectItem : job.getToCollectItems()) {
                this.getBeschikbaar().putIfAbsent(toCollectItem.getType(), 0);
                this.getBeschikbaar().replace(toCollectItem.getType(), this.getBeschikbaar().get(toCollectItem.getType())+1);
            }

            for (MachineType toDropItem : job.getToDropItems()) {
                this.getAfTeLeveren().putIfAbsent(toDropItem, 0);
                this.getAfTeLeveren().replace(toDropItem, this.getAfTeLeveren().get(toDropItem)+1);
            }

        }
    }
}
