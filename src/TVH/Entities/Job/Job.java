package TVH.Entities.Job;

import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Location;

import java.util.ArrayList;
import java.util.List;

public interface Job {

    int getRemoteFactor();

    Location getFixedLocation();

    boolean notDone();

    MachineType getMachineType();

    ArrayList<Move> generatePossibleMoves();

}
