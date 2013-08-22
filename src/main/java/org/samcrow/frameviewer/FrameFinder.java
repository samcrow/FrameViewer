
package org.samcrow.frameviewer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javafx.scene.image.Image;

/**
 * Finds and caches frame images in a directory. Frames available in this API have indexes
 * that start with 1, consistent with the naming of frame images.
 * @author Sam Crow
 */
public class FrameFinder {
    
    private final File[] imageFiles;
    
    /**
     * Many cached images
     */
    private final Cache<Integer, Image> cache;

    public FrameFinder(File frameDir) {
        if(frameDir.exists() && !frameDir.isDirectory()) {
            throw new IllegalArgumentException("The provided File "+frameDir+" must be a directory");
        }
        
        imageFiles = frameDir.listFiles(new JpegFilter());
        
        if(imageFiles.length < 1) {
            throw new IllegalArgumentException("No JPEG image files exist in the provided directory");
        }
        //Sort images in lexographical (alphabetical) order
        Arrays.sort(imageFiles);
        
        cache = new Cache<>(imageFiles.length, new Cache.CacheSource<Image>() {
            @Override
            public Image load(int index) throws IOException {
                return FrameFinder.this.load(index);
            }
        });
    }
    
    /**
     * @return The number of images available
     */
    public int frameCount() {
        return imageFiles.length;
    }
    
    /**
     * Reads the requested frame from the cache or from the file system
     * and returns it
     * @param frameNumber The 1-based frame index to get
     * @return An image for the frame
     */
    public Image getImage(int frameNumber) {
        //Convert from 1-based to 0-based indexes
        int index = frameNumber - 1;
        return cache.get(index);
    }

    
    private Image load(int index) throws IOException {
        String uri = imageFiles[index].toURI().toString();
        Image image = new Image(uri);
        if(image.isError()) {
            throw new IOException("Image for frame "+ (index + 1) +" could not be loaded");
        }
        
        return image;
    }
}
