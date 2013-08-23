package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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
    
    /**
     * The marker type that this marker has
     */
    private final MarkerType type;
    
    /**
     * The ID of the ant that this marker is tracking
     */
    private AntId antId = new AntId(0, AntId.Type.Unknown);
    

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

    public AntId getAntId() {
        return antId;
    }

    public void setAntId(AntId antId) {
        this.antId = antId;
    }
    
    public String getTypeName() {
        return type.getMarkerTypeName();
    }
    
    public MarkerType getType() {
        return type;
    }
    
    public void paint(GraphicsContext gc, double canvasX, double canvasY) {
        gc.save();
        type.paint(gc, canvasX, canvasY);
        //Add a label
        gc.restore();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeText("Ant "+getAntId().getId()+" "+getTypeName(), canvasX + 6, canvasY + 4 );
    }
}
