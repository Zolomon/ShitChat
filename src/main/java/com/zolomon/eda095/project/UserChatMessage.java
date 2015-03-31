package com.zolomon.eda095.project;

/**
 * Created by zol on 4/1/2015.
 */
public class UserChatMessage extends LobbyMessage {
    private String message;

    public UserChatMessage(String author, String message) {
        super(author);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
