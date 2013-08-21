package org.samcrow.frameviewer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

/**
 * Keeps track of the changes of an observable and if it has been saved
 * @author samcrow
 */
public class SaveStatusController {
    
    /**
     * If the value has any unsaved data
     */
    private final BooleanProperty unsavedData = new SimpleBooleanProperty();

    /**
     * Constructor
     * @param value The value to keep track of
     */
    public SaveStatusController(ObservableValue<?> value) {
        
        value.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                unsavedData.set(true);
            }
        });
    }
    
    
    public final ReadOnlyBooleanProperty unsavedDataProperty() {
        return unsavedData;
    }
    
    public final boolean hasUnsavedData() {
        return unsavedData.get();
    }
    
    /**
     * Marks the value as saved
     */
    public final void markSaved() {
        unsavedData.set(false);
    }
}
