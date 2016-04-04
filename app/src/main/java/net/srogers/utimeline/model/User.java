package net.srogers.utimeline.model;

import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;

/**
 * User class to represent the UTimeline app user.
 */
public class User {
    // instance variables
    private Timeline timeline;

    // constructor / generator
    public User() {
        timeline = new Timeline();
    }

    // getters and setters
    public Timeline getTimeline() {
        return timeline;
    }
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public static User getCurrentUser() {
        User user = Paper.book().read("current_user");
        return (user != null) ? user : new User();
    }

    public void saveUser() {
        Paper.book().write("current_user", this);
    }

    public List<UTimelineEvent> getEvents() {
        return timeline.getEvents();
    }

    /**
     * Attemtps to get the event at the index requested
     *
     * @param eventIndex
     * @return UTimeline event, or null if index is invalid
     */
    public UTimelineEvent getEvent(int eventIndex) {
        List<UTimelineEvent> events = timeline.getEvents();
        if (eventIndex < events.size()) {
            return timeline.getEvents().get(eventIndex);
        }
        return null;
    }

    /**
     * Deletes the UTimelineEvent at the given eventIndex
     *
     * @param eventIndex
     */
    public void deleteEvent(int eventIndex) {
        List<UTimelineEvent> events = timeline.getEvents();
        if (eventIndex < events.size()) {
            events.remove(eventIndex);
        }
    }

    public boolean hasEvents() {
        return !getEvents().isEmpty();
    }

    public void addEvent(UTimelineEvent event) {
        timeline.getEvents().add(event);
        Collections.sort(timeline.getEvents());
    }
}