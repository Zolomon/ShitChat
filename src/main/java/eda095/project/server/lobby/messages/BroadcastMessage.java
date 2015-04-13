package eda095.project.server.lobby.messages;

public class BroadcastMessage extends Message {
    private final String author;

    public BroadcastMessage(String author, String message) {
        super(message);
        this.author = author;
    }

    public String messageToString() {
        return "/msg general " + this.author + " " + getMessage();
    }
}
