package org.samcrow.frameviewer.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import org.samcrow.frameviewer.PaintableCanvas;

/**
 * Holds a Canvas and allows it to resize
 * <p/>
 * @param <T> The type of canvas to display
 * @author samcrow
 */
public class CanvasPane<T extends PaintableCanvas> extends Pane {

    private final ObjectProperty<T> canvas = new SimpleObjectProperty<>();

    public CanvasPane(T newView) {

        setPrefWidth(2000);
        setPrefHeight(2000);
        setMaxWidth(2000);
        setMaxHeight(2000);


        //Update children list when the view changes
        canvas.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                if (oldValue != null) {
                    getChildren().remove(oldValue);
                }
                if (newValue != null) {
                    getChildren().add(newValue);
                    
                    newValue.setWidth(getWidth());
                    newValue.setHeight(getHeight());
                }
            }
        });

        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number newValue) {
                if(canvas.get() != null) {
                    canvas.get().setWidth(newValue.doubleValue());
                    canvas.get().repaint();
                }
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number newValue) {
                if(canvas.get() != null) {
                    canvas.get().setHeight(newValue.doubleValue());
                    canvas.get().repaint();
                }
            }
        });

        canvas.set(newView);
    }

    public CanvasPane() {
        this(null);
    }

    public final T getCanvas() {
        return canvas.get();
    }

    public final void setCanvas(T newView) {
        canvas.set(newView);
    }

    public final ObjectProperty<T> canvasProperty() {
        return canvas;
    }

}
