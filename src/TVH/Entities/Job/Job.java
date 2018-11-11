package TVH.Entities.Job;

import TVH.Entities.Machine.MachineType;
import TVH.Entities.Node.Location;

public interface Job {

    int getRemoteFactor();

    Location getFixedLocation();

    boolean notDone();

    MachineType getMachineType();
}
