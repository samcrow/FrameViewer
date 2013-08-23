package org.samcrow.frameviewer;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
    Tracking ("Tracking", MarkerGraphic.Circle, Color.RED, MouseButton.PRIMARY),
    
    Unsure ("Unsure", MarkerGraphic.Square, Color.WHITE, new KeyCodeCombination(KeyCode.U)),
    
    Returner ("Returner", MarkerGraphic.FilledDiagonalSquare, Color.LIGHTCYAN, MouseButton.SECONDARY),
    
    Window ("Window", MarkerGraphic.PlusSign, Color.MAGENTA, new KeyCodeCombination(KeyCode.W)),
    
    Nest ("Nest", MarkerGraphic.X, Color.YELLOW, new KeyCodeCombination(KeyCode.N)),
    
    Leaving ("Leaving", MarkerGraphic.DiagonalSquare, Color.GOLD, new KeyCodeCombination(KeyCode.L)),
    
    Standing ("Standing ant", MarkerGraphic.Triangle, Color.LIMEGREEN, new KeyCodeCombination(KeyCode.S)),
    ;
    
    /**
     * This marker's type name. This is used only for user interface elements.
     */
    private final String markerTypeName;
    
    /**
     * The graphic used to draw this marker.
     */
    private final MarkerGraphic graphic;
    
    /**
     * The color in which this marker should be drawn.
     */
    private final Color color;
    
    /**
     * The key combination that can be used to create this marker, or null
     * if this type cannot be created by a key
     */
    private final KeyCombination key;
    /**
     * The mouse button that can be used to create this marker, or null
     * if this type cannot be created by a mouse event
     */
    private final MouseButton mouseButton;

    private MarkerType(String markerTypeName, MarkerGraphic graphic, Color color) {
        this.markerTypeName = markerTypeName;
        this.graphic = graphic;
        this.color = color;
        key = null;
        mouseButton = null;
    }

    private MarkerType(String markerTypeName, MarkerGraphic graphic, Color color, KeyCombination key) {
        this.markerTypeName = markerTypeName;
        this.graphic = graphic;
        this.color = color;
        this.key = key;
        mouseButton = null;
    }

    private MarkerType(String markerTypeName, MarkerGraphic graphic, Color color, MouseButton mouseButton) {
        this.markerTypeName = markerTypeName;
        this.graphic = graphic;
        this.color = color;
        key = null;
        this.mouseButton = mouseButton;
    }
    
    

    public String getMarkerTypeName() {
        return markerTypeName;
    }

    public KeyCombination getKey() {
        return key;
    }

    public MouseButton getMouseButton() {
        return mouseButton;
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
    
    //Builders
    
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
    
    /**
     * Returns the event type for the given mouse event, or null if none is applicable
     * @param event
     * @return 
     */
    public static MarkerType getTypeForMouseEvent(MouseEvent event) {
        for(MarkerType type : values()) {
            if(type.getMouseButton().equals(event.getButton())) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for mouse event "+event);
    }
    
    /**
     * Returns the event type for the given keyboard event, or null if none is applicable.
     * @param event
     * @return 
     */
    public static MarkerType getTypeForKeyboardEvent(KeyEvent event) {
        for(MarkerType type : values()) {
            if(type.getKey().match(event)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for keyboard event "+event);
    }
}
