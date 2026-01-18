package aether.model.memory;

import java.util.*;

public class NodeMemory {
    private List<CellUnit> units;

    public NodeMemory(int nodeID, int unitSize) {
        this.units = new ArrayList<>();

        int defaultMagnitude = 4096;
        int numUnits = (int) Math.ceil((double) defaultMagnitude / unitSize);
        for (int i = 0; i < numUnits; i++) {
            units.add(new CellUnit(i));
        }
    }

    public List<CellUnit> getUnits() {
        return units;
    }
}
