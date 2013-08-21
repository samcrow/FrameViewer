package org.samcrow.frameviewer;

/**
 * A frame player that plays forward
 * @author Sam Crow
 */
public class ForwardFramePlayer extends FramePlayer {

    private boolean firstRunDone = false;
    
    @Override
    protected void traverseFrame() {
        PlaybackControlModel model = getModel();
        
        if(!firstRunDone) {
            firstRunDone = true;
            //Snap forward to the next frame that is a multiple of 10
            int nextFrame = model.getCurrentFrame();
            do {
                nextFrame++;
            }
            while (nextFrame % 10 != 0);
            
            if(nextFrame > model.getMaximumFrame()) {
                model.setCurrentFrame(model.getMaximumFrame());
                cancel();
                return;
            }
            
            model.setCurrentFrame(nextFrame);
        }
        else {
            int nextFrame = model.getCurrentFrame() + FRAME_INCREMENT;

            if(nextFrame >= model.getMaximumFrame()) {
                model.setCurrentFrame(model.getMaximumFrame());
                cancel();
                return;
            }

            model.setCurrentFrame(nextFrame);
        }
    }

    public ForwardFramePlayer(PlaybackControlModel model, double frameRate) {
        super(model, frameRate);
    }

    public ForwardFramePlayer(PlaybackControlModel model) {
        super(model);
    }
    
}
