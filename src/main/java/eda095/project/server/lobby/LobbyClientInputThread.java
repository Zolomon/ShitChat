package eda095.project.server.lobby;

import eda095.project.server.messages.ClientLobbyMessage;
import eda095.project.server.messages.LobbyMessage;

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
        while (connection.getState().isRunning()) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    // Line is null when the user has disconnected
                    reader.close();
                    return;
                }
                LobbyMessage message;
                message = new ClientLobbyMessage(line);
                connection.sendInput(message);
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                return;
            }
        }
    }
}
