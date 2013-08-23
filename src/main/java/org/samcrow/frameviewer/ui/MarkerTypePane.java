package org.samcrow.frameviewer.ui;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import org.samcrow.frameviewer.MarkerType;

/**
 * A pane that displays the marker types that can be created
 * @author Sam Crow
 */
public class MarkerTypePane extends GridPane {
    
    public MarkerTypePane() {
        
        MarkerType[] types = MarkerType.values();
        
        final Insets MARGIN = new Insets(5);
        
        
            
        String bgStyle = "-fx-background-color: rgba(255, 255, 255, 0.5)";
        setStyle(bgStyle);
        
        int index = 0;
        for(MarkerType type : types) {
            
            final Canvas graphic = type.getPaintedCanvas();
            final Label nameLabel = new Label(type.getMarkerTypeName());
            final Label actionLabel = new Label(formatActions(type.getKey(), type.getMouseButton()));
            
            add(graphic, 0, index);
            add(nameLabel, 1, index);
            add(actionLabel, 2, index);
            setMargin(graphic, MARGIN);
            setMargin(nameLabel, MARGIN);
            setMargin(actionLabel, MARGIN);
            
            index++;
        }
    }
    
    private static String formatActions(KeyCombination keys, MouseButton mouseButton) {
        if(keys != null && mouseButton != null) {
            return formatMouseButton(mouseButton) + " or " + formatKeyCombination(keys);
        }
        else if(keys != null) {
            return formatKeyCombination(keys);
        }
        else if(mouseButton != null) {
            return formatMouseButton(mouseButton);
        }
        else {
            return "none";
        }
    }
    
    private static String formatKeyCombination(KeyCombination combination) {
        return combination.toString();
    }
    
    private static String formatMouseButton(MouseButton button) {
        
        return button.toString().toLowerCase() + " mouse button";
    }
}
