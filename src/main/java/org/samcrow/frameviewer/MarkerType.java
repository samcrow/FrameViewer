package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Enumerates types of markers that are available
 * @author Sam Crow
 */
public enum MarkerType {
    
    /**
     * Used for clicks from revision 1 files. Ant ID set to zero.
     */
    Tracking("Tracking", MarkerGraphic.Circle, Color.RED),
    
    Unsure ("Unsure", MarkerGraphic.Square, Color.WHITE),
    
    Returner("Returner", MarkerGraphic.FilledDiagonalSquare, Color.LIGHTCYAN),
    
    Window("Window", MarkerGraphic.PlusSign, Color.MAGENTA),
    
    Nest("Nest", MarkerGraphic.X, Color.YELLOW),
    
    Leaving("Leaving", MarkerGraphic.DiagonalSquare, Color.GOLD),
    
    Standing("Standing ant", MarkerGraphic.Triangle, Color.LIMEGREEN)
    ;
    
    /**
     * This marker's type name. This is used only for user interface elements.
     */
    private final String markerTypeName;
    
    private final MarkerGraphic graphic;
    
    /**
     * The color in which this marker should be drawn
     */
    private final Color color;

    private MarkerType(String markerTypeName, MarkerGraphic graphic, Color color) {
        this.markerTypeName = markerTypeName;
        this.graphic = graphic;
        this.color = color;
    }

    public String getMarkerTypeName() {
        return markerTypeName;
    }

    /**
     * Paints a symbol for this marker at the given location
     * @param gc The graphics context to draw to
     * @param centerX The X location in graphics context coordinates of the center position
     * @param centerY The Y location in graphics context coordinates of the center position
     */
    public void paint(GraphicsContext gc, double centerX, double centerY) {
        graphic.paint(gc, color, centerX, centerY);
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
        return Tracking;
    }
    
    public static MarkerType getTypeForKey(KeyCode key) {
        
        switch(key) {
            case U:
                return Unsure;
            case W:
                return Window;
            case N:
                return Nest;
            case L:
                return Leaving;
            case S:
                return Standing;
            default:
                throw new IllegalArgumentException("No defined marker type for key "+key);
        }
    }
    
    public static MarkerType getTypeForMouseEvent(MouseEvent event) {
        if(event.getButton().equals(MouseButton.SECONDARY)) {
            return Returner;
        }
        else {
            return Tracking;
        }
    }
}
