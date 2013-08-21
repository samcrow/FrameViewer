package org.samcrow.frameviewer.ui;

import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;

/**
 * A dialog that asks the user if he/she wants to save a document or not
 * @author Sam Crow
 */
public class SaveDialog extends MonologFX {
    
    public SaveDialog() {
        super(Type.QUESTION);
        
        setTitle("Save file");
        setMessage("A file may be unsaved. Would you like to save it?");
        setModal(true);
        
        MonologFXButton saveButton = new MonologFXButton();
        saveButton.setType(MonologFXButton.Type.YES);
        saveButton.setLabel("Save");
        saveButton.setDefaultButton(true);
        
        
        MonologFXButton dontSaveButton = new MonologFXButton();
        dontSaveButton.setType(MonologFXButton.Type.NO);
        dontSaveButton.setLabel("Don't Save");
        
        
        MonologFXButton cancelButton = new MonologFXButton();
        cancelButton.setType(MonologFXButton.Type.CANCEL);
        cancelButton.setLabel("Cancel");
        cancelButton.setCancelButton(true);
        
        addButton(cancelButton);
        addButton(saveButton);
        addButton(dontSaveButton);
    }
    
}
