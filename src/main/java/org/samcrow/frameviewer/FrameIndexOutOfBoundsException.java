package org.samcrow.frameviewer;

/**
 *
 * @author Sam Crow
 */
public class FrameIndexOutOfBoundsException extends IndexOutOfBoundsException {

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

    @Override
    public String getMessage() {
        return "Frame index out of bounds: Lowest allowed index "+firstFrame+", highest allowed index "+lastFrame+". Frame "+requestedFrame+" was requested.";
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
    
    
}
