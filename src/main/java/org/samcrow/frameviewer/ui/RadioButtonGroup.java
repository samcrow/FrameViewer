package org.samcrow.frameviewer.ui;

import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Toggle;

/**
 * Manages a group of radio buttons and associated objects
 * 
 * @param <T> The type of object to allow the user to choose between
 * @author Sam Crow
 */
public class RadioButtonGroup <T> extends GridPane {
    
    private ToggleGroup group = new ToggleGroup();
    
    private BiMap < T, Toggle > valueToButton = HashBiMap.create();
    private BiMap < Toggle, T > buttonToValue = valueToButton.inverse();
    
    private final ObjectProperty<T> selectedValue = new SimpleObjectProperty<>();
    
    private RadioButtonGroup() {
        
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldButton, Toggle newButton) {
                // Set the new selectedValue to match the selected toggle
                T newValue = buttonToValue.get(newButton);
                if(newValue != getValue()) {
                    setValue(newValue);
                }
            }
        });
        
        selectedValue.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                // Set the selected toggle to match the new value
                Toggle newButton = valueToButton.get(newValue);
                if(newButton != group.getSelectedToggle()) {
                    group.selectToggle(newButton);
                }
            }
        });
        
    }
    
    public RadioButtonGroup(T[] values) {
        // Call default constructor
        this();
        
        final Insets PADDING = new Insets(2);
        
        // Create a radio button for each selectedValue
        int i = 0;
        for(T value : values) {
            RadioButton button = new RadioButton(value.toString());
            button.setToggleGroup(group);
            button.setAlignment(Pos.BASELINE_LEFT);
            // Add the button to the grid layout
            add(button, 0, i);
            GridPane.setMargin(button, PADDING);
            
            
            valueToButton.put(value, button);
            
            i++;
        }
    }
    
    public final ObjectProperty<T> valueProperty() {
        return selectedValue;
    }
    
    public final T getValue() {
        return selectedValue.get();
    }
    
    public final void setValue(T newValue) {
        selectedValue.set(newValue);
    }
    
}
