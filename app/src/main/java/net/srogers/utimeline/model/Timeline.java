package net.srogers.utimeline.model;

import android.app.Activity;
import android.os.Bundle;

import net.srogers.utimeline.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Timeline class to hold logic about as user's personal UTimeline.
 */
public class Timeline {
    private List<UTimelineEvent> events;

    public Timeline() {
        events = new ArrayList<>();
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
//        Collections.sort(events); // auto-update sorted order or nah?
    }
}
