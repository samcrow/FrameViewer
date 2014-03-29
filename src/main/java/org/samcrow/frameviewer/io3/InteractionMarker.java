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

    private AntActivity metAntActivity;
    private AntLocation metAntLocation;
    
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
        buffer.append(metAntActivity.toString());
        buffer.append(',');
        buffer.append(metAntLocation.toString());
        
        return buffer.toString();
    }
}
