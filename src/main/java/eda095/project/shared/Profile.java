package eda095.project.shared;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import eda095.project.server.lobby.database.DatabaseStore;

/**
 * Created by zol on 4/13/2015.
 */
@DatabaseTable(tableName = "Profile")
public class Profile {
    public static final String ACCOUNTS_ID_FIELD_NAME = "accounts_id";

    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(foreign = true, columnName = ACCOUNTS_ID_FIELD_NAME)
    public Account account;
    @DatabaseField(columnName = "title", canBeNull = false, defaultValue = "")
    public String title;
    @DatabaseField(columnName = "location", canBeNull = false, defaultValue = "")
    public String location;
    @DatabaseField(columnName = "avatar_url", canBeNull = false, defaultValue = "")
    public String avatar_uri;

    Profile() {
        // package constructor
    }

    public Profile(Account account, String title, String location, String avatar_uri) {
        this.account = account;
        this.title = title;
        this.location = location;
        this.avatar_uri = avatar_uri;
    }

    public static Profile create(DatabaseStore db, String username, String title, String location, String avatar_uri) {
        Account account = db.getAccount(username);
        Profile profile = new Profile(account, title, location, avatar_uri);
        int updatedRows = db.create(profile);
        if (updatedRows == 0) {
            return null;
        }
        return profile;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar_uri() {
        return avatar_uri;
    }

    public void setAvatar_uri(String avatar_uri) {
        this.avatar_uri = avatar_uri;
    }

    @Override
    public String toString() {
        return String.format("[%d, %s, %s, %s]", id, title, location, avatar_uri);
    }
}
