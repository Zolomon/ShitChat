package eda095.project.shared;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import eda095.project.server.lobby.database.DatabaseStore;

/**
 * Created by zol on 4/13/2015.
 */
@DatabaseTable(tableName = "accounts")
public class Account {
    public static final String ID_FIELD = "id";
    public static final String USERNAME_FIELD = "username";

    @DatabaseField(columnName = ID_FIELD, generatedId = true)
    private int id;

    @DatabaseField(columnName = USERNAME_FIELD, canBeNull = false, unique = true)
    private String username;

    Account() {

    }

    public Account(String username) {
        this.username = username;
    }

    public static Account create(DatabaseStore db, String username) {
        Account account = new Account(username);
        int updatedRows = db.create(account);
        if (updatedRows == 0) {
            return null;
        }
        return account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("[%d, %s]", id, username);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
