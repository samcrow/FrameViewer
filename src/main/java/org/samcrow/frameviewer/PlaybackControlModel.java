package org.samcrow.frameviewer;

import java.util.Timer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

/**
 * Handles the logic of playback controls.
 * Supports a pause button, a play forwards button, a play backwards button,
 * a jump forwards button, and a jump backwards button. The jump buttons
 * are used to jump one frame.
 * @author Sam Crow
 */
public class PlaybackControlModel implements CurrentFrameProvider {
    
    /**
     * If the pause button is enabled
     */
    private final BooleanProperty pauseEnabled = new SimpleBooleanProperty(true);
    /**
     * If the play forward button should be enabled
     */
    private final BooleanProperty playForwardEnabled = new SimpleBooleanProperty(true);
    /**
     * If the play backwards button should be enabled
     */
    private final BooleanProperty playBackwardsEnabled = new SimpleBooleanProperty(true);
    /**
     * If the jump forward button should be enabled
     */
    private final BooleanProperty jumpForwardEnabled = new SimpleBooleanProperty(true);
    /**
     * If the jump backwards button should be enabled
     */
    private final BooleanProperty jumpBackwardsEnabled = new SimpleBooleanProperty(true);
    
    
    /**
     * The frame that is currently displayed
     */
    private final ObjectProperty<Image> currentFrameImage = new SimpleObjectProperty<>();
    
    /**
     * The frame number that is currently displayed
     */
    private final IntegerProperty currentFrame = new SimpleIntegerProperty();
    
    
    public static enum State {
        Paused,
        PlayingBackwards,
        PlayingForward,
    }
    
    private final ObjectProperty<State> state = new SimpleObjectProperty<>(State.Paused);
    
    /**
     * The player used to play video
     */
    private FramePlayer player;
    
    /**
     * Loads frames
     */
    private final FrameFinder finder;
    
