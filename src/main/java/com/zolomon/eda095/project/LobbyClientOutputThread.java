package com.zolomon.eda095.project;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by zol on 3/31/2015.
 */
public class LobbyClientOutputThread extends Thread {

    private final PrintWriter writer;
    private LobbyConnection connection;

    public LobbyClientOutputThread(LobbyConnection connection, OutputStream outputStream) {
        this.connection = connection;
        this.writer = new PrintWriter(outputStream);
    }

    @Override
    public void run() {
        Lobby lobby = connection.getLobby();
        while (connection.isRunning()) {

        }
    }
}
