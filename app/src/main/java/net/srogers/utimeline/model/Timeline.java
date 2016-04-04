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
}
