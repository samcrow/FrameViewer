package org.samcrow.frameviewer.io3;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.samcrow.frameviewer.MarkerGraphic;

/**
 * A marker that provides information about an ant's interaction with another
 * <p>
 * @author samcrow
 */
public class InteractionMarker extends Marker {

    /**
     * Types of interactions, and the ants that participated in them
     */
    public enum InteractionType {
        /**
         * Focal ant started interaction, met ant did not participate
         */
        Performed,
        /**
         * Met ant started interaction, focal ant did not participate
         */
        Received,
        /**
         * Both ants participated in interaction
         */
        TwoWay("2-Way"),
        
        Unknown,
        ;
        
        private String shortName = null;
        private InteractionType() {}
        private InteractionType(String shortName) {
            this.shortName = shortName;
        }
        @Override
        public String toString() {
            if(shortName == null) {
                return super.toString();
            }
            else {
                return shortName;
            }
        }
    }
    
    /**
     * The activity of the ant that was met
     */
    private AntActivity metAntActivity;
    /**
     * The location of the ant that was met
     */
    private AntLocation metAntLocation;
    
    /**
     * The ID number of the ant that was met
     */
    private int metAntId;
    
    private InteractionType type = InteractionType.Unknown;


    
    public InteractionMarker(int x, int y, AntActivity focusAntActivity, AntLocation focusAntLocation, AntActivity metAntActivity, AntLocation metAntLocation) {
        super(x, y, focusAntActivity, focusAntLocation);
        
        this.metAntActivity = metAntActivity;
        this.metAntLocation = metAntLocation;
        
        // Set up an alternate graphic
        graphic = MarkerGraphic.Triangle;
        color = Color.YELLOW;
    }
    public InteractionMarker(Point2D point, AntActivity focusAntActivity, AntLocation focusAntLocation, AntActivity metAntActivity, AntLocation metAntLocation) {
        this( (int) Math.round(point.getX()), (int) Math.round(point.getY()), focusAntActivity, focusAntLocation, metAntActivity, metAntLocation);
    }
    
    
    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public AntActivity getMetAntActivity() {
        return metAntActivity;
    }

    public AntLocation getMetAntLocation() {
        return metAntLocation;
    }

    /**
     * 
     * @return The ID number of the ant that was met
     */
    public int getMetAntId() {
        return metAntId;
    }

    /**
     * Sets the ID of the ant that was met
     * @param metAntId 
     */
    public void setMetAntId(int metAntId) {
        this.metAntId = metAntId;
    }
    
    /**
     * Copies all the attributes of another marker
     * to this marker.
     * 
     * Warning: This method is not safe when working with subclasses.
     * If this method is called on an object that is actually
     * a subclass of InteractionMarker, the subclass's set() method will not be called
     * and its subclass properties will not be set.
     * 
     * @param other 
     */
    public void set(InteractionMarker other) {
        super.set(other);
        type = other.type;
        metAntActivity = other.metAntActivity;
        metAntLocation = other.metAntLocation;
    }

    @Override
    public String toCSVLine() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append(antId);
        buffer.append(',');
        buffer.append(frame);
        buffer.append(',');
        buffer.append(x);
        buffer.append(',');
        buffer.append(y);
        buffer.append(',');
        buffer.append(focusAntActivity.toString());
        buffer.append(',');
        buffer.append(focusAntLocation.toString());
       
        buffer.append(',');
        buffer.append(type.name()); // use name(), not toString(), because toString() is overriden
        buffer.append(',');
        buffer.append(metAntActivity.toString());
        buffer.append(',');
        buffer.append(metAntLocation.toString());
        buffer.append(',');
        buffer.append(metAntId);
        
        return buffer.toString();
    }
}
