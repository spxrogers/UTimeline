package net.srogers.utimeline.model;

/**
 * Created by steven on 3/28/16.
 */

enum MediaType {
    IMAGE,
    VIDEO;
}

// not sure if this will be needed in actual implementation. just brainstorming
// will have to look into how we can store files on a droid device
// and if we need a image/video type distinction on how to display specific media

public class UTimelineMedia {
    private MediaType type; // determine display logic
    private String location; // location of file saved on disk

    public UTimelineMedia() {
        type = MediaType.IMAGE;
        location = "";
    }
}
