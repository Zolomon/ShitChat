package eda095.project.server.lobby;

import eda095.project.server.lobby.commands.CommandParser;
import eda095.project.server.lobby.messages.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Lobby {
    private LobbyClientListener listener;
    private ConcurrentLinkedDeque<LobbyConnection> connections;
    private LinkedBlockingDeque<Message> clientMessages;
    private CommandParser commandParser;

    public Lobby(int port) {
        listener = new LobbyClientListener(this, port);
        clientMessages = new LinkedBlockingDeque<>();
        commandParser = new CommandParser(this);
    }

    public void start() {
        listener.start();
        Message message;
        while(true) {
            System.out.println("[Waiting for a message...]");
            try {
                message = clientMessages.take();
                System.out.println("[Received '"+message+"']" );
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

    public void broadcastMessage(Message message) {
        System.out.println("Broadcasting message: " + message);
        for (LobbyConnection con : connections) {
            con.outputMessage(message);
        }
    }

    public void whisperMessage(Whisper message) {
        System.out.println("whispering: " + message);
        for (LobbyConnection con : connections) {
            if (message.isAuthorOrRecipient(con.getState().username))
                con.outputMessage(message);
        }
    }

    public void input(Message message) {
        clientMessages.offer(message);
    }

    public void removeConnection(LobbyConnection connection) {
        connections.remove(connection);
    }

    public boolean authenticate(Message message) {
        // TODO(zol): Let's properly handle authentication. :)
        if (message.getMessage().contains("zol") ||
                message.getMessage().contains("3amice") ||
                message.getMessage().contains("hanna") ||
                message.getMessage().contains("robin")) {
            return true;
        }
        return false;
    }

    public ArrayList<Message> showUsers() {
        // TODO(zol): We probably need to pool objects so that we don't hit GC too much.
        ArrayList<Message> messages = new ArrayList<>();
        synchronized (connections) {
            for (LobbyConnection con : connections) {
                messages.add(new ServerMessage(con.getState().username));
            }
        }
        return messages;
    }
}
