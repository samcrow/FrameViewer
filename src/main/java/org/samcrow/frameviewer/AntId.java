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
        Out ("Forager"),
        /**
         * Ants going in
         */
        Down ("Control"),
        
        NestMaintenance,
        
        Stays,
        
        Meanders,
        
        /**
         * Something else
         */
        Unknown,
        ;
        /**
         * The name of this type, declared in 1.2.1 beta 1,
         * or null if none exists
         */
        private String legacyName = null;
        
        private Type(String legacyName) {
            this.legacyName = legacyName;
        }
        private Type() {
            
        }
        public String getLegacyName() {
            return legacyName;
        }
        
        /**
         * Returns the type enumeration for the given name. If no type for
         * the name exists, searches for a type with a legacy name that
         * matches the given name.
         * @see Enum#valueOf(java.lang.Class, java.lang.String) 
         * @param name The name
         * @return A modern type corresponding to the name
         * @throws IllegalArgumentException If the given name does not correspond
         * to an enumeration value
         */
        public static Type valueOfWithLegacySupport(String name) {
            try {
                return valueOf(name);
            }
            catch (IllegalArgumentException ex) {
                //No value for the name
                //Search for a legacy value
                for(Type type : values()) {
                    if(type.getLegacyName().equals(name)) {
                        return type;
                    }
                }
                throw new IllegalArgumentException("No Type value for name "+name);
            }
        }
    }
}
