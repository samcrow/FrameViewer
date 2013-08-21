package org.samcrow.frameviewer;

/**
 * An interface for something that can provide the currently displayed video frame
 * @author Sam Crow
 */
public interface CurrentFrameProvider {

    /**
     * 
     * @return The frame number that is currently displayed
     */
    int getCurrentFrame();
    
}
