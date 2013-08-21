package org.samcrow.frameviewer;

import java.util.TimerTask;
import javafx.application.Platform;

/**
 * Plays video in a {@link FrameView}
 * <p/>
 * @author Sam Crow
 */
abstract class FramePlayer extends TimerTask {

    /**
     * The model that is to be manipulated
     */
    private final PlaybackControlModel model;

    /**
     * The minimum number of microseconds to wait between displaying consecutive
     * frames
     */
    private final long millisecondsBetweenFrames;

    /**
     * The number of frames by which to increment the playback position
     */
    protected static final int FRAME_INCREMENT = 10;
    
    /**
     * The runnable to be executed when this task is canceled
     */
    private Runnable cancelCallback;

    /**
     * Constructor
     * <p/>
     * @param model The model to manipulate
     * @param frameRate The maximum frame rate
     */
    public FramePlayer(PlaybackControlModel model, double frameRate) {
        this.model = model;

        millisecondsBetweenFrames = Math.round(1000 / frameRate);
    }
    
    /**
     * 
     * @return The delay, in milliseconds, between frame advancements
     */
    public long getMillisecondsBetweenFrames() {
        return millisecondsBetweenFrames * FRAME_INCREMENT;
    }

    /**
     * Constructor. The frame rate will be initialized to 29.97
     * <p/>
     * @param model The model to manipulate
     */
    public FramePlayer(PlaybackControlModel model) {
        this(model, 29.97);
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                traverseFrame();
            }
        });
    }

    @Override
    public boolean cancel() {
        boolean returnValue = super.cancel();
        //Run the cancel callback
        if(cancelCallback != null) {
            Platform.runLater(cancelCallback);
        }
        
        return returnValue;
    }
    
    /**
     * Set the runnable to be executed when this player is cancelled.
     * @param runnable The runnable to run. This will be called from the
     * JavaFX application thread.
     */
    public void setOnCancelled(Runnable runnable) {
        cancelCallback = runnable;
    }
    
    

    protected PlaybackControlModel getModel() {
        return model;
    }

    /**
     * Subclasses should implement this method and set the current frame of
     * the view returned by {@link #getModel()} as necessary, potentially
     * incrementing or decrementing it.
     * If the traversal has reached the end if its span,
     * this method should call {@link #cancel()}.
     * This method will be called only from the JavaFX application thread.
     */
    protected abstract void traverseFrame();

}
