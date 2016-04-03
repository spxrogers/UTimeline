package net.srogers.utimeline.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Timeline class to hold logic about as user's personal UTimeline.
 */
public class Timeline {
    // instance variables
    private List<UTimelineEvent> events;

    // constructor
    public Timeline() {
        events = new ArrayList<>();
    }

    // getters and setters
    public List<UTimelineEvent> getEvents() {
        return events;
    }
    public void setEvents(List<UTimelineEvent> events) {
        this.events = events;
    }

    /**
     * Checks to see whether this Timeline has any events added to it.
     *
     * @return boolean, false if there are no UTimelineEvent(s) in events list, true otherwise
     */
    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public void addEvent(UTimelineEvent e) {
        events.add(e);
        Collections.sort(events);
    }
}
