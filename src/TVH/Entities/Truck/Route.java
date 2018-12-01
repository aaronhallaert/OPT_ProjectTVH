package TVH.Entities.Truck;

import TVH.Config;
import TVH.Entities.Job.Move;
import TVH.Entities.Machine.Machine;
import TVH.Entities.Node.Location;
import TVH.Problem;

import java.util.*;

/**
 * De klasse Route geeft aan langs waar een truck rijdt.
 * Moves kunnen toegevoegd en verwijderd worden uit de Route.
 */

public class Route {

    public static int TIME_FACTOR = 0;
    public static int ORDER_FACTOR = 0;
    public static int FILL_RATE_VIOLATIONS_FACTOR = 0;
    public static int DISTANCE_FACTOR = 0;

    //Dit bespaart redelijk wat tijd (anders moet telkens problem object worden opgevraagd)
    public static int TRUCK_CAPACITY;
    public static int TRUCK_WORKING_TIME;
    public static int[][] distanceMatrix;
    public static int[][] timeMatrix;

    private ArrayList<Stop> stops;
    private int totalDistance = 0;
    private int cost = 0;
    private int timeViolations = 0;
    private int fillRateViolations = 0;
    private int totalTime = 0;
    private boolean changed;


    public Route(Stop first, Stop last, Truck truck) {
        stops = new ArrayList<>();
        stops.add(first);
        stops.add(last);
        changed = true;
    }

    public Route(ArrayList<Stop> stops) {
        this.stops = stops;
        changed = true;
    }

    public Route(Route r, boolean DeepCopyStops) {
        if (DeepCopyStops) {
            this.stops = new ArrayList<>();
            for (Stop s : r.stops) {
                stops.add(new Stop(s));
            }
        } else {
            this.stops = new ArrayList<>(r.stops);
        }
        this.totalDistance = r.totalDistance;
        this.cost = r.cost;
        this.timeViolations = r.timeViolations;
        this.fillRateViolations = r.fillRateViolations;
        this.totalTime = r.totalTime;
        this.changed = r.changed;
    }

    public void loadRoute(Route r) {
        this.stops = r.stops;
        this.totalDistance = r.totalDistance;
        this.cost = r.cost;
        this.timeViolations = r.timeViolations;
        this.fillRateViolations = r.fillRateViolations;
        this.totalTime = r.totalTime;
        this.changed = r.changed;
    }

    /**
     * Deze methode probeert een bepaalde Move toe te voegen aan de route.
     *
     * @param m Move die moet toegevoegd worden.
     * @return true als de move succesvol is toegevoegd (zonder constraints te breken)
     */
    public boolean addMove(Move m) {

        //Backups nemen van stops en locationstopmap
        Route backup = new Route(this, false);

        //2 nieuwe stops bijmaken
        Stop collectStop = new Stop(m.getCollect());
        Stop dropStop = new Stop(m.getDrop());
        collectStop.addToCollect(m.getMachine());
        dropStop.addToDrop(m.getMachine());

        //Stops inserten op beste plek
        int[] indices = insertStops(collectStop, dropStop);

        //Feasiblity checken
        if (!isFeasible()) {
            //Originele route herstellen
            removeMove(m);
            loadRoute(backup);

            return false;
        }

        //Sequentiële stops met zelfde locatie mergen met elkaar
        mergeStops(indices);

        return true;
    }

    /**
     * Deze methode verwijdert een move terug van de route
     *
     * @param m Move die verwijderd moet worden
     */

    public void removeMove(Move m) {
        Stop collectStop = null;
        Stop dropStop = null;

        int collectIndex = -1;
        int dropIndex = -1;

        //Respectieve stops waar de move effect op had opzoeken en de machine verwijderen van collect en drop
        for (int i = 0; i < stops.size(); i++) {
            Stop s = stops.get(i);
            if (s.getLocation() == m.getCollect()) {
                if (s.removeFromCollect(m.getMachine())) {
                    collectStop = s;
                    collectIndex = i;
                }

            }
            if (s.getLocation() == m.getDrop()) {
                if (s.removeFromDrop(m.getMachine())) {
                    dropStop = s;
                    dropIndex = i;
                }
            }
        }
        //Als we op een bepaalde locatie (behalve start en eind) passeren zonder iets te droppen of collecten kan deze
        //Stop worden verwijderd.
        if (collectStop.isEmpty() && collectStop != stops.get(0) && collectStop != stops.get(stops.size() - 1)) {
            stops.remove(collectStop);
        }
        if (dropStop.isEmpty() && dropStop != stops.get(0) && dropStop != stops.get(stops.size() - 1)) {
            stops.remove(dropStop);
        }

        //TODO: opnieuw mergen rond de verwijderde indices

        changed = true;
    }

