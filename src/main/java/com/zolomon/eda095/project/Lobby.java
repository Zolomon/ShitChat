package com.zolomon.eda095.project;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 23060835 on 3/31/15.
 */
public class Lobby {
    private LobbyClientListener listener;
    private ConcurrentLinkedDeque<LobbyConnection> connections;
    private ConcurrentLinkedDeque<LobbyMessage> clientMessages;

    /**
     * Start the lobby listening on the specified {@link port}.
     * @param port The port to listen on
     */
    public Lobby(int port) {
        listener = new LobbyClientListener(this, port);
        clientMessages = new ConcurrentLinkedDeque<>();
    }

    /**
     * Start the listener thread
     */
    public void start() {
        listener.start();
    }

    /**
     * Broadcast a new connection to all clients notifying them of a new connection.
     * @param connection
     */
    public synchronized void broadcastNewConnection(LobbyConnection connection) {
        for (LobbyConnection con : connections) {
            con.pushMsg(new NewUserConnectedMessage("Lobby", connection));
        }
    }

    public ConcurrentLinkedDeque<LobbyConnection> getConnections() {
        return connections;
    }

    public void setConnections(ConcurrentLinkedDeque<LobbyConnection> connections) {
        this.connections = connections;
    }

    /**
     * Sends a message to all connected clients
     *
     * @param message Message to broadcast to clients
     */
    public void broadcastMessage(LobbyMessage message) {
        for (LobbyConnection con : connections) {
            con.outputMessage(message);
        }
    }

    /**
     * Send an input message to the lobby for verification
     * and processing.
     * <p/>
     * The message will be verified and then the state will be modified
     * according to the input.
     *
     * @param message Input message to process
     */
    public void input(LobbyMessage message) {
        clientMessages.addLast(message);
    }
}
