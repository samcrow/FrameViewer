package org.samcrow.frameviewer;

/**
 * Plays backwards
 * @author Sam Crow
 */
class BackwardsFramePlayer extends FramePlayer {

    public BackwardsFramePlayer(PlaybackControlModel model) {
        super(model);
    }

    public BackwardsFramePlayer(PlaybackControlModel model, double frameRate) {
        super(model, frameRate);
    }
    
    private boolean firstRunDone = false;

    @Override
    protected void traverseFrame() {
        PlaybackControlModel model = getModel();
        
        if(!firstRunDone) {
            firstRunDone = true;
            //Snap forward to the next frame that is a multiple of 10
            int nextFrame = model.getCurrentFrame();
            do {
                nextFrame--;
            }
            while (nextFrame % 10 != 0);
            
            if(nextFrame < 1) {
                model.setCurrentFrame(1);
                cancel();
                return;
            }
            
            model.setCurrentFrame(nextFrame);
        }
        else {

            int newFrame = model.getCurrentFrame() - FRAME_INCREMENT;

            if(newFrame <= 1) {
                model.setCurrentFrame(1);
                cancel();
                return;
            }


            model.setCurrentFrame(newFrame);
        }
    }
    
}