    /**
     * Deze methode insert 2 nieuwe stops op de beste locatie mogelijk
     *
     * @param stop1
     * @param stop2
     * @return de 2 indices waar de nieuwe stops zich bevinden
     */
    private int[] insertStops(Stop stop1, Stop stop2) {
        int stop1Index = -1;
        int stop2Index = -1;

        //Aangezien we verder op met delta kost gaan werken is het belangrijk om te zorgen dat de kost nu correct is
        if (changed) {
            calculateCost();
        }

        //Fillrates cachen
        int[] fillRateArray = new int[stops.size()];
        fillRateArray[0] = stops.get(0).getDeltaFillRate();
        for (int i = 1; i < stops.size(); i++) {
            fillRateArray[i] = fillRateArray[i-1] + stops.get(i).getDeltaFillRate();
        }


        int minCost = Integer.MAX_VALUE;
        Route best = null;
        int minFillRateViolations = Integer.MAX_VALUE;

        for (int i = 1; i < getStops().size(); i++) {
            for (int j = 1; j < getStops().size(); j++) {
                if (i <= j) {
                    //Kopie van de route nemen zonder stops de deepcopien
                    Route candidate = new Route(this, false);
                    candidate.getStops().add(i, stop1);
                    candidate.getStops().add(j + 1, stop2);
                    candidate.setChanged(true);
                    if (candidate.calculateCost(i, j + 1, fillRateArray, minFillRateViolations) < minCost) {
                        best = candidate;
                        minCost = candidate.getCost();
                        minFillRateViolations = candidate.getFillRateViolations();
                        stop1Index = i;
                        stop2Index = j + 1;
                    }
                }
            }
        }
        //beste gevonden route inladen
        loadRoute(best);

        return new int[]{stop1Index, stop2Index};
    }

    /**
     * Deze methode plakt sequentiele stops tesamen die dezelfde locatie hebben
     *
     * @param indices indices van de net toegevoegde stops
     */
    private void mergeStops(int[] indices) {
        Stop stop1 = stops.get(indices[0]);
        Stop stop2 = stops.get(indices[1]);

        //Als de 2 toegevoegde stops niet op dezelfde locatie liggen
        if(stop1.getLocation() != stop2.getLocation()) {
            boolean stop1merged = false;
            boolean stop2merged = false;
            //Normaal zijn de nieuwe stops altijd toegevoegd voor de oude (behalve bij de eerste)
            if (stop1.getLocation() == stops.get(indices[0] + 1).getLocation()) {
                stops.get(indices[0] + 1).merge(stop1);
                stop1merged = true;
            }
            //Speciaal voor 1ste stop in de route
            else if (stop1.getLocation() == stops.get(indices[0] - 1).getLocation()) {
                stops.get(indices[0] - 1).merge(stop1);
                stop1merged = true;
            }
            if (stop2.getLocation() == stops.get(indices[1] + 1).getLocation()) {
                stops.get(indices[1] + 1).merge(stop2);
                stop2merged = true;
            }

            if (stop1merged && stop2merged) {
                stops.remove(indices[0]);
                stops.remove(indices[1] - 1);
            } else if (stop1merged) stops.remove(indices[0]);
            else if (stop2merged) stops.remove(indices[1]);
        }
        //Als de 2 toegevoegde stops wel op dezelfde locatie liggen
        else{
            stop1.merge(stop2);
            stops.remove(indices[1]);
        }
    }

    /**
     * Deze methode checkt de feasibility van de route
     *
     * @return true als de route feasible is
     */
    public boolean isFeasible() {
        if (changed) calculateCost();
        return timeViolations == 0 && fillRateViolations == 0;
    }

    /**
     * Berekent de kost volledig opnieuw indienen de Route gewijzigd is.
     * @return kost
     */
    private int calculateCost() {
        if (changed) {

            totalDistance = 0;
            timeViolations = 0;
            fillRateViolations = 0;

            totalTime = stops.get(0).getTimeSpend();
            int fillrate = stops.get(0).getDeltaFillRate();
            if (fillrate > TRUCK_CAPACITY) {
                fillRateViolations += fillrate - TRUCK_CAPACITY;
            }

            for (int i = 1; i < stops.size(); i++) {
                Stop selected = stops.get(i);
                //Total distance
                Location A = selected.getLocation();
                Location B = stops.get(i - 1).getLocation();
                totalDistance += A.distanceTo(B);

                //Total time
                totalTime += A.timeTo(B);
                totalTime += selected.getTimeSpend();

                //FillRate violations
                fillrate += selected.getDeltaFillRate();
                if (fillrate > TRUCK_CAPACITY) {
                    fillRateViolations += fillrate - TRUCK_CAPACITY;
                }
            }

            //Time violations
            timeViolations = (totalTime > TRUCK_WORKING_TIME) ? totalTime - TRUCK_WORKING_TIME : 0;

            cost = DISTANCE_FACTOR * totalDistance
                    + TIME_FACTOR * timeViolations
                    + FILL_RATE_VIOLATIONS_FACTOR * fillRateViolations;
            changed = false;

        }
        return cost;
    }

