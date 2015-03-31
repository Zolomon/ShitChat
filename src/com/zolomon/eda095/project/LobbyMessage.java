package com.zolomon.eda095.project;

/**
 * Created by 23060835 on 3/31/15.
 */
public abstract class LobbyMessage {
    protected String author;

    public LobbyMessage(String author) {
        this.author = author;
    }

    public abstract String getMessage();
}
