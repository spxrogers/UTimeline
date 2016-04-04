package net.srogers.utimeline.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to represent the event data for UTimeline.
 */
public class UTimelineEvent implements Comparable<UTimelineEvent> {
    // instance variables
    private String title;
    private String description;
    private Date date;
    private List<UTimelineMedia> media;

    // default no arg constructor to make Java happy
    public UTimelineEvent() {
        this.title = "";
        this.description = "";
        this.date = null;
        this.media = null;
    }

    // contructors
    public UTimelineEvent(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
        media = new ArrayList<>();
    }

    // getters and setters
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public List<UTimelineMedia> getMedia() {
        return media;
    }
    public void setMedia(List<UTimelineMedia> media) {
        this.media = media;
    }
    public void addMedia(UTimelineMedia media) {
        this.media.add(media);
    }

    @Override
    public int compareTo(UTimelineEvent obj) {
        return date.compareTo(obj.getDate()) * -1;
    }
}