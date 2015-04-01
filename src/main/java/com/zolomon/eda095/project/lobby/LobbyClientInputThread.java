package com.zolomon.eda095.project.lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zol on 3/31/2015.
 * <p>
 * The LobbyClientInputThread reads input from a socket and sends it to the Lobby for processing
 * <p>
 * TODO(zol): Change the structure so that chat message input is sent to the lobby and mouse input
 * TODO(zol): (clicks) is sent to the Session manager that tracks the world state.
 */
public class LobbyClientInputThread extends Thread {

    private final BufferedReader reader;
    private final LobbyConnection connection;

    public LobbyClientInputThread(LobbyConnection connection, InputStream inputStream) {
        this.connection = connection;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void run() {
        while (connection.isRunning()) {
            String line = "";
            try {
                line = reader.readLine();
                System.out.println("Read: " + line);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LobbyMessage message;
            if (connection.getState().isLoggedIn) {
                message = new LobbyMessage(connection.getState().username, line);
            } else {
                message = new LobbyMessage("", line);
            }
            connection.sendInput(message);
        }
    }
}
