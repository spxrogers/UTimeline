package net.srogers.utimeline.model;

import java.util.Collections;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Timeline class to hold logic about as user's personal UTimeline.
 */
public class Timeline extends RealmObject {
    // instance variables
    private RealmList<UTimelineEvent> events;

    // constructor
    public Timeline() {
        events = new RealmList<>();
    }

    // getters and setters
    public RealmList<UTimelineEvent> getEvents() {
        return events;
    }
    public void setEvents(RealmList<UTimelineEvent> events) {
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
