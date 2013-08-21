package org.samcrow.frameviewer;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

/**
 * A canvas that provides a mechanism for requesting repaints
 * @author Sam Crow
 */
public abstract class PaintableCanvas extends Canvas {
    
    /**
     * This method is called to paint the contents of a canvas.
     * This is only called from the JavaFX application thread.
     */
    protected abstract void paint();
    
    /**
     * Causes this canvas to be painted. This method can safely be
     * called from any thread.
     */
    public void repaint() {
        if(!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            });
        }
        else {
            paint();
        }
    }
}
