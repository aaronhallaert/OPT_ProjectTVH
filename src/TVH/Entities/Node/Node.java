package TVH.Entities.Node;

import TVH.Entities.Machine;
import TVH.Entities.MachineType;

import java.util.List;

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
