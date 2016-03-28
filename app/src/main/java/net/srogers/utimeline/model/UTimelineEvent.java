package net.srogers.utimeline.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by steven on 3/28/16.
 */
public class UTimelineEvent implements Comparable<UTimelineEvent> {
    private String title;
    private String description;
    private Date date;
    private List<UTimelineMedia> media;

    public UTimelineEvent(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
        media = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(UTimelineEvent obj) {
        // todo: check date's compareTo. i suspect they use a lesser to greater date order. we'll likely have
        // todo cont: to return a negated value to get a greater to lesser ordering (we want most recent events shown most recent)
        return date.compareTo(obj.getDate());
    }
}
