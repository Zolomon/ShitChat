package eda095.project.server.lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zol on 01/04/15.
 */
public class LobbyClientState {
    private boolean isLoggedIn;
    private String username;
    private boolean isRunning;
    private ArrayList<String> channels;

    public LobbyClientState(String username) {
        this.username = username;
        channels = new ArrayList<>();
        channels.add("general");
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public synchronized void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getChannels() {
        return channels;
    }

    public boolean addChannel(String channel) {
        if (channels.contains(channel))
            return false;
        channels.add(channel);
        return true;
    }

    public boolean removeChannel(String channel) {
        if (!channels.contains(channel))
            return false;
        channels.remove(channel);
        return true;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}


