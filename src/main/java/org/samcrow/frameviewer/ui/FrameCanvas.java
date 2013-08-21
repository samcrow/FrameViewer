package org.samcrow.frameviewer.ui;

import java.util.LinkedList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import org.samcrow.frameviewer.Marker;
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
    
    public FrameCanvas() {
        
        //Add a marker when clicked on
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
                try {
                    Point2D markerPoint = getFrameLocation(event);
                    
                    getMarkers().add(new Marker(markerPoint));
                    repaint();
                }
                catch (NotInFrameException ex) {
                    //Ignore this click
                }
                
            }
        });
        
        //Repaint when the frame or the markers changes
        image.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
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
            
            Paint initialStroke = gc.getStroke();
            //Draw markers
            for(Marker marker : getMarkers()) {
                gc.setStroke(marker.getColor());
                
                final double imageXRatio = marker.getX() / image.get().getWidth();
                final double imageYRatio = marker.getY() / image.get().getHeight();
                
                final double canvasX = imageTopLeftX + imageWidth * imageXRatio;
                final double canvasY = imageTopLeftY + imageHeight * imageYRatio;
                
                final int radius = 3;
                gc.setLineWidth(2);
                
                gc.strokeOval(canvasX - radius, canvasY - radius, radius * 2, radius * 2);
            }
            
            gc.setStroke(initialStroke);
        }
        
        
    }
    
    
    /**
     * Returns the location, in frame image coordinates, of a mouse event
     * @param event
     * @return
     * @throws org.samcrow.frameviewer.FrameCanvas.NotInFrameException If the mouse
     * event was not on the displayed frame
     */
    private Point2D getFrameLocation(MouseEvent event) throws NotInFrameException {
        
        if(image.get() == null) {
            throw new NotInFrameException("No image available");
        }
        
        double x = event.getX();
        double y = event.getY();
        
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
