package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

/**
 * Stores a marker that can be displayed on a video frame
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
    
    
    
    private static final Color DEFAULT_COLOR = Color.RED;

    public Marker(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
    
    public Marker(Point2D point, Color color) {
        x = (int) Math.round(point.getX());
        y = (int) Math.round(point.getY());
        this.color = color;
    }
    
    public Marker(Point2D point) {
        this(point, DEFAULT_COLOR);
    }
    
    public Marker(int x, int y) {
        this(x, y, DEFAULT_COLOR);
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
    
    
    
}
