package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyConnection;

/**
 * Created by zol on 4/17/2015.
 */
public class ChatLobbyMessage extends LobbyMessage {
    private final String username;
    private final String channel;

    public ChatLobbyMessage(String username, String channel, String message) {
        super(message);
        this.username = username;
        this.channel = channel;
    }

    @Override
    public void process(Lobby lobby) {
        for (LobbyConnection conn : lobby.getConnections())
            if (conn.getState().getChannels().contains(channel))
                conn.outputMessage(this);
    }

    public String getUsername() {
        return username;
    }

    public String getChannel() {
        return channel;
    }

    public String messageToString() {
        return "/msg " + getChannel() + " " + getUsername() + " " + getMessage();
    }
}
