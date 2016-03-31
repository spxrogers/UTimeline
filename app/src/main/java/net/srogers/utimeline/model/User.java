package net.srogers.utimeline.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * User class to represent the UTimeline app user.
 */
public class User extends RealmObject {
    // realm instance
    private static Realm realm = Realm.getDefaultInstance();

    // instance variables
    @Required
    private Timeline timeline;

    // constructor / generator
    public User() {
        timeline = new Timeline();
    }

    private static User generateUser() {
        realm.beginTransaction();
        User user = realm.createObject(User.class); // create the new object tied to realm
        realm.commitTransaction();
        return user;
    }

    // getters and setters
    public Timeline getTimeline() {
        return timeline;
    }
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public static User getCurrentUser() {
        User user = realm.where(User.class).findFirst();
        return (user != null) ? user : User.generateUser();
    }

    public void saveUser() {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
    }
}
