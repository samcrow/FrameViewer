
package org.samcrow.frameviewer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.image.Image;

/**
 * Finds and caches frame images in a directory. Frames available in this API have indexes
 * that start with 1, consistent with the naming of frame images.
 * @author Sam Crow
 */
public class FrameFinder {
    
    private final File[] imageFiles;
    
    /**
     * The number of the first frame
     */
    private final int firstFrame;
    
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
        
        //Extract the frame number of the first frame
        firstFrame = extractFrameNumber(imageFiles[0].getName());
        
        cache = new Cache<>(imageFiles.length, new Cache.CacheSource<Image>() {
            @Override
            public Image load(int index) throws IOException {
                return FrameFinder.this.load(index);
            }
        });
    }
    
    /**
     * @return The highest frame number that is available
     */
    public int getMaximumFrame() {
        return firstFrame + imageFiles.length - 1;
    }
    
    /**
     * @return The lowest frame number that is available
     */
    public int getFirstFrame() {
        return firstFrame;
    }
    
    /**
     * Reads the requested frame from the cache or from the file system
     * and returns it
     * @param frameNumber The 1-based frame index to get
     * @return An image for the frame
     */
    public Image getImage(int frameNumber) {
        if(frameNumber < getFirstFrame() || frameNumber > getMaximumFrame()) {
            throw new FrameIndexOutOfBoundsException(getFirstFrame(), frameNumber, getMaximumFrame());
        }
        //Convert from 1-based to 0-based indexes
        int index = frameNumber - getFirstFrame();
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
    
    private static int extractFrameNumber(String fileName) {
        //Pattern that matches numbers and then a 1-4 character file extension
        //before the end of the input, case insensitive
        //with the numbers in group 1 
        Pattern pattern = Pattern.compile("(\\d+).[a-z]{1,4}\\z", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);
        if(!matcher.find()) {
            throw new IllegalArgumentException("File name "+fileName+" does not match the expected pattern");
        }
        return Integer.valueOf(matcher.group(1));
    }
}
