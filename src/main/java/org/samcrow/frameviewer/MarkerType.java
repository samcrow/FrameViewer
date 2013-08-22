package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Enumerates types of markers that are available
 * @author Sam Crow
 */
public enum MarkerType {
    
    Default ("Default", Color.RED),
    
    Test1 ("Test 1", Color.CYAN),
    
    Test2 ("Test 2", Color.LAVENDER),
    
    ;
    
    /**
     * This marker's type name. This is used only for user interface elements.
     */
    private final String markerTypeName;
    
    private final Color color;

    private MarkerType(String markerTypeName, Color color) {
        this.markerTypeName = markerTypeName;
        this.color = color;
    }

    public String getMarkerTypeName() {
        return markerTypeName;
    }

    public Color getColor() {
        return color;
    }
    
    /**
     * Creates and returns a marker at the specified point
     * @param x
     * @param y
     * @return 
     */
    public Marker buildMarker(int x, int y) {
        return new Marker(x, y, color, this);
    }
    
    /**
     * Creates and returns a marker of this type at the specified point
     * @param point
     * @return 
     */
    public Marker buildMarker(Point2D point) {
        return new Marker(point, color, this);
    }
    
    //Static accessors
    
    public static MarkerType getDefaultType() {
        return Default;
    }
}
