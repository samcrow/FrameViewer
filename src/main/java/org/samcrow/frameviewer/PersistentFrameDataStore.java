package org.samcrow.frameviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A frame data store that can read/write its contents to/from files
 * @author Sam Crow
 * @param <T> The type of data to store. This must be an instance of List&lt;Marker&gt;.
 */
public class PersistentFrameDataStore<T extends Marker> extends FrameDataStore<T> {
    
    /**
     * Writes this data store to a CSV file
     * @param file The file to write to
     * @throws IOException 
     */
    public void writeTo(File file) throws IOException {
        
        if(!file.exists()) {
            boolean success = file.createNewFile();
            if(!success) {
                throw new IOException("Could not create file "+file.getAbsolutePath());
            }
        }
        if(!file.canWrite()) {
            throw new IOException("Can't write to file "+file.getAbsolutePath());
        }
        
        try ( PrintStream out = new PrintStream(new FileOutputStream(file)) ) {
            
            //Add heading
            out.println("Frame,X,Y,Type");
            
            for(List<T> markerList : this) {
                for(T marker :  markerList) {
                    out.print(marker.getFrame());
                    out.print(',');
                    out.print(marker.getX());
                    out.print(',');
                    out.print(marker.getY());
                    out.print(',');
                    out.print(marker.getType().name());
                    out.println();
                }
            }
            
        }
        
    }
    
    /**
     * Reads a data store from a file
     * @param file The file to read from
     * @return an instance containing the data in the file
     * @throws IOException
     * @throws ParseException
     */
    public static  PersistentFrameDataStore<Marker> readFromFile(File file) throws IOException, ParseException {
        
        PersistentFrameDataStore<Marker> instance = new PersistentFrameDataStore<>();
        
        try( BufferedReader reader = new BufferedReader(new FileReader(file)) ) {
            
            //Read and ignore header
            reader.readLine();
            
            final Pattern linePattern = Pattern.compile("(?<frame>\\d+),(?<x>\\d+),(?<y>\\d+),(?<type>[a-zA-Z_$][a-zA-Z\\d_$]*)");
            
            //A pattern for the old line format, with no type specified
            final Pattern oldLinePattern = Pattern.compile("(?<frame>\\d+),(?<x>\\d+),(?<y>\\d+)");
            
            while(true) {
                String line = reader.readLine();
                if(line == null) {
                    break;
                }
                
                Matcher matcher = linePattern.matcher(line);
                if(matcher.find()) {
                    
                    try {
                    
                    int frame = Integer.valueOf(matcher.group("frame"));
                    int x = Integer.valueOf(matcher.group("x"));
                    int y = Integer.valueOf(matcher.group("y"));
                    
                    String typeName = matcher.group("type");
                    
                    MarkerType type = MarkerType.valueOf(typeName);
                    
                    
                    Marker marker = type.buildMarker(x, y);
                    //Find the existing list of markers for this frame
                    instance.getFrameData(frame).add(marker);
                    
                    }
                    catch (IllegalArgumentException ex) {
                        ParseException parseEx = new ParseException("Unrecognized marker type in line \""+line+"\"", 0);
                        parseEx.initCause(ex);
                        throw parseEx;
                    }
                }
                else {
                    //Try to parse the old line format
                    Matcher oldFormatMatcher = oldLinePattern.matcher(line);
                    if(oldFormatMatcher.find()) {
                        int frame = Integer.valueOf(matcher.group("frame"));
                        int x = Integer.valueOf(matcher.group("x"));
                        int y = Integer.valueOf(matcher.group("y"));

                        //Use the default marker type
                        MarkerType type = MarkerType.getDefaultType();

                        Marker marker = type.buildMarker(x, y);
                        //Find the existing list of markers for this frame
                        instance.getFrameData(frame).add(marker);
                    }
                    else {
                        Logger.getLogger(PersistentFrameDataStore.class.getName()).log(Level.WARNING, "Line \"{0}\" from file \"{1}\" could not be parsed", new Object[]{line, file.getAbsolutePath()});
                        throw new ParseException("Failed to parse line \""+line+"\"", 0);
                    }
                }
            }
            
        }
        
        return instance;
    }
    
}
