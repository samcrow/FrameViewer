package org.samcrow.frameviewer.ui;

import java.util.ListIterator;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
 * A dialog that asks the user to marker information or allows the user to
 * edit an existing marker
 * @author Sam Crow
 */
public class MarkerDialog extends Stage {
    
    /**
     * The last ant ID that was entered. This is used to suggest a new ant ID.
     */
    private static int lastAntId = 0;
    private static AntActivity lastActivity = AntActivity.Unknown;
    private static AntLocation lastLocation = AntLocation.Unknown;
    private static boolean lastIsInteraction = false;
    private static int lastMetAntId = 0;
    private static InteractionMarker.InteractionType lastInteractionType = InteractionMarker.InteractionType.Unknown;
    private static AntActivity lastMetActivity = AntActivity.Unknown;
    private static AntLocation lastMetLocation = AntLocation.Unknown;
    
    protected boolean succeeded = false;

    protected final IntegerField antIdField = new IntegerField();
    
    protected final RadioButtonGroup<AntActivity> activityBox = new RadioButtonGroup<>(AntActivity.values());

    protected final RadioButtonGroup<AntLocation> locationBox = new RadioButtonGroup<>(AntLocation.values());
    
    protected final CheckBox interactionBox = new CheckBox("Interaction");
    
    protected final RadioButtonGroup<InteractionMarker.InteractionType> interactionTypeBox
            = new RadioButtonGroup<>(InteractionMarker.InteractionType.values());
    
    protected final IntegerField metAntIdField = new IntegerField(0);
    
    protected final RadioButtonGroup<AntActivity> metActivityBox = new RadioButtonGroup<>(AntActivity.values());
    protected final RadioButtonGroup<AntLocation> metLocationBox = new RadioButtonGroup<>(AntLocation.values());
    

    private final VBox root = new VBox();

    private static final Insets PADDING = new Insets(10);
    
    
    public MarkerDialog(Window parent) {
        
        GridPane topBox = new GridPane();
        {
            
            antIdField.setPrefColumnCount(4);
            //Close when the return key is pressed
            antIdField.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    antIdField.setFieldFocused(false);
                    succeeded = true;
                    saveValues();
                    close();
                }
            });
            
            GridPane focalAntPane = new GridPane();
            {
                final Label label = new Label("Focal ant:");
                focalAntPane.add(label, 0, 0);
                GridPane.setMargin(label, PADDING);
                
                focalAntPane.add(antIdField, 1, 0);
                GridPane.setMargin(antIdField, PADDING);

                // Focus ant activity and location
                focalAntPane.add(activityBox, 0, 1, 2, 1);
                GridPane.setMargin(activityBox, PADDING);

                focalAntPane.add(locationBox, 0, 2, 2, 1);
                GridPane.setMargin(locationBox, PADDING);
            
            }

            // Checkbox and met ant selectors
            GridPane metAntPane = new GridPane();
            {
                metAntPane.add(interactionBox, 0, 0, 1, 1);
                GridPane.setMargin(interactionBox, PADDING);
                // Interaction type to the right of the checkbox
                metAntPane.add(interactionTypeBox, 1, 0, 1, 1);
                GridPane.setMargin(interactionTypeBox, PADDING);

                Label metLabel = new Label("Met ant:");
                metAntPane.add(metLabel, 0, 1, 1, 1);
                GridPane.setMargin(metLabel, PADDING);
                metLabel.disableProperty().bind(interactionBox.selectedProperty().not());

                // Met ant ID field
                metAntPane.add(metAntIdField, 1, 1, 1, 1);
                GridPane.setMargin(metAntIdField, PADDING);

                metAntPane.add(metActivityBox, 0, 2, 2, 1);
                GridPane.setMargin(metActivityBox, PADDING);

                metAntPane.add(metLocationBox, 0, 3, 2, 1);
                GridPane.setMargin(metLocationBox, PADDING);
            
            }
            
            topBox.add(focalAntPane, 0, 0, 1, 1);
            topBox.add(metAntPane, 1, 0, 1, 1);
            
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
        metAntIdField.disableProperty().bind(interactionBox.selectedProperty().not());
        interactionTypeBox.disableProperty().bind(interactionBox.selectedProperty().not());
        
        // Fill in initial values for fields
        antIdField.setValue(lastAntId);
        activityBox.setValue(lastActivity);
        locationBox.setValue(lastLocation);
        interactionBox.setSelected(lastIsInteraction);
        interactionTypeBox.setValue(lastInteractionType);
        metAntIdField.setValue(lastMetAntId);
        metActivityBox.setValue(lastMetActivity);
        metLocationBox.setValue(lastMetLocation);
        
        
        setTitle("New marker");
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
    protected void saveValues() {
        
        lastAntId = antIdField.getValue();
        lastActivity = activityBox.getValue();
        lastLocation = locationBox.getValue();
        lastIsInteraction = interactionBox.isSelected();
        lastMetAntId = metAntIdField.getValue();
        lastInteractionType = interactionTypeBox.getValue();
        lastMetActivity = metActivityBox.getValue();
        lastMetLocation = metLocationBox.getValue();
    }
    
    /**
     * 
     * @return A Marker, created based on the user's selections. Its X and Y
     * values will be set to zero.
     */
    public Marker createMarker() {
        Marker marker;
        if(interactionBox.isSelected()) {
            InteractionMarker interactionMarker = new InteractionMarker(0, 0, activityBox.getValue(), locationBox.getValue(), metActivityBox.getValue(), metLocationBox.getValue());
            interactionMarker.setType(interactionTypeBox.getValue());
            interactionMarker.setMetAntId(metAntIdField.getValue());
            marker = interactionMarker;
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
    
    
    /**
     * Sets whether the dialog should be set up to record an interaction
     * @param isInteraction 
     */
    public void setIsInteraction(boolean isInteraction) {
        interactionBox.setSelected(isInteraction);
    }
    
    public static int getLastAntId() {
        return lastAntId;
    }
    
    /**
     * Adds a node to this window, below the controls and above the OK/cancel
     * buttons.
     * @param node 
     */
    protected void insertNode(Node node) {
        ObservableList<Node> children = root.getChildren();
        // Get an iterator with a cursor just before the last element
        ListIterator<Node> iterator = children.listIterator(children.size() - 1);
        // Insert just before the last element
        iterator.add(node);
        VBox.setMargin(node, PADDING);
    }
}
