package aether.model.memory;

import java.io.*;
import java.util.*;

public class CellStore {
    private Map<Integer, NodeMemory> nodeMemoryMap;
    private int cellSize;
    private int totalCapacity;
    private FluxCache fluxCache;

    public CellStore(String configFilePath) {
        nodeMemoryMap = new HashMap<>();
        this.cellSize = loadCellSize(configFilePath);
        this.totalCapacity = loadTotalCapacity(configFilePath);
        this.fluxCache = new FluxCache(totalCapacity / cellSize);
    }

    public int loadCellSize(String configFilePath) {
        int defaultSize = 4096;
        File configFile = new File(configFilePath);
        if (!configFile.exists())
            return defaultSize;

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("PageSize") || line.startsWith("CellSize")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        return Integer.parseInt(parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading config. Defaulting.");
        }
        return defaultSize;
    }

    private int loadTotalCapacity(String configFilePath) {
        int defaultCapacity = 1024;
        File configFile = new File(configFilePath);
        if (!configFile.exists())
            return defaultCapacity;

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("TotalMemory") || line.startsWith("TotalCapacity")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        return Integer.parseInt(parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading config. Defaulting.");
        }
        return defaultCapacity;
    }

    public void allocateMemoryToProcess(int nodeID) {
        NodeMemory nodeMem = new NodeMemory(nodeID, cellSize);
        nodeMemoryMap.put(nodeID, nodeMem);

        for (CellUnit unit : nodeMem.getUnits()) {
            fluxCache.accessPage(unit.unitID);
        }
    }

    public int getPageSize() {
        return cellSize;
    }
}
