package eda095.project.server.lobby;

import eda095.project.server.lobby.commands.CommandParser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by 23060835 on 3/31/15.
 */
public class Lobby {
    private LobbyClientListener listener;
    private ConcurrentLinkedDeque<LobbyConnection> connections;
    private LinkedBlockingDeque<LobbyMessage> clientMessages;
    private CommandParser commandParser;

    /**
     * Start the lobby listening on the specified {@code port}.
     *
     * @param port The port to listen on
     */
    public Lobby(int port) {
        listener = new LobbyClientListener(this, port);
        clientMessages = new LinkedBlockingDeque<>();
        commandParser = new CommandParser(this);
    }

    /**
     * Start the listener thread
     */
    public void start() {
        listener.start();
        LobbyMessage message;
        while(true) {
            System.out.println("[Waiting for a message...]");
            try {
                message = clientMessages.take();
                System.out.println("[Received '"+message.toString()+"']" );
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            CommandParser.CommandKeyValueEntry callback = commandParser.parseMessage(message);
            callback.callback.accept(message, callback);
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
        System.out.println("Broadcasting message: " + message);
        for (LobbyConnection con : connections) {
            con.outputMessage(message);
        }
    }

    /**
     * Send an input message to the lobby for verification
     * and processing.
     * <p>
     * The message will be verified and then the state will be modified
     * according to the input.
     *
     * @param message Input message to process
     */
    public void input(LobbyMessage message) {
        clientMessages.offer(message);
    }

    public void removeConnection(LobbyConnection connection) {
        connections.remove(connection);
    }

    public boolean authenticate(LobbyMessage message) {
        // TODO(zol): Let's properly handle authentication. :)
        if (message.getMessage().contains("zol") ||
                message.getMessage().contains("3amice") ||
                message.getMessage().contains("hanna") ||
                message.getMessage().contains("robin")) {
            return true;
        }
        return false;
    }

    public ArrayList<LobbyMessage> showUsers() {
        // TODO(zol): We probably need to pool objects so that we don't hit GC too much.
        ArrayList<LobbyMessage> messages = new ArrayList<>();
        synchronized (connections) {
            for (LobbyConnection con : connections) {
                messages.add(new LobbyMessage("Lobby", con.getState().username));
            }
        }
        return messages;
    }
}
