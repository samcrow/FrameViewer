package org.samcrow.frameviewer.ui;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.samcrow.frameviewer.CurrentFrameProvider;

/**
 * Records clicks on the image
 * @author Sam Crow
 */
public class ClickRecordingView extends ImageView {
    
    private final CurrentFrameProvider provider;

    public ClickRecordingView(CurrentFrameProvider provider) {
        this.provider = provider;
    }

    public ClickRecordingView(CurrentFrameProvider provider, String string) {
        super(string);
        this.provider = provider;
    }

    public ClickRecordingView(CurrentFrameProvider provider, Image image) {
        super(image);
        this.provider = provider;
    }
    
    {
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Get click information
                int x = (int) Math.round(event.getX());
                int y = (int) Math.round(event.getY());
                int frame = provider.getCurrentFrame();
                
                System.out.println("Clicked during frame "+frame+" at ("+x+", "+y+")");
            }
        });
    }
    
    
    
}
