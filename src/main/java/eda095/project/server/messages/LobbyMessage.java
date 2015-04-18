package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyConnection;

public abstract class LobbyMessage {
    protected String message;

    private LobbyConnection connection;

    protected LobbyMessage() {

    }

    public LobbyMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String messageToString() {
        return message;
    }

    @Override
    public String toString() {
        return messageToString();
    }

    public LobbyConnection getConnection() {
        return connection;
    }

    public void setConnection(LobbyConnection connection) {
        this.connection = connection;
    }

    public abstract void process(Lobby lobby);
}
