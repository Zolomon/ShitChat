package eda095.project.server.lobby;

import eda095.project.server.messages.LobbyMessage;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by zol on 3/31/2015.
 * <p>
 * Handles writing of output to the socket.
 */
public class LobbyClientOutputThread extends Thread {

    private final PrintWriter writer;
    private LobbyConnection connection;
    private LinkedBlockingDeque<LobbyMessage> outputMessageQueue;

    public LobbyClientOutputThread(LobbyConnection connection, OutputStream outputStream, LinkedBlockingDeque<LobbyMessage> outputMessageQueue) {
        this.connection = connection;
        this.outputMessageQueue = outputMessageQueue;
        this.writer = new PrintWriter(outputStream);
    }

    @Override
    public void run() {
        while (connection.getState().isRunning()) {
            LobbyMessage msg;
            try {
                msg = outputMessageQueue.takeFirst();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Client " + connection.getState().getUsername() + " was interrupted while waiting for a connection.");
                break;
            }
            writer.write(msg + "\n");
            writer.flush();
        }
        writer.close();
        System.out.println("Exiting ClientOutputThread");
    }
}
