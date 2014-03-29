package org.samcrow.frameviewer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Stores data associated with frames. Each frame can have any number
 * of data objects associated with it.
 * <p/>
 * If this class is provided with a value that extends {@link FrameObject},
 * the value's frame will be set to match the frame at which it was added.
 * <p/>
 * This class is observable, so other classes can be notified when it changes.
 * This can be used to keep track of data save status.
 * <p/>
 * @param <T> The type of data to store.
 * @author Sam Crow
 */
public class FrameDataStore<T extends FrameObject> extends ObservableValueBase<FrameDataStore<T>> implements Iterable<List<T>> {

    private final List<List<T>> data = createList();

    /**
     * The current frame for which data is returned
     */
    private final IntegerProperty currentFrame = new SimpleIntegerProperty();

    /**
     * Makes a deep copy of another frame data store. Copies each
     * frame's list of markers. The markers themselves are not copied,
     * so this constructor does not allow the original to be
     * safely modified without affecting the new object.
     * @param other 
     */
    public FrameDataStore(FrameDataStore<? extends T> other) {
        int frame = 0;
        for(List<? extends T> frameMarkers : other.getList()) {
            setFrameData(frame, new LinkedList<>(frameMarkers));
            
            frame++;
        }
    }
    
    public FrameDataStore() {
        
    }
    
    /**
     *
     * @return The data associated with the current frame
     * @see #currentFrameProperty()
     */
    public final List<T> getCurrentFrameData() {
        final int frame = getCurrentFrame();

        return getFrameData(frame);
    }

    public final void setCurrentFrameData(List<T> value) {
        final int frame = getCurrentFrame();
        fillList(frame);

        setFrameData(frame, value);
    }

    public final void setFrameData(int frame, List<T> value) {
        fillList(frame);

        if (value instanceof FrameObject) {
            ((FrameObject) value).setFrame(frame);
        }


        List<T> oldValue = data.get(frame);
        if (!Objects.equals(oldValue, value)) {
            fireValueChangedEvent();
        }

        data.set(frame, value);
    }

    public List<T> getFrameData(int frame) {
        fillList(frame);

        List<T> list = data.get(frame);
        if (list == null) {
            list = createList();
            data.set(frame, list);
        }
        return list;
    }

    /**
     * Fills the list to ensure that the list has a value for the given index.
     * Null values will be inserted as necessary.
     * <p/>
     * @param lastIndex The index to ensure a value for
     */
    private void fillList(int lastIndex) {
        while (data.size() < lastIndex + 1) {
            data.add(null);
        }
    }

    public final IntegerProperty currentFrameProperty() {
        return currentFrame;
    }

    public final int getCurrentFrame() {
        return currentFrame.get();
    }

    public final void setCurrentFrame(int newFrame) {
        currentFrame.set(newFrame);
    }

    /**
     * An invalidation listener that invalidates this data store
     */
    private InvalidationListener invalidationListener;

    /**
     * Creates a list and configures it to invalidate this data store when it
     * changes
     * <p/>
     * @param <T2> The type of element to store in the list
     * @return
     */
    private <T2> List<T2> createList() {
        ObservableList<T2> list = FXCollections.observableList(new LinkedList<T2>());

        if (invalidationListener == null) {
            invalidationListener = new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    fireValueChangedEvent();
                }
            };
        }
        list.addListener(invalidationListener);

        return list;
    }

    /**
     * Returns an iterator over all the lists of data for which this data
     * store has a value. This iterator supports all the optional operations.
     * <p/>
     * @return an iterator
     */
    @Override
    public Iterator<List<T>> iterator() {
        return new Iterator<List<T>>() {
            private final Iterator<List<T>> dataIterator = data.iterator();

            private List<T> next;

            /**
             * The index of the element last retrieved from the data iterator
             */
            private int index = -1;

            @Override
            public boolean hasNext() {
                findNext();
                return next != null;
            }

            @Override
            public List<T> next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                return next;
            }

            @Override
            public void remove() {
                data.set(index, null);
            }

            /**
             * Uses the list iterator to find the next non-null value
             */
            private void findNext() {
                while (dataIterator.hasNext()) {
                    List<T> value = dataIterator.next();
                    index++;
                    if (value != null) {
                        next = value;
                        return;
                    }
                }
                //Next remains null if no value was found
                next = null;
            }
        };
    }

    @Override
    public FrameDataStore getValue() {
        return this;
    }

    /**
     * 
     * @return The sets of data that this data store contains
     */
    protected List<List<T>> getList() {
        return data;
    }
    

}
