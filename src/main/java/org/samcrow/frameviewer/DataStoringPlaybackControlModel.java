package org.samcrow.frameviewer;

import org.samcrow.frameviewer.ui.FrameCanvas;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXBuilder;
import jfxtras.labs.dialogs.MonologFXButton;

public class DataStoringPlaybackControlModel extends PlaybackControlModel {

    /**
     * The data store from which frames are read
     */
    private FrameDataStore<Marker> dataStore;

    private FrameCanvas canvas;

    public DataStoringPlaybackControlModel(FrameFinder frameFinder, FrameDataStore<Marker> newDataStore) {
        super(frameFinder);
        dataStore = newDataStore;

        currentFrameProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {

                if (canvas != null) {
                    int oldFrame = oldValue.intValue();
                    int newFrame = newValue.intValue();
                    //Save the current markers from the old frame
                    List<Marker> markers = canvas.getMarkers();
                    //Assign each marker the correct frame
                    for (Marker marker : markers) {
                        marker.setFrame(oldFrame);
                    }
                    dataStore.setFrameData(oldFrame, markers);


                    //Get the markers for the new frame
                    List<Marker> newMarkers = dataStore.getFrameData(newFrame);
                    canvas.setMarkers(newMarkers);
                    
                    canvas.repaint();
                }
                else {
                    Logger.getLogger(DataStoringPlaybackControlModel.class.getName()).warning("No canvas is set. This model will not update any markers.");
                }
            }
        });
    }

    /**
     * Sets up a canvas to have its markers be managed by this model
     * <p/>
     * @param canvas
     */
    public final void bindMarkers(FrameCanvas canvas) {
        this.canvas = canvas;
    }

    public void setDataStore(FrameDataStore<Marker> dataStore) {
        this.dataStore = dataStore;
        //Move to frame 1
        setCurrentFrame(1);
        canvas.setMarkers(this.dataStore.getFrameData(1));
        canvas.repaint();
    }

    /**
     * Stores the data from the current frame into the data store.
     * This should be called before a file is saved to ensure that data from the
     * current frame gets saved.
     */
    public void syncCurrentFrameData() {
        final int frame = getCurrentFrame();
        List<Marker> markers = canvas.getMarkers();
        //Assign each marker the correct frame
        for (Marker marker : markers) {
            marker.setFrame(frame);
        }
        dataStore.setFrameData(frame, markers);
    }

    public void undo() {
        //Check for an item to undo
        if(canvas.getMarkers().isEmpty()) {
            //Error: No marker to delete
            MonologFX dialog = new MonologFX(MonologFX.Type.ERROR);
            dialog.setTitle("Nothing to delete");
            dialog.setMessage("This video frame does not have any points to delete");
            dialog.setModal(true);
            dialog.showDialog();
        }
        else {
            //Ask for confirmation
            MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
            
            MonologFXButton okButton = new MonologFXButton();
            okButton.setType(MonologFXButton.Type.OK);
            okButton.setLabel("OK");
            okButton.setDefaultButton(true);
            dialog.addButton(okButton);
            
            MonologFXButton cancelButton = new MonologFXButton();
            cancelButton.setType(MonologFXButton.Type.CANCEL);
            cancelButton.setLabel("Cancel");
            cancelButton.setCancelButton(true);
            dialog.addButton(cancelButton);
            
            dialog.setTitle("Confirm delete");
            dialog.setMessage("Delete the most recently entered point from this frame?");
            
            MonologFXButton.Type result = dialog.showDialog();
            
            if(result == MonologFXButton.Type.OK) {
                //Remove the last item in the list
                canvas.getMarkers().remove(canvas.getMarkers().size() - 1 );
                canvas.repaint();
            }
            
        }
    }
}
