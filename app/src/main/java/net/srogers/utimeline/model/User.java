package net.srogers.utimeline.model;

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

    /**
     * Gets the UTimelineEvent currently in "focus"
     *
     * @return UTimelineEvent
     */
    public UTimelineEvent getCurrentEvent() {
        //TODO: Add logic to determine actual current event, probably from TimelineActivity
        return timeline.getEvents().get(0);
    }
}