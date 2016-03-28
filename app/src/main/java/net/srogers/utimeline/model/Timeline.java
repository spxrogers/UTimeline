package net.srogers.utimeline.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steven on 3/28/16.
 */
public class Timeline {
    private List<UTimelineEvent> events;

    public Timeline() {
        events = new ArrayList<>();
    }

    public void addEvent(UTimelineEvent e) {
        events.add(e);
//        Collections.sort(events); // auto-update sorted order or nah?
    }
}
