package org.samcrow.frameviewer.io3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.samcrow.frameviewer.FrameDataStore;

/**
 * A frame data store that can read/write its contents to/from files
 * <p/>
 * @author Sam Crow
 * @param <T> The type of data to store. This must be an instance of
 * List&lt;Marker&gt;.
 */
public class PersistentFrameDataStore<T extends Marker> extends FrameDataStore<T> {

    /**
     * Writes this data store to a CSV file
     * <p/>
     * @param file The file to write to
     * @throws IOException
     */
    public void writeTo(File file) throws IOException {

        if (!file.exists()) {
            boolean success = file.createNewFile();
            if (!success) {
                throw new IOException("Could not create file " + file.getAbsolutePath());
            }
        }
        if (!file.canWrite()) {
            throw new IOException("Can't write to file " + file.getAbsolutePath());
        }

        //Extract an array of markers
        //Move all the sublists out into a new, outer, list
        final List<T> outerList = new LinkedList<>();
        final List<List<T>> composite = getList();
        for (List<T> subList : composite) {
            if (subList != null) {
                for (T value : subList) {
                    outerList.add(value);
                }
            }
        }
        //Sort the entries in the outer list by colony ID
        Collections.sort(outerList, new Comparator<Marker>() {
            @Override
            public int compare(Marker marker1, Marker marker2) {
                if (marker1.getAntId() < marker2.getAntId()) {
                    return -1;
                }
                else if (marker1.getAntId() > marker2.getAntId()) {
                    return 1;
                }
                else {
                    //Ant IDs are the same; now compare frames
                    if (marker1.getFrame() < marker2.getFrame()) {
                        return -1;
                    }
                    else if (marker1.getFrame() > marker2.getFrame()) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            }
        });

        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {

            // Add version number
            out.println("File version,3");
            //Add heading
            out.println(Marker.fileHeader());

            for (T marker : outerList) {
                out.println(marker.toCSVLine());
            }

        }

    }

    /**
     * Reads a data store from a file
     * <p/>
     * @param file The file to read from
     * @return an instance containing the data in the file
     * @throws IOException
     * @throws ParseException
     */
    public static PersistentFrameDataStore<Marker> readFromFile(File file) throws IOException, ParseException {

        PersistentFrameDataStore<Marker> instance = new PersistentFrameDataStore<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            //Read and interpret version line
            try {
                int version = getVersion(reader.readLine());

                // Check version
                if (version != 3) {
                    throw new ParseException("Invalid version number " + version, 0);
                }
            }
            catch (IllegalArgumentException ex) {
                // That wasn't a version line, it was a header line!
                // Use the old parser
                reader.close();
                return new PersistentFrameDataStore<>(PersistentFrameDataStore2to3.readFromFile(file));
            }

            // Read and ignore header
            reader.readLine();

            final Pattern linePattern = Pattern.compile("^(?<ant>\\d+),(?<frame>\\d+),(?<x>\\d+),(?<y>\\d+),(?<focusAntActivity>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<focusAntLocation>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<interactionType>[a-zA-Z_$][a-zA-Z\\d_$]*)?,(?<trackedAntActivity>[a-zA-Z_$][a-zA-Z\\d_$]*)?,(?<trackedAntLocation>[a-zA-Z_$][a-zA-Z\\d_$]*)?,(?<metAntId>\\d+)?$");

            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                Matcher matcher = linePattern.matcher(line);
                if (matcher.find()) {

                    try {

                        int antId = Integer.parseInt(matcher.group("ant"));
                        int frame = Integer.parseInt(matcher.group("frame"));
                        int x = Integer.parseInt(matcher.group("x"));
                        int y = Integer.parseInt(matcher.group("y"));

                        AntActivity focusAntActivity = AntActivity.valueOf(matcher.group("focusAntActivity"));
                        AntLocation focusAntLocation = AntLocation.valueOf(matcher.group("focusAntLocation"));

                        Marker marker;
                        // Check for tracked ant activity and location
                        String trackedAntActivityString = matcher.group("trackedAntActivity");
                        if (trackedAntActivityString != null && !trackedAntActivityString.isEmpty()) {
                            // Marker is an interaction marker
                            AntActivity trackedAntActivity = AntActivity.valueOf(trackedAntActivityString);
                            AntLocation trackedAntLocation = AntLocation.valueOf(matcher.group("trackedAntLocation"));
                            InteractionMarker.InteractionType type = InteractionMarker.InteractionType.valueOf(matcher.group("interactionType"));

                            InteractionMarker interactionMarker = new InteractionMarker(x, y, focusAntActivity, focusAntLocation, trackedAntActivity, trackedAntLocation);

                            interactionMarker.setType(type);
                            interactionMarker.setMetAntId(Integer.parseInt(matcher.group("metAntId")));

                            marker = interactionMarker;
                        }
                        else {
                            // Not an interaction marker
                            marker = new Marker(x, y, focusAntActivity, focusAntLocation);
                        }

                        marker.setAntId(antId);
                        marker.setFrame(frame);

                        instance.getFrameData(frame).add(marker);

                    }
                    catch (IllegalArgumentException ex) {
                        ParseException parseEx = new ParseException("Unrecognized marker or in line \"" + line + "\"", 0);
                        parseEx.initCause(ex);
                        throw parseEx;
                    }
                }
                else {
                    // No match. Try the old regex, which does not include the met ant ID
                    final Pattern oldLinePattern = Pattern.compile("^(?<ant>\\d+),(?<frame>\\d+),(?<x>\\d+),(?<y>\\d+),(?<focusAntActivity>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<focusAntLocation>[a-zA-Z_$][a-zA-Z\\d_$]*),(?<interactionType>[a-zA-Z_$][a-zA-Z\\d_$]*)*,(?<trackedAntActivity>[a-zA-Z_$][a-zA-Z\\d_$]*)*,(?<trackedAntLocation>[a-zA-Z_$][a-zA-Z\\d_$]*)*$");

                    Matcher oldMatcher = oldLinePattern.matcher(line);
                    if (oldMatcher.matches()) {

                        try {

                            int antId = Integer.valueOf(oldMatcher.group("ant"));
                            int frame = Integer.valueOf(oldMatcher.group("frame"));
                            int x = Integer.valueOf(oldMatcher.group("x"));
                            int y = Integer.valueOf(oldMatcher.group("y"));

                            AntActivity focusAntActivity = AntActivity.valueOf(oldMatcher.group("focusAntActivity"));
                            AntLocation focusAntLocation = AntLocation.valueOf(oldMatcher.group("focusAntLocation"));

                            Marker marker;
                            // Check for tracked ant activity and location
                            String trackedAntActivityString = oldMatcher.group("trackedAntActivity");
                            if (trackedAntActivityString != null && !trackedAntActivityString.isEmpty()) {
                                // Marker is an interaction marker
                                AntActivity trackedAntActivity = AntActivity.valueOf(trackedAntActivityString);
                                AntLocation trackedAntLocation = AntLocation.valueOf(oldMatcher.group("trackedAntLocation"));
                                InteractionMarker.InteractionType type = InteractionMarker.InteractionType.valueOf(oldMatcher.group("interactionType"));

                                InteractionMarker interactionMarker = new InteractionMarker(x, y, focusAntActivity, focusAntLocation, trackedAntActivity, trackedAntLocation);

                                interactionMarker.setType(type);

                                marker = interactionMarker;
                            }
                            else {
                                // Not an interaction marker
                                marker = new Marker(x, y, focusAntActivity, focusAntLocation);
                            }

                            marker.setAntId(antId);
                            marker.setFrame(frame);

                            instance.getFrameData(frame).add(marker);

                        }
                        catch (IllegalArgumentException ex) {
                            ParseException parseEx = new ParseException("Unrecognized marker or in line \"" + line + "\"", 0);
                            parseEx.initCause(ex);
                            throw parseEx;
                        }

                    }
                    else {
                        throw new ParseException("Unrecognized line \"" + line + "\"", 0);
                    }
                }
            }

        }

        return instance;
    }

    private static int getVersion(String versionLine) {
        final Pattern versionPattern = Pattern.compile("File version,\\s*([0-9]+)");
        Matcher match = versionPattern.matcher(versionLine);
        if (!match.find() || match.groupCount() != 1) {
            throw new IllegalArgumentException("Version line \"" + versionLine + "\" did not match the expected format");
        }

        return Integer.valueOf(match.group(1));
    }

    public PersistentFrameDataStore() {

    }

    /**
     * Copies the markers from another frame data store.
     * <p>
     * @param other
     */
    public PersistentFrameDataStore(PersistentFrameDataStore2to3<T> other) {
        super(other);
    }

}
