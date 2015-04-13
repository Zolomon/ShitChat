package eda095.project.server.lobby.commands;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.messages.Message;
import eda095.project.server.lobby.messages.ServerMessage;
import eda095.project.server.lobby.LobbyClientState;
import eda095.project.server.lobby.LobbyConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private final Lobby lobby;
    private HashMap<String, BiConsumer<Message, CommandKeyValueEntry>> functionTable;
    private ArrayList<CommandKeyValueEntry> patterns;
    private static CommandKeyValueEntry DEFAULT_COMMAND_CHAT_MESSAGE;

    public CommandParser(Lobby lobby) {
        this.lobby = lobby;
        patterns = new ArrayList<>();
        functionTable = new HashMap<>();
        DEFAULT_COMMAND_CHAT_MESSAGE = new CommandKeyValueEntry("default", ".*", (lobbyMessage, ckve) -> {
            lobby.broadcastMessage(lobbyMessage);
        });
        setupParsers(lobby);
    }

    private void setupParsers(Lobby lobby) {
        addParser("quit", "\\/quit", (message, ckve) -> {
            synchronized (this) {
                message.getConnection().stop();
                lobby.removeConnection(message.getConnection());
            }
        });
        addParser("login", "\\/login (?<username>.*)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                boolean matches = matcher.matches();
                String username = matcher.group("username");
                boolean authenticated = lobby.authenticate(message);
                if (authenticated) {
                    LobbyConnection connection = message.getConnection();
                    LobbyClientState state = connection.getState();
                    state.username = username;
                    state.isLoggedIn = true;
                    connection.outputMessage(new ServerMessage("You logged in successfully"));
                    connection.outputMessage(new ServerMessage("Currently logged in users: "));
                    connection.outputMessages(lobby.showUsers());
                    lobby.broadcastMessage(new ServerMessage(username + " has joined the club."));
                }
            }
        });
    }

    private void addParser(String name, String regex, BiConsumer<Message, CommandKeyValueEntry> callback) {
        patterns.add(new CommandKeyValueEntry(name, regex, callback));
        functionTable.put(name, callback);
    }

    public CommandKeyValueEntry parseMessage(Message message) {
        for (CommandKeyValueEntry ckve : patterns) {
            if (ckve.pattern.matcher(message.getMessage()).matches()) {
                System.out.println("[Message parsed as: " + ckve.key + "]");
                return ckve;
            }
        }
        System.out.println("[Message was parsed as a chat message]");
        return DEFAULT_COMMAND_CHAT_MESSAGE;
    }

    public class CommandKeyValueEntry {
        public String key;
        public Pattern pattern;
        public BiConsumer<Message, CommandKeyValueEntry> callback;

        public CommandKeyValueEntry(String key, String regex, BiConsumer<Message, CommandKeyValueEntry> callback) {
            this.key = key;
            this.pattern = Pattern.compile(regex);
            this.callback = callback;
        }
    }
}
