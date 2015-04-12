package eda095.project.server.lobby.commands;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyClientState;
import eda095.project.server.lobby.LobbyConnection;
import eda095.project.server.lobby.LobbyMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zol on 01/04/15.
 * <p>
 * Parses {@link LobbyMessage} and returns a callback
 * that should be called to process the command.
 * <p>
 * If the message can not be parsed as a command, it will
 * be processed as a simple chat message.
 */
public class CommandParser {
    private final Lobby lobby;
    private HashMap<String, BiConsumer<LobbyMessage, CommandKeyValueEntry>> functionTable;
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

    /**
     * Setup parsers
     * @param lobby The lobby instance to use for processing in callbacks
     */
    private void setupParsers(Lobby lobby) {
        addParser("quit", "\\/quit", (message, ckve) -> {
            synchronized (this) {
                System.out.println(Thread.currentThread().getId());
                message.getConnection().getState().isLoggedIn = false;
                message.getConnection().getState().setRunning(false);
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
                    connection.outputMessage(new LobbyMessage("Lobby", "You logged in successfully"));
                    connection.outputMessage(new LobbyMessage("Lobby", "Currently logged in users: "));
                    connection.outputMessages(lobby.showUsers());
                    lobby.broadcastMessage(new LobbyMessage("Lobby", username + " has joined the club."));
                }
            }
        });
    }

    /**
     * Add a new parser
     * @param name Name of the parser command
     * @param regex Regex used for validation before processing
     * @param callback Callback to process the message
     */
    private void addParser(String name, String regex, BiConsumer<LobbyMessage, CommandKeyValueEntry> callback) {
        patterns.add(new CommandKeyValueEntry(name, regex, callback));
        functionTable.put(name, callback);
    }

    /**
     * Parses a message and returns a callback to process
     * the specified message.
     * @param message Message to parse
     * @return Callback to process the message.
     */
    public CommandKeyValueEntry parseMessage(LobbyMessage message) {
        if (!message.getAuthor().equals("Lobby")) {
            for (CommandKeyValueEntry ckve : patterns) {
                if (ckve.pattern.matcher(message.getMessage()).matches()) {
                    System.out.println("[Message parsed as: " + ckve.key + "]");
                    return ckve;
                }
            }
        }
        System.out.println("[Message was parsed as a chat message]");
        return DEFAULT_COMMAND_CHAT_MESSAGE;
    }

    public class CommandKeyValueEntry {
        public String key;
        public Pattern pattern;
        public BiConsumer<LobbyMessage, CommandKeyValueEntry> callback;

        public CommandKeyValueEntry(String key, String regex, BiConsumer<LobbyMessage, CommandKeyValueEntry> callback) {
            this.key = key;
            this.pattern = Pattern.compile(regex);
            this.callback = callback;
        }
    }
}