    /**
     * Berekent de kost (delta) bij toevoegen van 2 nieuwe stops. Sneller dan algemene kost berekening.
     * @param firstAddedIndex index van de eerste nieuwe stop
     * @param secondAddedIndex index van de tweede nieuwe stop
     * @return nieuwe kost
     */
    private int calculateCost(int firstAddedIndex, int secondAddedIndex, int[] fillrateArray, int minFillRateViolations) {
        if (changed) {
            if (firstAddedIndex < secondAddedIndex - 1) {
                //A1 - B1 is nu  A1 - X1 - B1 geworden
                int A1 = stops.get(firstAddedIndex - 1).getLocation().getLocationID();
                int X1 = stops.get(firstAddedIndex).getLocation().getLocationID();
                int B1 = stops.get(firstAddedIndex + 1).getLocation().getLocationID();

                //A2 - B2 is nu  A2 - X2 - B2 geworden
                int A2 = stops.get(secondAddedIndex - 1).getLocation().getLocationID();
                int X2 = stops.get(secondAddedIndex).getLocation().getLocationID();
                int B2 = stops.get(secondAddedIndex + 1).getLocation().getLocationID();

                totalDistance -= distanceMatrix[A1][B1];
                totalDistance += distanceMatrix[A1][X1]  + distanceMatrix[X1][B1];

                totalDistance -= distanceMatrix[A2][B2];
                totalDistance += distanceMatrix[A2][X2] +distanceMatrix[X2][B2];

                totalTime -= timeMatrix[A1][B1];
                totalTime += timeMatrix[A1][X1] + timeMatrix[X1][B1];

                totalTime -= timeMatrix[A2][B2];
                totalTime += timeMatrix[A2][X2] + timeMatrix[X2][B2];

                totalTime += stops.get(firstAddedIndex).getTimeSpend() + stops.get(secondAddedIndex).getTimeSpend();
            } else {
                //A - B is nu A - X - Y - B geworden
                int A = stops.get(firstAddedIndex - 1).getLocation().getLocationID();
                int X = stops.get(firstAddedIndex).getLocation().getLocationID();
                int Y = stops.get(secondAddedIndex).getLocation().getLocationID();
                int B = stops.get(secondAddedIndex + 1).getLocation().getLocationID();

                totalDistance -= distanceMatrix[A][B];
                totalDistance += distanceMatrix[A][X] + distanceMatrix[X][Y] + distanceMatrix[Y][B];

                totalTime -= timeMatrix[A][B];
                totalTime += timeMatrix[A][X] + timeMatrix[X][Y] + timeMatrix[Y][B];

                totalTime += stops.get(firstAddedIndex).getTimeSpend() + stops.get(secondAddedIndex).getTimeSpend();

            }

            timeViolations = (totalTime > TRUCK_WORKING_TIME) ? totalTime - TRUCK_WORKING_TIME : 0;

            //Fillrate violations worden opnieuw berekend vanaf de firstAddedIndex, en vanaf we meer uitkomen dan het minimum, stoppen we met rekenen
            fillRateViolations = 0;
            int fillrate = fillrateArray[firstAddedIndex-1];
            for(int i = firstAddedIndex; i < fillrateArray.length; i++){
                fillrate += stops.get(i).getDeltaFillRate();
                if (fillrate > TRUCK_CAPACITY) {
                    fillRateViolations += fillrate - TRUCK_CAPACITY;
                    if(fillRateViolations > minFillRateViolations){
                        cost = Integer.MAX_VALUE;
                        return cost;
                    }
                }
            }

            cost = DISTANCE_FACTOR * totalDistance
                    + TIME_FACTOR * timeViolations
                    + FILL_RATE_VIOLATIONS_FACTOR * fillRateViolations;

            changed = false;
        }
        return cost;

    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        changed = true;
        this.stops = stops;
    }

    public int getTotalDistance() {
        if(changed) calculateCost();
        return totalDistance;
    }

    public int getCost() {
        if(changed) calculateCost();
        return calculateCost();
    }

    public int getTimeViolations() {
        if(changed) calculateCost();
        return timeViolations;
    }

    public int getFillRateViolations() {
        if(changed) calculateCost();
        return fillRateViolations;
    }

    public int getTotalTime() {
        if(changed) calculateCost();
        return totalTime;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
