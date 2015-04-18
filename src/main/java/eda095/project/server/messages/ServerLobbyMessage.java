package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;

public class ServerLobbyMessage extends LobbyMessage {
    public ServerLobbyMessage(String message) {
        super(message);
    }

    @Override
    public String messageToString() {
        return "/server " + getMessage();
    }

    @Override
    public void process(Lobby lobby) {
        this.getConnection().outputMessage(this);
    }

}
