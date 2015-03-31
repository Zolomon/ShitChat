package com.zolomon.eda095.project;

import java.net.Socket;

/**
 * Created by 23060835 on 3/31/15.
 */
public class LobbyConnection {
    private final Socket socket;
    private String username;

    public LobbyConnection(Socket socket, Lobby lobby) {
        this.socket = socket;
    }

    public boolean login() {

        // TODO(zol): if new user, create account
        // TODO(zol): disconnect if login failed
        // TODO(zol): Set username to what it should be
        username = "Zolomon";
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void pushMsg(LobbyMessage message) {
        String msg = message.getMessage();
        // TODO(zol): Write message
    }
}
