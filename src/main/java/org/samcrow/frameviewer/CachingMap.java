package org.samcrow.frameviewer;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A map with a configurable maximum capacity that removes old items when this
 * capacity is exceeded. The item that was accessed the least recently is the
 * first item to be removed.
 * @param <K> The key type
 * @param <V> The value type
 * @author Sam Crow
 */
public class CachingMap <K, V> extends LinkedHashMap <K, V> {
    
    /**
     * The maximum capacity of this map
     */
    private final int capacity;
    
    /**
     * The default maximum capacity
     */
    private static final int DEFAULT_CAPACITY = 100;
    
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    private static final int DEFAULT_INITIAL_CAPACITY = 100;

    /**
     * Constructor
     * @param initialCapacity The initial capacity of this map. The map can expand
     * beyond this capacity.
     * @param capacity The capacity of this map. If the map exceeds this capacity,
     * old items will be removed.
     */
    public CachingMap(int initialCapacity, int capacity) {
        super(initialCapacity, 0.75f, true);
        this.capacity = capacity;
    }
    
    /**
     * Constructor
     * @param capacity The capacity of this map. If the map exceeds this capacity,
     * old items will be removed.
     */
    public CachingMap(int capacity) {
        super(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, true);
        this.capacity = capacity;
    }
    
    /**
     * Constructor. The capacity will be initialized to the default value of 100.
     */
    public CachingMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Returns true to clear the oldest entry when the current size exceeds
     * the capacity
     * @param eldest
     * @return 
     */
    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        boolean remove = size() > capacity;
        if(remove) {
            Logger.getLogger(CachingMap.class.getName()).log(Level.INFO, "Removing cache entry with key {0}", eldest.getKey());
        }
        return remove;
    }
    
    
    
}
