package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Stores a marker that can be displayed on a video frame.
 * 
 * Instances of this class can be created using {@link MarkerType#buildMarker(int, int)}
 * or {@link MarkerType#buildMarker(javafx.geometry.Point2D) }.
 * 
 * @author Sam Crow
 */
public class Marker extends FrameObject {
    
    /**
     * The X position, in frame coordinates, of this marker
     */
    private final int x;
    
    /**
     * The Y position, in frame coordinates, of this marker
     */
    private final int y;
    
    /**
     * The color in which this marker should be displayed
     */
    private final Color color;
    
    private final MarkerType type;
    

    Marker(int x, int y, Color color, MarkerType type) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.type = type;
    }
    
    Marker(Point2D point, Color color, MarkerType type) {
        this( (int) Math.round(point.getX()), (int) Math.round(point.getY()), color, type);
    }
    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
    
    public String getTypeName() {
        return type.getMarkerTypeName();
    }
    
    public MarkerType getType() {
        return type;
    }
}
