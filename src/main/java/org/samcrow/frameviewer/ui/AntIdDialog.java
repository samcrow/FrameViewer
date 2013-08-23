package org.samcrow.frameviewer.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.samcrow.frameviewer.AntId;

/**
 * A dialog that asks the user to enter an ant ID
 * @author Sam Crow
 */
public class AntIdDialog extends Stage {
    
    /**
     * The last ant ID that was entered. This is used to suggest a new ant ID.
     */
    private static AntId lastAntId;
    
    private boolean succeeded = false;

    private final IntegerField antIdField;

    private final ChoiceBox<AntId.Type> typeBox;
    
    public AntIdDialog(Window parent) {
        
        final Insets PADDING = new Insets(10);
        
        VBox root = new VBox();
        
        GridPane topBox = new GridPane();
        {
            final Label label = new Label("Ant ID");
            topBox.add(label, 0, 0);
            GridPane.setMargin(label, PADDING);
            
            antIdField = new IntegerField();
            antIdField.setPrefColumnCount(4);
            if(lastAntId != null) {
                antIdField.setValue(lastAntId.getId());
            }
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
            
            //Type field label
            final Label typeLabel = new Label("Type:");
            topBox.add(typeLabel, 0, 1);
            GridPane.setMargin(typeLabel, PADDING);
            
            //Type field
            typeBox = new ChoiceBox<>(FXCollections.observableArrayList(AntId.Type.values()));
            //Set default selection
            typeBox.getSelectionModel().select(AntId.Type.Control);
            if(lastAntId != null) {
                typeBox.getSelectionModel().select(lastAntId.getType());
            }
            topBox.add(typeBox, 1, 1);
            GridPane.setMargin(typeBox, PADDING);
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
                    close();
                }
            });
            bottomBox.getChildren().add(okButton);
            HBox.setMargin(okButton, PADDING);
        }
        root.getChildren().add(bottomBox);
        
        
        setTitle("Enter ant ID");
        initOwner(parent);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        
        root.setPadding(PADDING);
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        setScene(scene);
    }
    
    /**
     * Shows this dialog, waits for it to close, and returns the entered ant ID
     * @return The entered ant ID, or -1 if the user canceled the operation
     */
    public AntId showAndGetId() {
        showAndWait();
        return getEnteredId();
    }
    
    /**
     * 
     * @return The ant ID that was entered, or -1 if the user canceled the operation
     */
    public AntId getEnteredId() {
        if(succeeded) {
            lastAntId = new AntId(antIdField.getValue(), typeBox.getValue());
            return lastAntId;
        }
        else {
            return null;
        }
    }
}
