package org.samcrow.frameviewer.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.samcrow.frameviewer.io3.AntActivity;
import org.samcrow.frameviewer.io3.AntLocation;
import org.samcrow.frameviewer.io3.InteractionMarker;
import org.samcrow.frameviewer.io3.Marker;

/**
 * A dialog that asks the user to enter an ant ID
 * @author Sam Crow
 */
public class AntIdDialog extends Stage {
    
    /**
     * The last ant ID that was entered. This is used to suggest a new ant ID.
     */
    private static int lastAntId = 0;
    private static AntActivity lastActivity = AntActivity.Unknown;
    private static AntLocation lastLocation = AntLocation.Unknown;
    private static boolean lastIsInteraction = false;
    private static AntActivity lastMetActivity = AntActivity.Unknown;
    private static AntLocation lastMetLocation = AntLocation.Unknown;
    
    private boolean succeeded = false;

    private final IntegerField antIdField = new IntegerField();
    
    private final ChoiceBox<AntActivity> activityBox = new ChoiceBox<>(FXCollections.observableArrayList(AntActivity.values()));
    private final ChoiceBox<AntLocation> locationBox = new ChoiceBox<>(FXCollections.observableArrayList(AntLocation.values()));
    
    private final CheckBox interactionBox = new CheckBox("Interaction");
    private final ChoiceBox<AntActivity> metActivityBox = new ChoiceBox<>(FXCollections.observableArrayList(AntActivity.values()));
    private final ChoiceBox<AntLocation> metLocationBox = new ChoiceBox<>(FXCollections.observableArrayList(AntLocation.values()));
    
    
    public AntIdDialog(Window parent) {
        
        final Insets PADDING = new Insets(10);
        
        VBox root = new VBox();
        
        GridPane topBox = new GridPane();
        {
            final Label label = new Label("Focus ant");
            topBox.add(label, 0, 0);
            GridPane.setMargin(label, PADDING);
            
            antIdField.setPrefColumnCount(4);
            //Close when the return key is pressed
            antIdField.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    succeeded = true;
                    close();
                }
            });
            
            topBox.add(antIdField, 1, 0);
            GridPane.setMargin(antIdField, PADDING);
            
            // Focus ant activity and location
            topBox.add(activityBox, 0, 1, 2, 1);
            GridPane.setMargin(activityBox, PADDING);
            
            topBox.add(locationBox, 0, 2, 2, 1);
            GridPane.setMargin(locationBox, PADDING);

            // Checkbox and met ant selectors
            topBox.add(interactionBox, 0, 3, 2, 1);
            GridPane.setMargin(interactionBox, PADDING);
            
            topBox.add(metActivityBox, 0, 4, 2, 1);
            GridPane.setMargin(metActivityBox, PADDING);
            
            topBox.add(metLocationBox, 0, 5, 2, 1);
            GridPane.setMargin(metLocationBox, PADDING);
            
            
            
        }
        root.getChildren().add(topBox);
        
        
        HBox bottomBox = new HBox();
        {
            final Button cancelButton = new Button("Cancel");
            cancelButton.setCancelButton(true);
            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    succeeded = false;
                    close();
                }
            });
            bottomBox.getChildren().add(cancelButton);
            HBox.setMargin(cancelButton, PADDING);
            
            final Button okButton = new Button("OK");
            okButton.setDefaultButton(true);
            okButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    succeeded = true;
                    saveValues();
                    close();
                }
            });
            bottomBox.getChildren().add(okButton);
            HBox.setMargin(okButton, PADDING);
        }
        root.getChildren().add(bottomBox);
        
        // Bind checkbox to met ant enable/disable
        metActivityBox.disableProperty().bind(interactionBox.selectedProperty().not());
        metLocationBox.disableProperty().bind(interactionBox.selectedProperty().not());
        
        // Fill in initial values for fields
        antIdField.setValue(lastAntId);
        activityBox.getSelectionModel().select(lastActivity);
        locationBox.getSelectionModel().select(lastLocation);
        interactionBox.setSelected(lastIsInteraction);
        metActivityBox.getSelectionModel().select(lastMetActivity);
        metLocationBox.getSelectionModel().select(lastMetLocation);
        
        
        setTitle("Enter marker info");
        initOwner(parent);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        
        root.setPadding(PADDING);
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        setScene(scene);
    }
    
    /**
     * Saves the current form values in static fields for later access
     */
    private void saveValues() {
        
        lastAntId = antIdField.getValue();
        lastActivity = activityBox.getValue();
        lastLocation = locationBox.getValue();
        lastIsInteraction = interactionBox.isSelected();
        lastMetActivity = metActivityBox.getValue();
        lastMetLocation = metLocationBox.getValue();
    }
    
    /**
     * 
     * @return A Marker, created based on the user's selections. Its X and Y
     * values will be set to zero.
     */
    public Marker getSelectedMarker() {
        Marker marker;
        if(interactionBox.isSelected()) {
            marker = new InteractionMarker(0, 0, activityBox.getValue(), locationBox.getValue(), metActivityBox.getValue(), metLocationBox.getValue());
        }
        else {
            marker = new Marker(0, 0, activityBox.getValue(), locationBox.getValue());
        }
        marker.setAntId(antIdField.getValue());
        return marker;
    }
    
    /**
     * 
     * @return True if the user entered valid values, otherwise false
     */
    public boolean success() {
        return succeeded;
    }
}
