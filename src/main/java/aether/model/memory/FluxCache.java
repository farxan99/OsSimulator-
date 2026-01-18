package aether.model.memory;

import java.util.*;

public class FluxCache {
    private int capacity;
    private LinkedHashMap<Integer, Integer> cache;

    public FluxCache(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public void accessPage(int key) {
        if (cache.containsKey(key)) {
            System.out.println("Flux Hit: Block " + key);
        } else {
            if (cache.size() >= capacity && capacity > 0) {
                Integer lru = cache.keySet().iterator().next();
                cache.remove(lru);
                System.out.println("Flux Shift: Expelled " + lru + " for " + key);
            }
            cache.put(key, key);
            System.out.println("Flux Load: Block " + key);
        }
    }
}
