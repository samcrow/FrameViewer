package org.samcrow.frameviewer.ui;

import java.text.ParseException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import jfxtras.labs.dialogs.MonologFX;
import org.samcrow.frameviewer.FrameIndexOutOfBoundsException;

/**
 * A text field that displays a video timecode and allows it to be edited
 * @author Sam Crow
 */
public class TimeField extends TextField {
    
    private static final double FRAMES_PER_SECOND = 29.97;

    private static final int SECONDS_PER_MINUTE = 60;
    
    private final IntegerProperty currentFrame = new SimpleIntegerProperty();
    
    public TimeField() {
        setPrefColumnCount(5);
        
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                validateInput();
            }
        });
        
        //Validate the number when the field loses focus
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
                if(newValue == false) {
                    validateInput();
                }
            }
        });
        
        currentFrame.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                
                int currentSecond = (int) Math.floor( newValue.intValue() / FRAMES_PER_SECOND );
                
                setText(formatDuration(currentSecond));
            }
        });
        
        
    }
    
    private void validateInput() {
        try {
            int secondsEntered = parseDuration(this.getText());
            
            int newFrame = (int) Math.round(secondsEntered * FRAMES_PER_SECOND);
            //Ensure that frame >= 1
            if(newFrame < 1) {
                newFrame = 1;
            }
            setCurrentFrame(newFrame);
        }
        catch (ParseException ex) {
            //Revert
            int currentSecond = (int) Math.round(getCurrentFrame() / FRAMES_PER_SECOND);
            
            setText(formatDuration(currentSecond));
        }
        catch (FrameIndexOutOfBoundsException ex) {
            //Invalid frame number
            //Revert
            int currentSecond = (int) Math.round(getCurrentFrame() / FRAMES_PER_SECOND);
            
            setText(formatDuration(currentSecond));
            
            //Alert
            MonologFX dialog = new MonologFX(MonologFX.Type.ERROR);
            dialog.setTitle("Invalid time");
            dialog.setMessage("Please enter a time between "+formatDurationFromFrame(ex.getFirstFrame())+" and "+formatDurationFromFrame(ex.getLastFrame()));
            dialog.setModal(true);
            dialog.showDialog();
            
        }
        
    }
    
    /**
     * Returns a formatted duration with hours, minutes, and seconds
     * @param seconds The number of seconds to format
     * @return 
     */
    private static String formatDuration(int seconds) {
        
        int minutes = 0;
        
        while(seconds >= SECONDS_PER_MINUTE) {
            minutes++;
            seconds -= SECONDS_PER_MINUTE;
        }
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    private static String formatDurationFromFrame(int frame) {
        return formatDuration((int) Math.floor(frame / FRAMES_PER_SECOND));
    }
    
    /**
     * Parses a time string that contains a minutes value and a seconds value,
     * separated by a colon character
     * @param time The duration string to parse
     * @return The number of seconds represented by the value
     * @throws ParseException If the duration string was not in a valid format 
     */
    protected static int parseDuration(String time) throws ParseException {
        try {
        
        int firstColonIndex = time.indexOf(':');
        int min = Integer.valueOf(time.substring(0, firstColonIndex));
        int sec = Integer.valueOf(time.substring(firstColonIndex + 1));
        
        return (min * SECONDS_PER_MINUTE) + sec;
        
        
        }
        catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
            ParseException exception = new ParseException("Invalid duration string", 0);
            exception.initCause(ex);
            throw exception;
        }
    }
    
    
    public final int getCurrentFrame() {
        return currentFrame.get();
    }
    
    public final void setCurrentFrame(int newValue) {
        currentFrame.set(newValue);
    }
    
    final IntegerProperty currentFrameProperty() {
        return currentFrame;
    }
}
