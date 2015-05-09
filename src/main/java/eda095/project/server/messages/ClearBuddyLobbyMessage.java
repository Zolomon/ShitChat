package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;

public class ClearBuddyLobbyMessage extends LobbyMessage {
    public ClearBuddyLobbyMessage(String message) {
        super(message);
    }

    @Override
    public String messageToString() {
        return "/clear " + getMessage();
    }

    @Override
    public void process(Lobby lobby) {
        this.getConnection().outputMessage(this);
    }

}
