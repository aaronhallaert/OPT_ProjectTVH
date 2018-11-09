package TVH.Entities.Node;

import TVH.Entities.Machine;
import TVH.Entities.MachineType;

import java.util.List;

public interface Node {

    public boolean hasMachineAvailableOfType(MachineType mt);
    public List<Machine> getAvailableMachines();
    public boolean canPutMachine(Machine m);
    public void putMachine(Machine m);
    public void takeMachine(Machine m);
    public Machine takeMachine(MachineType mt);
    public Location getLocation();
}
