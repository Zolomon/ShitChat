package eda095.project.server.lobby;

/**
 * Created by 23060835 on 3/31/15.
 */
public class LobbyMessage {
    private final String author;
    private final String message;
    private LobbyConnection connection;

    public LobbyMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }

    /**
     * Get the message part
     * @return The message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the string representation of this message.
     * @return The message
     */
    public String toString() {
        return this.author + ": " + getMessage();
    }

    public LobbyConnection getConnection() {
        return connection;
    }

    public String getAuthor() {
        return author;
    }

    public void setConnection(LobbyConnection connection) {
        this.connection = connection;
    }
}
