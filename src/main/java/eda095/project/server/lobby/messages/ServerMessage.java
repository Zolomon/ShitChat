package eda095.project.server.lobby.messages;

public class ServerMessage extends Message{
    public ServerMessage(String message) {
        super(message);
    }

    public String messageToString() {
        return "/server " + getMessage();
    }
}
