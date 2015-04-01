package com.zolomon.eda095.project;

/**
 * Created by 23060835 on 3/31/15.
 */
public abstract class LobbyMessage {
    protected String author;

    public LobbyMessage(String author) {
        this.author = author;
    }

    /**
     * Get the message part
     * @return The message
     */
    public abstract String getMessage();

    /**
     * Returns the string representation of this message.
     * @return The message
     */
    public String toString() {
        return this.author + ": " + getMessage();
    }
}
