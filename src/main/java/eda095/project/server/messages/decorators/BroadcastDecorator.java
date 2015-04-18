package eda095.project.server.messages.decorators;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyConnection;
import eda095.project.server.messages.LobbyMessage;

/**
 * Created by zol on 4/17/2015.
 */
public class BroadcastDecorator extends LobbyMessage {
    private LobbyMessage lobbyMessage;

    public BroadcastDecorator(LobbyMessage lobbyMessage) {
        this.lobbyMessage = lobbyMessage;
    }

    @Override
    public void process(Lobby lobby) {
        for (LobbyConnection con : lobby.getConnections()) {
            con.outputMessage(lobbyMessage);
        }
    }
}
