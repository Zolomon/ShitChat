package eda095.project.server.messages;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyConnection;

public class WhisperLobbyMessage extends LobbyMessage {
    private final String author;
    private final String recipient;

    public WhisperLobbyMessage(String author, String recipient, String message) {
        super(message);
        this.author = author;
        this.recipient = recipient;
    }

    public boolean isAuthorOrRecipient(String username) {
        return username.equals(author) || username.equals(recipient);
    }

    @Override
    public String messageToString() {
        return "/whisper " + this.author + " " + this.recipient + " " + getMessage();
    }

    @Override
    public void process(Lobby lobby) {
        for (LobbyConnection con : lobby.getConnections()) {
            if (isAuthorOrRecipient(con.getState().getUsername())) {
                con.outputMessage(this);
            }
        }
    }
}