    public PlaybackControlModel(FrameFinder frameFinder) {
        this.finder = frameFinder;
        
        currentFrame.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int frame = newValue.intValue();
                if(frame < 1 || frame > getMaximumFrame()) {
                    throw new FrameIndexOutOfBoundsException(1, frame, getMaximumFrame());
                }
                
                currentFrameImage.set(finder.getImage(frame));
                
                //Disable backwards buttons if the first frame has been reached
                if(frame == 1) {
                    playBackwardsEnabled.set(false);
                    jumpBackwardsEnabled.set(false);
                    if(player != null) {
                        player.cancel();
                    }
                }
                else {
                    playBackwardsEnabled.set(true);
                    jumpBackwardsEnabled.set(true);
                }
                //Disable forwards buttons if the last frame has been reached
                if(frame == getMaximumFrame()) {
                    playForwardEnabled.set(false);
                    jumpForwardEnabled.set(false);
                    if(player == null) {
                        player.cancel();
                    }
                }
                else {
                    playForwardEnabled.set(true);
                    jumpForwardEnabled.set(true);
                }
            }
        });
        
        //Display the first frame
        currentFrame.set(1);
    }
    
    /**
     * @return if the pause button should be enabled
     */
    public final ReadOnlyBooleanProperty pauseEnabledProperty() {
        return pauseEnabled;
    }
    
    /**
     * @return if the play forward button should be enabled
     */
    public final ReadOnlyBooleanProperty playForwardEnabledProperty() {
        return playForwardEnabled;
    }
    /**
     * @return if the play backwards button should be enabled
     */
    public final ReadOnlyBooleanProperty playBackwardsEnabledProperty() {
        return playBackwardsEnabled;
    }
    /**
     * @return if the jump forward button should be enabled
     */
    public final ReadOnlyBooleanProperty jumpForwardEnabledProperty() {
        return jumpForwardEnabled;
    }
    /**
     * @return if the jump backwards button should be enabled
     */
    public final ReadOnlyBooleanProperty jumpBackwardsEnabledProperty() {
        return jumpBackwardsEnabled;
    }
    
    private void pauseButtonClicked() {
        //Stop the player
        if(player != null) {
            player.cancel();
            player = null;
        }
        //Enable all buttons except pause
        pauseEnabled.set(false);
        playForwardEnabled.set(true);
        playBackwardsEnabled.set(true);
        jumpForwardEnabled.set(true);
        jumpBackwardsEnabled.set(true);
        
        state.set(State.Paused);
    }
    
    private void playForwardButtonClicked() {
        //Cancel old player, if it exists
        if(player != null) {
            player.cancel();
        }
        //Set up player
        player = new ForwardFramePlayer(this);
        player.setOnCancelled(new Runnable() {
            @Override
            public void run() {
                pauseButtonClicked();
            }
        });
        new Timer("Frame player").schedule(player, 0, player.getMillisecondsBetweenFrames());
        
        pauseEnabled.set(true);
        //Disable all non-pause buttons
        playForwardEnabled.set(false);
        playBackwardsEnabled.set(false);
        jumpForwardEnabled.set(false);
        jumpBackwardsEnabled.set(false);
        
        state.set(State.PlayingForward);
    }
    
    private void playBackwardsButtonClicked() {
        //Cancel old player, if it exists
        if(player != null) {
            player.cancel();
        }
        //Set up player
        player = new BackwardsFramePlayer(this);
        player.setOnCancelled(new Runnable() {
            @Override
            public void run() {
                pauseButtonClicked();
            }
        });
        new Timer("Frame player").schedule(player, 0, player.getMillisecondsBetweenFrames());
        
        pauseEnabled.set(true);
        //Disable all non-pause buttons
        playForwardEnabled.set(false);
        playBackwardsEnabled.set(false);
        jumpForwardEnabled.set(false);
        jumpBackwardsEnabled.set(false);
        
        state.set(State.PlayingBackwards);
    }
    
    private void jumpForwardButtonClicked() {
        currentFrame.set(currentFrame.get() + 1);
    }
    
    private void jumpBackwardsButtonClicked() {
        currentFrame.set(currentFrame.get() - 1);
    }
    
    private final EventHandler<ActionEvent> pauseHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            pauseButtonClicked();
        }
    };
    
    public final EventHandler<ActionEvent> getPauseButtonHandler() {
        return pauseHandler;
    }
    
    private final EventHandler<ActionEvent> playForwardHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            playForwardButtonClicked();
        }
    };
    
    public final EventHandler<ActionEvent> getPlayForwardButtonHandler() {
        return playForwardHandler;
    }
    
    private final EventHandler<ActionEvent> playBackwardsHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            playBackwardsButtonClicked();
        }
    };
    
    public final EventHandler<ActionEvent> getPlayBackwardsButtonHandler() {
        return playBackwardsHandler;
    }
    
    
    private final EventHandler<ActionEvent> jumpForwardHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            jumpForwardButtonClicked();
        }
    };
    
    public final EventHandler<ActionEvent> getJumpForwardButtonHandler() {
        return jumpForwardHandler;
    }
    
    private final EventHandler<ActionEvent> jumpBackwardsHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            jumpBackwardsButtonClicked();
        }
    };
    
    public final EventHandler<ActionEvent> getJumpBackwardsButtonHandler() {
        return jumpBackwardsHandler;
    }
    
    public final IntegerProperty currentFrameProperty() {
        return currentFrame;
    }
    
    @Override
    public final int getCurrentFrame() {
        return currentFrame.get();
    }
    
    public final void setCurrentFrame(int newFrame) {
        currentFrame.set(newFrame);
    }
    
    public final int getMaximumFrame() {
        return finder.frameCount();
    }
    
    public final ReadOnlyObjectProperty<Image> currentFrameImageProperty() {
        return currentFrameImage;
    }
    
    public Image getCurrentFrameImage() {
        return currentFrameImage.get();
    }
    
    public final ReadOnlyObjectProperty<State> stateProperty() {
        return state;
    }
    
    public State getState() {
        return state.get();
    }
    
    public void setState(State newState) {
        switch(newState) {
            case Paused:
                pauseButtonClicked();
                break;
            case PlayingForward:
                if(playForwardEnabled.get()) {
                    playForwardButtonClicked();
                }
                else {
                    pauseButtonClicked();
                    playForwardButtonClicked();
                }
                break;
            case PlayingBackwards:
                if(playBackwardsEnabled.get()) {
                    playBackwardsButtonClicked();
                }
                else {
                    pauseButtonClicked();
                    playBackwardsButtonClicked();
                }
                break;
        }
        state.set(newState);
    }
    
    
    public static class FrameIndexOutOfBoundsException extends IndexOutOfBoundsException {
        
        /**
         * The lowest-numbered frame that can be accessed
         */
        private final int firstFrame;
        /**
         * The frame that was requested and that led to this exception being thrown
         */
        private final int requestedFrame;
        /**
         * The highest-numbered frame that can be accessed
         */
        private final int lastFrame;

        /**
         * Constructor
         * @param firstFrame The minimum valid frame index
         * @param requestedFrame The frame index that was requested
         * @param lastFrame The maximum valid frame index
         */
        public FrameIndexOutOfBoundsException(int firstFrame, int requestedFrame, int lastFrame) {
            this.requestedFrame = requestedFrame;
            this.firstFrame = firstFrame;
            this.lastFrame = lastFrame;
        }

        public int getFirstFrame() {
            return firstFrame;
        }

        public int getLastFrame() {
            return lastFrame;
        }

        public int getRequestedFrame() {
            return requestedFrame;
        }
    }
}
