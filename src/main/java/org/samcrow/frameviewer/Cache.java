package org.samcrow.frameviewer;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Caches several values that can be referred to by their numerical indexes
 * @param <T> The type of image to store
 * @author Sam Crow
 */
public class Cache<T> {
    
    private final CacheSource<? extends T> source;
    
    private final ArrayList<SoftReference<T>> list;
    
    public Cache(int initialCapacity, CacheSource<? extends T> cache) {
        list = new ArrayList<>(initialCapacity);
        fillList(initialCapacity);
        this.source = cache;
    }
    
    public Cache(CacheSource<? extends T> cache) {
        this.source = cache;
        list = new ArrayList<>();
    }
    
    /**
     * Returns a value
     * @param index The index to get a value for
     * @return
     */
    public synchronized T get(int index) {
        load(index);
        SoftReference<T> ref = list.get(index);
        if(ref == null) {
            return null;
        }
        else {
            return ref.get();
        }
    }
    
    /**
     * Ensures that this cache has an entry for the given index
     * @param index The index to load
     * @throws IOException If an IO error occurred
     */
    public synchronized void load(int index) {
        fillList(index);
        try {
        SoftReference<T> ref = list.get(index);
        if(ref == null) {
            ref = new SoftReference<>(source.load(index));
            list.set(index, ref);
        }
        if(ref.get() == null) {
            ref = new SoftReference<>(source.load(index));
            list.set(index, ref);
        }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Sets the cached object for a given index
     * @param index The index to set the value for
     * @param value The value to set
     */
    public synchronized void set(int index, T value) {
        SoftReference<T> ref = new SoftReference<>(value);
        list.set(index, ref);
    }
    
    /**
     * An interface for something that can provide an image to add to the cache
     * @param <T2> The type of image
     */
    public interface CacheSource<T2> {
        
        /**
         * Loads and returns an object identified by the given index
         * @param index the 0-based index to return
         * @return the object
         * @throws IOException  
         */
        public T2 load(int index) throws IOException;
        
    }
    
    /**
     * Fills the list to ensure that the list has a value for the given index.
     * Null values will be inserted as necessary.
     * @param lastIndex The index to ensure a value for
     */
    private void fillList(int lastIndex) {
        while(list.size() < lastIndex + 1) {
            list.add(null);
        }
    }
}
