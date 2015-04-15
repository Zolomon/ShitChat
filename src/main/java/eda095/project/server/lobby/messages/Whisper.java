package eda095.project.server.lobby.messages;

public class Whisper extends Message {
    private final String author;
    private final String recipient;

    public Whisper(String author, String recipient, String message) {
        super(message);
        this.author     = author;
        this.recipient  = recipient;
    }

    public boolean isAuthorOrRecipient(String username) {
        return username.equals(author) || username.equals(recipient);
    }

    public String messageToString() {
        return "/whisper " + this.author + " " +  this.recipient + " " + getMessage();
    }
}
