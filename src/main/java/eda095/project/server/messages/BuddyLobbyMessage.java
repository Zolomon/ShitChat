package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;

public class BuddyLobbyMessage extends LobbyMessage {
    public BuddyLobbyMessage(String message) {
        super(message);
    }

    @Override
    public String messageToString() {
        return "/buddy " + getMessage();
    }

    @Override
    public void process(Lobby lobby) {
        this.getConnection().outputMessage(this);
    }

}
