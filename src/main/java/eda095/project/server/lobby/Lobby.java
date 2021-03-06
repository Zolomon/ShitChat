package eda095.project.server.lobby;

import eda095.project.server.lobby.commands.CommandParser;
import eda095.project.server.lobby.database.DatabaseStore;
import eda095.project.server.messages.LobbyMessage;
import eda095.project.server.messages.ServerLobbyMessage;
import eda095.project.server.messages.BuddyLobbyMessage;
import eda095.project.server.messages.ClearBuddyLobbyMessage;
import eda095.project.server.messages.decorators.BroadcastDecorator;
import eda095.project.server.messages.decorators.SequentialListDecorator;
import eda095.project.shared.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Lobby {
    public final DatabaseStore database;
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

    private boolean isLoggedIn(String username) {
        for (LobbyConnection c : connections)
            if (c.getState().getUsername().equals(username)){
                return false;
            }
        return true;
    }

    public synchronized void authenticate(LobbyMessage message, String username) {
        Account acc = database.getAccount(username);
        LobbyConnection connection = message.getConnection();
        LobbyClientState state = connection.getState();
        String oldUsername = connection.getState().getUsername();
        if (acc != null) {
            if (isLoggedIn(username)) {
                state.setUsername(username);
                state.setIsLoggedIn(true);
                processMessage(message, new ServerLobbyMessage("You logged in successfully."));
                processMessage(message, new BroadcastDecorator(new ServerLobbyMessage(username + " has logged in.")));
                clearUser(oldUsername);
                addUser(username);
            } else {
                processMessage(message, new ServerLobbyMessage("Failed logging in: Name already taken."));
            }
        } else {
            database.create(new Account(username));
            state.setUsername(username);
            state.setIsLoggedIn(true);
            processMessage(message, new ServerLobbyMessage("Account created."));
            processMessage(message, new BroadcastDecorator(new ServerLobbyMessage(username + " has joined the club.")));
            clearUser(oldUsername);
            addUser(username);
        }
    }

    public List<LobbyMessage> showUsers(LobbyConnection reciever) {
        ArrayList<LobbyMessage> messages = new ArrayList<>();
        synchronized (connections) {
            for (LobbyConnection con : connections) {
                if(con != reciever) {
                    messages.add(new BuddyLobbyMessage(con.getState().getUsername()));
                }
            }
        }
        return messages;
    }

    public void presentUsers(LobbyConnection reciever){
        SequentialListDecorator sld = new SequentialListDecorator(this.showUsers(reciever));
        sld.setConnection(reciever);
        sld.process(this);
    }

    public void addUser(String username) {
        synchronized (connections) {
            BroadcastDecorator sbd = new BroadcastDecorator(new BuddyLobbyMessage(username));
            sbd.process(this);
        }
    }

    public void clearUser(String username){
        synchronized (connections) {
            BroadcastDecorator bd = new BroadcastDecorator(new ClearBuddyLobbyMessage(username));
            bd.process(this);
        }
    }
}
