package TVH.Entities.Job;

import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Location;

import java.util.ArrayList;
import java.util.List;

//Job is een collect of een store die moet gebeuren
//gemeenschappelijke methoden die ervoor zorgen dat een dropjob en een collectjob in dezelfde lijst kunnen
public interface Job {

    int getRemoteFactor();

    Location getFixedLocation();

    boolean notDone();

    MachineType getMachineType();

    ArrayList<Move> generatePossibleMoves();

}
