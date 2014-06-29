package org.samcrow.frameviewer.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Window;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;
import org.samcrow.frameviewer.io3.InteractionMarker;
import org.samcrow.frameviewer.io3.Marker;

/**
 *
 * @author samcrow
 */
public class MarkerEditDialog extends MarkerDialog {
    
    private boolean deleted = false;
    
    /**
     * Creates a dialog and populates its fields with the attributes
     * of the given marker
     * @param parent
     * @param markerToEdit 
     */
    public MarkerEditDialog(Window parent, Marker markerToEdit) {
        super(parent);
        
        antIdField.setValue(markerToEdit.getAntId());
        activityBox.setValue(markerToEdit.getFocusAntActivity());
        locationBox.setValue(markerToEdit.getFocusAntLocation());
        
        if(markerToEdit instanceof InteractionMarker) {
            InteractionMarker iMarker = (InteractionMarker) markerToEdit;
            interactionBox.setSelected(true);
            interactionTypeBox.setValue(iMarker.getType());
            metActivityBox.setValue(iMarker.getMetAntActivity());
            metLocationBox.setValue(iMarker.getMetAntLocation());
        }
        else {
            interactionBox.setSelected(false);
        }
        
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                
                // Ask for confirmation
                
                MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
                MonologFXButton okButton = new MonologFXButton();
                okButton.setType(MonologFXButton.Type.OK);
                okButton.setDefaultButton(true);
                okButton.setLabel("OK");
                MonologFXButton cancelButton = new MonologFXButton();
                cancelButton.setType(MonologFXButton.Type.CANCEL);
                cancelButton.setCancelButton(true);
                cancelButton.setLabel("Cancel");
                
                dialog.addButton(okButton);
                dialog.addButton(cancelButton);
                
                dialog.setModal(true);
                dialog.setTitleText("Delete marker?");
                dialog.setMessage("Are you sure you want to delete this marker?");
                
                dialog.initOwner(getOwner());
                MonologFXButton.Type response = dialog.showDialog();
                
                if(response == MonologFXButton.Type.OK) {
                    deleted = true;
                    succeeded = true;
                    close();
                }
            }
        });
        
        insertNode(deleteButton);
        
        setTitle("Edit marker");
    }
    
    /**
     * 
     * @return true if the user chose to delete the marker, otherwise false
     */
    public final boolean deleted() {
        return deleted;
    }
}
