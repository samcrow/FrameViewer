package org.samcrow.frameviewer.io3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.samcrow.frameviewer.AntId;
import org.samcrow.frameviewer.FrameDataStore;
import org.samcrow.frameviewer.io2.MarkerType;

/**
 * A frame data store that can read/write its contents to/from files
 * <p/>
 * @author Sam Crow
 * @param <T> The type of data to store. This must be an instance of
 * List&lt;Marker&gt;.
 */
public class PersistentFrameDataStore2to3 <T extends Marker> extends FrameDataStore<T> {

    /**
     * Throws an exception. This implementation does not support this method.
     * @param file The file to write to
     * @throws IOException
     */
    public void writeTo(File file) throws IOException {
        throw new UnsupportedOperationException("PersistentFrameDataStore2to3 is used to read version 2 files and cannot be used to write files.");
    }

    /**
     * Reads a data store from a file
     * <p/>
     * @param file The file to read from
     * @return an instance containing the data in the file
     * @throws IOException
     * @throws ParseException
     */
    public static PersistentFrameDataStore2to3<Marker> readFromFile(File file) throws IOException, ParseException {

        PersistentFrameDataStore2to3<Marker> instance = new PersistentFrameDataStore2to3<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            //Read and ignore header
            reader.readLine();

            final Pattern linePattern = Pattern.compile("(?<ant>\\d+),(?<antType>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<markerType>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<x>\\d+),(?<y>\\d+),(?<frame>\\d+)");

            //A pattern for the old line format, with no type specified
            final Pattern oldLinePattern = Pattern.compile("(?<frame>\\d+),(?<x>\\d+),(?<y>\\d+)");

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                Matcher matcher = linePattern.matcher(line);
                if (matcher.find()) {

                    try {

                        int frame = Integer.valueOf(matcher.group("frame"));
                        int x = Integer.valueOf(matcher.group("x"));
                        int y = Integer.valueOf(matcher.group("y"));

                        int antId = Integer.valueOf(matcher.group("ant"));
                        AntId.Type antType = AntId.Type.valueOfWithLegacySupport(matcher.group("antType"));
                        String typeName = matcher.group("markerType");

                        MarkerType type = MarkerType.valueOf(typeName);


                        Marker marker = markerFromV2Type(x, y, type);
                        marker.setAntId(antId);
                        marker.setFrame(frame);
                        //Find the existing list of markers for this frame
                        instance.getFrameData(frame).add(marker);

                    }
                    catch (IllegalArgumentException ex) {
                        ParseException parseEx = new ParseException("Unrecognized marker or ant type in line \"" + line + "\"", 0);
                        parseEx.initCause(ex);
                        throw parseEx;
                    }
                }
                else {
                    //Try to parse the old line format
                    Matcher oldFormatMatcher = oldLinePattern.matcher(line);
                    if (oldFormatMatcher.find()) {
                        int frame = Integer.valueOf(oldFormatMatcher.group("frame"));
                        int x = Integer.valueOf(oldFormatMatcher.group("x"));
                        int y = Integer.valueOf(oldFormatMatcher.group("y"));

                        //Use the default marker type
                        MarkerType type = MarkerType.getDefaultType();

                        Marker marker = markerFromV2Type(x, y, type);
                        marker.setAntId(0);
                        marker.setFrame(frame);
                        //Find the existing list of markers for this frame
                        instance.getFrameData(frame).add(marker);
                    }
                    else {
                        Logger.getLogger(PersistentFrameDataStore2to3.class.getName()).log(Level.WARNING, "Line \"{0}\" from file \"{1}\" could not be parsed", new Object[]{line, file.getAbsolutePath()});
                        throw new ParseException("Failed to parse line \"" + line + "\"", 0);
                    }
                }
            }

        }

        return instance;
    }

    
    private static Marker markerFromV2Type(int x, int y, MarkerType type) {
        switch(type) {
                // Interaction types
            case Returning:
                return new InteractionMarker(x, y, AntActivity.Unknown, AntLocation.Unknown, AntActivity.Unknown, AntLocation.ReturningToNest);
            case LeavingTunnel:
                return new InteractionMarker(x, y, AntActivity.Unknown, AntLocation.Unknown, AntActivity.Unknown, AntLocation.Ascending);
            case LeavingNest:
                return new InteractionMarker(x, y, AntActivity.Unknown, AntLocation.Unknown, AntActivity.Unknown, AntLocation.LeavingNest);
            case Carrying:
                return new InteractionMarker(x, y, AntActivity.Unknown, AntLocation.Unknown, AntActivity.CarryingUnkown, AntLocation.Unknown);
            case Standing:
                return new InteractionMarker(x, y, AntActivity.Unknown, AntLocation.Unknown, AntActivity.Unknown, AntLocation.ReturningToNest);
                
            case Unknown:
                return new Marker(x, y, AntActivity.Unknown, AntLocation.Unknown);
                
                // Non-interaction types
            case Tracking:
                return new Marker(x, y, AntActivity.Unknown, AntLocation.Unknown);
            case Tunnel:
                return new Marker(x, y, AntActivity.Unknown, AntLocation.AtTunnel);
            case Window:
                return new Marker(x, y, AntActivity.Unknown, AntLocation.AtExit);
            
            default:
                throw new IllegalArgumentException("Marker type "+type+" could not be converted to a version 3 marker");
        }
    }
}
