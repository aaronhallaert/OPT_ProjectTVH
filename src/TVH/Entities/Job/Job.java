package TVH.Entities.Job;

import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Algemene interface van job die geïmplementeerd wordt door CollectJob en DropJob
 */

public interface Job {

    /**
     * Check als een Job al voltooid is of niet.
     * @return true als de job nog niet gedaan is.
     */
    boolean notDone();

    /**
     * Deze methode genereert elke move die mogelijk is om een Job te voltooien.
     * 1 move uitvoern uit deze lijst voltooit de job.
     * @return lijst met mogelijke moves
     */
    ArrayList<Move> generatePossibleMoves();

    /**
     * De remotefactor dient enkel om te sorteren. Hoe hoger de remotefactor hoe meer afgelegen een Job ligt.
     * De remotefactor komt overeen met de afstand van de fixedlocation naar alle andere nodes
     * @return remotefactor
     */
    int getRemoteFactor();

    MachineType getMachineType();

    Location getFixedLocation();

    //public List<Move> getAllMoves();



}
