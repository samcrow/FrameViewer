package org.samcrow.frameviewer;

/**
 * Stores and ant's ID number and type (in or out)
 * @author Sam Crow
 */
public class AntId {
    
    private final int id;
    
    private final Type type;

    public AntId(int id, Type type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }
    
    public static enum Type {
        /**
         * Ants going out
         */
        Forager,
        /**
         * Ants going in
         */
        Control,
        /**
         * Something else
         */
        Unknown,
    }
}
