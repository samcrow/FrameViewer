package org.samcrow.frameviewer.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import org.samcrow.frameviewer.PlaybackControlModel;

/**
 * Displays the elapsed time. Assumes a frame rate of 29.97 fps
 * @author Sam Crow
 */
public class ElapsedTimeView extends Label {
    
    private static final double FRAME_RATE = 29.97;
    
    private final IntegerProperty currentFrame = new SimpleIntegerProperty();
    
    public ElapsedTimeView(PlaybackControlModel model) {
        
        currentFrame.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number newValue) {
                int frame = newValue.intValue();
                
                int seconds = (int) Math.round(frame / FRAME_RATE);
                
                setText(formatDuration(seconds));
            }
        });
        
        currentFrame.set(model.getFirstFrame());
    }
    
    
    
    /**
     * Returns a formatted duration with hours, minutes, and seconds
     * @param seconds The number of seconds to format
     * @return 
     */
    private static String formatDuration(int seconds) {
        
        final int MINUTES_PER_HOUR = 60;
        final int SECONDS_PER_MINUTE = 60;
        final int SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
        
        int hours = 0;
        int minutes = 0;
        
        while(seconds >= SECONDS_PER_HOUR) {
            hours++;
            seconds -= SECONDS_PER_HOUR;
        }
        while(seconds >= SECONDS_PER_MINUTE) {
            minutes++;
            seconds -= SECONDS_PER_MINUTE;
        }
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public final IntegerProperty currentFrameProperty() {
        return currentFrame;
    }
}
