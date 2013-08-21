package org.samcrow.frameviewer;

import java.io.File;
import java.io.FileFilter;

/**
 * Finds JPEG image files. Checks extensions.
 * @author Sam Crow
 */
class JpegFilter implements FileFilter {


    @Override
    public boolean accept(File file) {
        String extension = getExtension(file);
        if (!(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))) {
            //Not a jpg or jpeg -extensioned file
            return false;
        }
        return true;
    }

    private String getExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            //No dot in the name
            return "";
        }
        if (dotIndex == name.length() - 1) {
            //Dot is last character in the name
            return "";
        }
        //Dot in the name
        return name.substring(dotIndex + 1);
    }
    
}
