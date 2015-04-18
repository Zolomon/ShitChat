package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;

/**
 * Created by zol on 4/17/2015.
 */
public class ClientLobbyMessage extends LobbyMessage {

    public ClientLobbyMessage(String message) {
        this.message = message;
    }

    @Override
    public String messageToString() {
        return message;
    }

    @Override
    public void process(Lobby lobby) {
        throw new RuntimeException("Client messages should be parsed in lobby.sendInput() " +
                "and never exist anywhere else afterwards.");
    }
}
