package org.samcrow.frameviewer.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.samcrow.frameviewer.io2.MarkerType;

/**
 * A dialog that asks the user to choose a type of marker to do something with
 * <p/>
 * @author Sam Crow
 */
public class MarkerTypeDialog extends Stage {

    private MarkerType chosenType = null;

    public MarkerTypeDialog(Window owner) {

        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        initStyle(StageStyle.UTILITY);

        setTitle("Choose a marker type");

        //Layout

        VBox root = new VBox();
        final Insets MARGIN = new Insets(10);

        root.setPadding(MARGIN);

        //A button for each type
        for (final MarkerType type : MarkerType.values()) {
            Button button = new Button(type.getMarkerTypeName());

            //Make the default marker type the default button
            if(type == MarkerType.getDefaultType()) {
                button.setDefaultButton(true);
            }
            
            //When this button is clicked on, set the chosen type
            //and close the window
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    chosenType = type;
                    close();
                }
            });

            root.getChildren().add(button);
            VBox.setMargin(button, MARGIN);
        }

        //Cancel button, separated by a separator
        Separator separator = new Separator(Orientation.HORIZONTAL);
        root.getChildren().add(separator);
        VBox.setMargin(separator, MARGIN);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                close();
            }
        });
        root.getChildren().add(cancelButton);
        VBox.setMargin(cancelButton, MARGIN);
        

        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        setScene(scene);
    }

    /**
     * @return The marker type that the user has chosen, or null if no type
     * has been selected yet
     */
    public MarkerType getChosenType() {
        return chosenType;
    }

    /**
     * Shows this dialog, waits for it to close, and returns the selected type
     * <p/>
     * @return The selected type, or null if the user did not choose a type
     */
    public MarkerType showAndGetType() {
        showAndWait();
        return chosenType;
    }

}
