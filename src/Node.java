import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

public class Node {

    private int locatieId;
    private LinkedList<Machine> pickupItems;
    private LinkedList<Machine> dropOffItems;
    private HashMap<MachineType, Stack<Machine>>  machines;
    boolean depot;


}
