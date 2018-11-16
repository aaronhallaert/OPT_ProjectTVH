package TVH.Entities.Node;

import TVH.Entities.Machine.Machine;
import TVH.Entities.Machine.MachineType;

import java.util.List;

/**
 * Algemene interface van een Node die geïmplementeerd wordt door Client en Depot
 */

public interface Node {


    public boolean hasMachineAvailableOfType(MachineType mt);

    public List<Machine> getAvailableMachines();

    public boolean canPutMachineType(MachineType mt);

    public void putMachine(Machine m);

    public void undoPutMachine(Machine m);

    public void takeMachine(Machine m);

    public void undoTakeMachine(Machine m);

    public Machine viewMachineOfType(MachineType mt);

    public Location getLocation();
}
