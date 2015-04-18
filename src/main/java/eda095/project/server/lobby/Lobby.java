package eda095.project.server.lobby;

import eda095.project.server.lobby.commands.CommandParser;
import eda095.project.server.lobby.database.DatabaseStore;
import eda095.project.server.messages.LobbyMessage;
import eda095.project.server.messages.ServerLobbyMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Lobby {
    private final DatabaseStore database;
    private final LobbyClientListener listener;
    private final LinkedBlockingDeque<LobbyMessage> clientMessages;
    private final CommandParser commandParser;
    private ConcurrentLinkedDeque<LobbyConnection> connections;

    public Lobby(int port) {
        database = DatabaseStore.getInstance();
        listener = new LobbyClientListener(this, port);
        clientMessages = new LinkedBlockingDeque<>();
        commandParser = new CommandParser(this);
    }

    public void start() {
        database.load();

        listener.start();
        LobbyMessage message;
        while (true) {
            System.out.println("[Waiting for a message...]");
            try {
                message = clientMessages.take();
                System.out.println("[Received '" + message + "']");
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            CommandParser.CommandKeyValueEntry callback = commandParser.parseMessage(message);
            callback.callback.accept(message, callback);
        }

        database.close();
    }

    public ConcurrentLinkedDeque<LobbyConnection> getConnections() {
        return connections;
    }

    public void setConnections(ConcurrentLinkedDeque<LobbyConnection> connections) {
        this.connections = connections;
    }

    public void processMessage(LobbyMessage originalLobbyMessage, LobbyMessage message) {
        message.setConnection(originalLobbyMessage.getConnection());
        message.process(this);
    }

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

    public List<LobbyMessage> showUsers() {
        ArrayList<LobbyMessage> messages = new ArrayList<>();
        synchronized (connections) {
            for (LobbyConnection con : connections) {
                messages.add(new ServerLobbyMessage(con.getState().getUsername()));
            }
        }
        return messages;
    }
}
