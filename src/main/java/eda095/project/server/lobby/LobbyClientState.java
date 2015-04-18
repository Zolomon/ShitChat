package eda095.project.server.lobby;

/**
 * Created by zol on 01/04/15.
 */
public class LobbyClientState {
    private boolean isLoggedIn;
    private String username;
    private boolean isRunning;

    public LobbyClientState(String username) {
        this.username = username;
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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
}
