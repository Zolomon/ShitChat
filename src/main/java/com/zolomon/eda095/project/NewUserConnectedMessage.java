package com.zolomon.eda095.project;

/**
 * Created by 23060835 on 3/31/15.
 */
public class NewUserConnectedMessage extends LobbyMessage {
    private LobbyConnection connection;

    public NewUserConnectedMessage(String author, LobbyConnection connection) {
        super(author);
        this.connection = connection;
    }

    @Override
    public String getMessage() {
        return String.format("%s: %s has connected.", author, connection.getUsername());
    }
}
