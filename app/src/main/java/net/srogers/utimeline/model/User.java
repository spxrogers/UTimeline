package net.srogers.utimeline.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by steven on 3/29/16.
 */
public class User extends RealmObject {
    // realm instance
    private static Realm realm = Realm.getDefaultInstance();

    // instance variables
    @Required
    private Timeline timeline;

    // constructor / generator
    private User() {
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
