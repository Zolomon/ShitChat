package eda095.project.server.messages.decorators;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.messages.LobbyMessage;

import java.util.List;

/**
 * Created by zol on 4/17/2015.
 */
public class SequentialListDecorator extends LobbyMessage {
    private List<LobbyMessage> messages;

    public SequentialListDecorator(List<LobbyMessage> messages) {
        this.messages = messages;
    }

    @Override
    public void process(Lobby lobby) {
        for (LobbyMessage message : messages) {
            message.setConnection(this.getConnection());
            message.getConnection().outputMessage(message);
        }
    }
}
