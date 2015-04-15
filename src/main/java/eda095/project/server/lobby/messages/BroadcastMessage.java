package eda095.project.server.lobby.messages;

public class BroadcastMessage extends Message {
    private final String author;
    private final String channel;

    public BroadcastMessage(String author, String channel, String message) {
        super(message);
        this.channel = channel;
        this.author = author;
    }

    public String messageToString() {
        return "/msg " + channel + " " + this.author + " " + getMessage();
    }
}
