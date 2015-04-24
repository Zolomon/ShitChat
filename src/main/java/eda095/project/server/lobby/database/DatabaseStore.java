package eda095.project.server.lobby.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import eda095.project.shared.Account;
import eda095.project.shared.Profile;

import java.sql.*;
import java.util.List;

/**
 * Created by zol on 4/13/2015.
 */
public class DatabaseStore {

    private static DatabaseStore instance;
    private final String dbSrc = "jdbc:sqlite:server.db";
    private ConnectionSource source = null;
    private Dao<Account, Integer> accountDao;
    private Dao<Profile, Integer> profileDao;

    private DatabaseStore() {

    }

    public static DatabaseStore getInstance() {
        if (instance == null) {
            instance = new DatabaseStore();
        }

        return instance;
    }

    public void load() {
        try {
            source = new JdbcConnectionSource(dbSrc);

            accountDao = DaoManager.createDao(source, Account.class);
            TableUtils.createTableIfNotExists(source, Account.class);

            profileDao = DaoManager.createDao(source, Profile.class);
            TableUtils.createTableIfNotExists(source, Profile.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (source != null) {
            try {
                source.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This will create the account and <strong>UPDATE THE PRIMARY KEY ID:</strong> {@code id} field
     * to its value in the database.
     *
     * @param account Account to update
     * @return How many rows were updated
     */
    public int create(Account account) {
        try {
            System.out.println("Creating: " + account);
            return accountDao.create(account);
        } catch (SQLException e) {
            //e.printStackTrace();
            return 0;
        }
    }

    public int update(Account account) {
        try {
            return accountDao.update(account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Account getAccount(String username) {
        QueryBuilder<Account, Integer> qb = accountDao.queryBuilder();
        try {
            qb.where().eq(Account.USERNAME_FIELD, username);
            List<Account> accounts = accountDao.query(qb.prepare());

            if (accounts.size() == 0) {
                return null;
            }

            return accounts.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int delete(Account account) {
        try {
            return accountDao.delete(account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * This will create the profile and <strong>UPDATE THE PRIMARY KEY ID:</strong> {@code id} field
     * to its value in the database.
     *
     * @param profile Profile to update
     * @return How many rows were updated
     */
    public int create(Profile profile) {
        try {
            return profileDao.create(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete(Profile profile) {
        try {
            return profileDao.delete(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int update(Profile profile) {
        try {
            return profileDao.update(profile);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Profile getProfile(String username) {
        QueryBuilder<Profile, Integer> qb = profileDao.queryBuilder();
        try {
            qb.where().eq(Profile.ACCOUNTS_ID_FIELD_NAME, getAccount(username));
            List<Profile> profiles = profileDao.query(qb.prepare());

            if (profiles.size() == 0) {
                return null;
            }

            return profiles.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
