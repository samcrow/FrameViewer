package org.samcrow.frameviewer.ui;

import java.util.LinkedList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.samcrow.frameviewer.AntId;
import org.samcrow.frameviewer.Marker;
import org.samcrow.frameviewer.MarkerType;
import org.samcrow.frameviewer.PaintableCanvas;


/**
 * Displays a video frame and allows it to be clicked on
 * @author Sam Crow
 */
public class FrameCanvas extends PaintableCanvas {
    
    /**
     * The image to be displayed
     */
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    
    /**
     * The markers to display on this frame. This must not be null. For no markers
     * to be displayed, this should be set to an empty list.
     */
    private List<Marker> markers = new LinkedList<>();

    /**
     * Local coordinate X position of the frame's top left corner
     */
    private double imageTopLeftX;
    /**
     * Local coordinate Y position of the frame's top left corner
     */ 
    private double imageTopLeftY;
    /**
     * Local coordinate displayed width of the frame
     */
    private double imageWidth;
    /**
     * Local coordinate displayed width of the frame
     */
    private double imageHeight;
    
    /**
     * X location of the cursor in screen coordinates, when last moved
     */
    private double cursorScreenX;
    /**
     * Y location of the cursor in screen coordinates, when last moved
     */
    private double cursorScreenY;
    /**
     * X location of the cursor in canvas coordinates, when last moved
     */
    private double cursorCanvasX;
    /**
     * Y location of the cursor in canvas coordinates, when last moved
     */
    private double cursorCanvasY;
    
    public FrameCanvas() {
        
        setFocusTraversable(true);
        requestFocus();
        
        //Add a marker when clicked on
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                try {
                    Point2D markerPoint = getFrameLocation(event);
                    
                    //Ask the user for a marker type
                    AntIdDialog dialog = new AntIdDialog(getScene().getWindow());
                    //Move the dialog to the position of the cursor
                    dialog.setX(event.getScreenX());
                    dialog.setY(event.getScreenY());
                    AntId antId = dialog.showAndGetId();
                    MarkerType type = MarkerType.getTypeForMouseEvent(event);
                    
                    if(antId == null) {
                        //Do nothing
                        return;
                    }
                    
                    Marker marker = type.buildMarker(markerPoint);
                    marker.setAntId(antId);
                    getMarkers().add(marker);
                    repaint();
                    
                    event.consume();
                }
                catch (NotInFrameException ex) {
                    //Ignore this click
                }
                
            }
        });
        
        //Update cursor position when the mouse moves
        setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                requestFocus();
                cursorScreenX = event.getScreenX();
                cursorScreenY = event.getScreenY();
                cursorCanvasX = event.getX();
                cursorCanvasY = event.getY();
            }
        });
        
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    MarkerType type = MarkerType.getTypeForKey(event.getCode());
                    
                    AntIdDialog dialog = new AntIdDialog(getScene().getWindow());
                    //Move the dialog to the cursor position
                    dialog.setX(cursorScreenX);
                    dialog.setY(cursorScreenY);
                    AntId antId = dialog.showAndGetId();
                    
                    if(antId == null) {
                        //Do nothing
                        return;
                    }
                    
                    Point2D position = getFrameLocation(cursorCanvasX, cursorCanvasY);
                    
                    Marker marker = type.buildMarker(position);
                    marker.setAntId(antId);
                    getMarkers().add(marker);
                    repaint();
                    
                    event.consume();
                }
                catch (IllegalArgumentException | NotInFrameException ex) {
                    //No valid type for this key
                    //Do nothing
                }
            }
        });
        
        //Repaint when the frame or the markers changes
        image.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                requestFocus();
                repaint();
            }
        });
    }
    
    @Override
    protected void paint() {
        
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        
        //Draw image
        if(image.get() != null) {
            
            //Scale image to fit this canvas, but preserve its aspect ratio
            
            final double canvasWidth = getWidth();
            final double canvasHeight = getHeight();
            
            final double targetImageWidth = image.get().getWidth();
            final double targetImageHeight = image.get().getHeight();
            final double imageAspectRatio = targetImageWidth / targetImageHeight;
            
            final double widthRatio = targetImageWidth / canvasWidth;
            final double heightRatio = targetImageHeight / canvasHeight;
            
            if(heightRatio < widthRatio) {
                //Window is taller than image
                //If necessary, shrink image width to fit
                imageWidth = Math.min(targetImageWidth, canvasWidth);
                imageHeight = imageWidth / imageAspectRatio;
            }
            else {
                //Window is wider than image
                //If necessary, shrink image height to fit
                imageHeight = Math.min(targetImageHeight, canvasHeight);
                imageWidth = imageHeight * imageAspectRatio;
            }
            
            final double centerX = canvasWidth / 2;
            final double centerY = canvasHeight / 2;
            imageTopLeftX = centerX - imageWidth / 2;
            imageTopLeftY = centerY - imageHeight / 2;
            
            gc.drawImage(image.get(), imageTopLeftX, imageTopLeftY, imageWidth, imageHeight);
            
            gc.save();
            //Draw markers
            for(Marker marker : getMarkers()) {
                gc.setStroke(marker.getColor());
                
                final double imageXRatio = marker.getX() / image.get().getWidth();
                final double imageYRatio = marker.getY() / image.get().getHeight();
                
                final double canvasX = imageTopLeftX + imageWidth * imageXRatio;
                final double canvasY = imageTopLeftY + imageHeight * imageYRatio;
                
                marker.paint(gc, canvasX, canvasY);
            }
            
            gc.restore();
        }
        
        
    }
    
    
    /**
     * Returns the location, in frame image coordinates, of a mouse event
     * @param event
     * @return
     * @throws org.samcrow.frameviewer.ui.FrameCanvas.NotInFrameException If the mouse
     * event was not on the displayed frame
     */
    private Point2D getFrameLocation(MouseEvent event) throws NotInFrameException {
        return getFrameLocation(event.getX(), event.getY());
    }
    
    /**
     * Returns the location, in frame image coordinates, of a location in local
     * canvas coordinates
     * @param x
     * @param y
     * @return
     * @throws org.samcrow.frameviewer.ui.FrameCanvas.NotInFrameException 
     */
    private Point2D getFrameLocation(double x, double y) throws NotInFrameException {
        if( ( x < imageTopLeftX || x > (imageTopLeftX + imageWidth) )
              || ( y < imageTopLeftY || y > (imageTopLeftY + imageHeight)  ) ) {
            throw new NotInFrameException("The provided click was outside the bounds of the frame");
        }
        
        final double xRatio = (x - imageTopLeftX) / imageWidth;
        final double yRatio = (y - imageTopLeftY) / imageHeight;
        
        assert xRatio <= 1;
        assert yRatio <= 1;
        
        return new Point2D(image.get().getWidth() * xRatio, image.get().getHeight() * yRatio);
    }
    
    /**
     * Thrown when a mouse event is not inside the frame
     */
    private static class NotInFrameException extends Exception {
        public NotInFrameException(String message) {
            super(message);
        }
    }
    
    public final ObjectProperty<Image> imageProperty() {
        return image;
    }
    
    /**
     * Sets the markers.
     * The given list will be copied, so changes to it will not affect
     * the displayed markers.
     * @param newMarkers 
     */
    public final void setMarkers( List<Marker> newMarkers) {
        if(newMarkers == null) {
            throw new IllegalArgumentException("The marker list must not be null");
        }
        
        markers = newMarkers;
    }
    
    
    public final List<Marker> getMarkers() {
        return markers;
    }
    
}
