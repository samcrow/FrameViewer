package org.samcrow.frameviewer;

import org.samcrow.frameviewer.ui.CanvasPane;
import org.samcrow.frameviewer.ui.FrameCanvas;
import org.samcrow.frameviewer.ui.PlaybackControlPane;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;
import org.samcrow.frameviewer.io3.Marker;
import org.samcrow.frameviewer.io3.PersistentFrameDataStore;
import org.samcrow.frameviewer.ui.SaveDialog;

/**
 * Hello world!
 *
 */
public class App extends Application {

    /**
     * The data model
     */
    private PersistentFrameDataStore<Marker> dataStore;

    private SaveStatusController saveController;

    /**
     * The file that was last opened
     */
    private File lastOpenedFile;

    private Stage stage;

    private DataStoringPlaybackControlModel model;

    @Override
    public void start(final Stage stage) {
        this.stage = stage;

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (saveController.hasUnsavedData()) {
                    //Ask to save
                    SaveDialog dialog = new SaveDialog();
                    dialog.initOwner(stage);

                    MonologFXButton.Type returnType = dialog.showDialog();
                    if (returnType == MonologFXButton.Type.CANCEL) {
                        //Cancel close
                        event.consume();

                    }
                    else if (returnType == MonologFXButton.Type.NO) {
                        //Don't save; allow close
                    }
                    else {
                        try {
                            //Save the file
                            saveFile();
                        }
                        catch (IOException ex) {
                            //Save failed
                            //Cancel close
                            event.consume();
                        }
                    }
                }
            }
        });

        try {

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose a directory with images to process");
            File frameDir = chooser.showDialog(stage);

            VBox box = new VBox();
            
            MenuBar bar = createMenuBar();
            bar.setUseSystemMenuBar(true);
            box.getChildren().add(bar);


            dataStore = new PersistentFrameDataStore<>();
            saveController = new SaveStatusController(dataStore);
            FrameFinder finder = new FrameFinder(frameDir);
            model = new DataStoringPlaybackControlModel(finder, dataStore);
            

            FrameCanvas canvas = new FrameCanvas();
            canvas.imageProperty().bind(model.currentFrameImageProperty());
            model.bindMarkers(canvas);

            box.getChildren().add(new CanvasPane<>(canvas));

            PlaybackControlPane controls = new PlaybackControlPane(model);
            box.getChildren().add(controls);

            //Assemble the root StackPane
            StackPane root = new StackPane();
            root.getChildren().add(box);

            stage.setTitle("Frame Viewer");
            Scene scene = new Scene(root);
            
            controls.setupAccelerators();
            
            stage.setScene(scene);
            stage.show();

        }
        catch (Exception ex) {
            MonologFX dialog = new MonologFX(MonologFX.Type.ERROR);
            dialog.setTitle("Error");
            dialog.setMessage(ex.toString());
            dialog.showDialog();
            ex.printStackTrace();
            stop();
        }
    }

    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();

        final Menu fileMenu = new Menu("File");

        final MenuItem saveItem = new MenuItem("Save as...");
        saveItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        saveItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    saveFile();
                }
                catch (IOException ex) {
                    
                }
            }
        });
        
        final MenuItem openItem = new MenuItem("Open...");
        openItem.setAccelerator(KeyCombination.keyCombination("Shortcut+O"));
        openItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                openFile();
            }
        });

        fileMenu.getItems().addAll(openItem, saveItem);
        
        bar.getMenus().add(fileMenu);

        final Menu editMenu = new Menu("Edit");
        final MenuItem undoItem = new MenuItem("Undo");
        undoItem.setAccelerator(KeyCombination.keyCombination("Shortcut+Z"));
        undoItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                model.undo();
            }
        });
        
        editMenu.getItems().add(undoItem);
        bar.getMenus().add(editMenu);
        
        return bar;
    }

    private void saveFile() throws IOException {
        model.syncCurrentFrameData();
        
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        if (lastOpenedFile != null) {
            chooser.setInitialDirectory(lastOpenedFile.getParentFile());
        }
        File saveFile = chooser.showSaveDialog(stage);
        try {
            dataStore.writeTo(saveFile);
            saveController.markSaved();
        }
        catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            MonologFX errDialog = new MonologFX(MonologFX.Type.ERROR);
            errDialog.setModal(true);
            errDialog.initOwner(stage);
            errDialog.setTitleText("Save failed");
            errDialog.setMessage(ex.getLocalizedMessage());

            errDialog.showDialog();
            
            throw ex;
        }
    }
    
    private void openFile() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
            
            lastOpenedFile = chooser.showOpenDialog(stage);
            
            dataStore = PersistentFrameDataStore.readFromFile(lastOpenedFile);
            model.setDataStore(dataStore);
        }
        catch (IOException | ParseException | IllegalStateException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            MonologFX errDialog = new MonologFX(MonologFX.Type.ERROR);
            errDialog.setModal(true);
            errDialog.initOwner(stage);
            errDialog.setTitleText("Open failed");
            errDialog.setMessage(ex.getLocalizedMessage());

            errDialog.showDialog();
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
