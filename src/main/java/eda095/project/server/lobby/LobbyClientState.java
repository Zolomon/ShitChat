package eda095.project.server.lobby;

/**
 * Created by zol on 01/04/15.
 */
public class LobbyClientState {
    public boolean isLoggedIn;
    public String username;
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
}
