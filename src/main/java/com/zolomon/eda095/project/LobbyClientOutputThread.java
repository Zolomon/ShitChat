package com.zolomon.eda095.project;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by zol on 3/31/2015.
 *
 * Handles writing of output to the socket.
 */
public class LobbyClientOutputThread extends Thread {

    private final PrintWriter writer;
    private LobbyConnection connection;
    private ConcurrentLinkedDeque<LobbyMessage> outputMessageQueue;

    public LobbyClientOutputThread(LobbyConnection connection, OutputStream outputStream, ConcurrentLinkedDeque<LobbyMessage> outputMessageQueue) {
        this.connection = connection;
        this.outputMessageQueue = outputMessageQueue;
        this.writer = new PrintWriter(outputStream);
    }

    @Override
    public void run() {
        Lobby lobby = connection.getLobby();
        while (connection.isRunning()) {
            LobbyMessage msg = outputMessageQueue.pollFirst();
            writer.write(msg.getMessage());
            writer.flush();
        }
        writer.close();
    }
}
