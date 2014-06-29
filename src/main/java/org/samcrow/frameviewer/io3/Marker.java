package org.samcrow.frameviewer.io3;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.samcrow.frameviewer.FrameObject;
import org.samcrow.frameviewer.MarkerGraphic;

/**
 * Represents a marker. A marker exists at a certain location on a certain
 * frame, and
 * <p>
 * @author samcrow
 */
public class Marker extends FrameObject {

    /**
     * The X position, in frame coordinates, of this marker
     */
    protected int x;

    /**
     * The Y position, in frame coordinates, of this marker
     */
    protected int y;

    /**
     * The color in which this marker should be displayed
     */
    protected Color color;

    /**
     * The graphic used to represent this marker
     */
    protected MarkerGraphic graphic;

    /**
     * The ID of the ant that this marker is tracking (the focal ant)
     */
    protected int antId;

    protected AntActivity focusAntActivity;

    protected AntLocation focusAntLocation;

    public Marker(int x, int y, AntActivity focusAntActivity, AntLocation focusAntLocation) {
        this(x, y);
        this.focusAntActivity = focusAntActivity;
        this.focusAntLocation = focusAntLocation;
    }

    public Marker(Point2D point, AntActivity focusAntActivity, AntLocation focusAntLocation) {
        this(point);
        this.focusAntActivity = focusAntActivity;
        this.focusAntLocation = focusAntLocation;
    }

    /**
     * Base constructor. All other constructors go here.
     * 
     * Sets up the correct graphic and color.
     * 
     * @param x
     * @param y 
     */
    private Marker(int x, int y) {
        this.x = x;
        this.y = y;

        graphic = MarkerGraphic.Circle;
        color = Color.RED;
    }

    private Marker(Point2D point) {
        this((int) Math.round(point.getX()), (int) Math.round(point.getY()));
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

    public int getAntId() {
        return antId;
    }

    public void setAntId(int antId) {
        this.antId = antId;
    }

    public void paint(GraphicsContext gc, double canvasX, double canvasY) {
        gc.save();
        graphic.paint(gc, color, canvasX, canvasY);
        //Add a label
        gc.restore();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeText("Ant " + getAntId(), canvasX + 6, canvasY + 4);
    }

    /**
     * Get the value of focusAntActivity
     *
     * @return the value of focusAntActivity
     */
    public AntActivity getFocusAntActivity() {
        return focusAntActivity;
    }

    /**
     * Set the value of focusAntActivity
     *
     * @param focusAntActivity new value of focusAntActivity
     */
    public void setFocusAntActivity(AntActivity focusAntActivity) {
        this.focusAntActivity = focusAntActivity;
    }

    public AntLocation getFocusAntLocation() {
        return focusAntLocation;
    }

    public void setFocusAntLocation(AntLocation focusAntLocation) {
        this.focusAntLocation = focusAntLocation;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public void setPosition(Point2D position) {
        x = (int) Math.round(position.getX());
        y = (int) Math.round(position.getY());
    }
    
    /**
     * Copies all the attributes of another marker
     * to this marker.
     * 
     * Warning: This method is not safe when working with subclasses.
     * If this method is called on an object that is actually
     * a subclass of Marker, the subclass's set() method will not be called
     * and its subclass properties will not be set.
     * 
     * @param other 
     */
    public void set(Marker other) {
        x = other.getX();
        y = other.getY();
        color = other.color;
        graphic = other.graphic;
        antId = other.antId;
        focusAntActivity = other.focusAntActivity;
        focusAntLocation = other.focusAntLocation;
    }

    // File-related things
    
    /**
     * 
     * @return A CSV header that describes the fields stored by this marker.
     * 
     * This static method provides the fields used both by Marker and its
     * subclass InteractionMarker.
     */
    public static String fileHeader() {
        return "Ant,Frame,X,Y,Focus Ant Activity,Focus Ant Location,Interaction Type,Ant Met Activity,Ant Met Location,Ant Met ID";
    }
    
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
        // Last four fields empty for the interaction type and tracked ant activity and location
        buffer.append(",,,,");
        
        return buffer.toString();
    }
}
