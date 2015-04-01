package com.zolomon.eda095.project.lobby.commands;

import com.zolomon.eda095.project.lobby.Lobby;
import com.zolomon.eda095.project.lobby.LobbyMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
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
    private HashMap<String, Consumer<LobbyMessage>> functionTable;
    private ArrayList<CommandKeyValueEntry> patterns;

    public CommandParser(Lobby lobby) {
        this.lobby = lobby;
        patterns = new ArrayList<>();
        functionTable = new HashMap<>();

        setupParsers(lobby);
    }

    /**
     * Setup parsers
     * @param lobby The lobby instance to use for processing in callbacks
     */
    private void setupParsers(Lobby lobby) {
        addParser("quit", "/quit", (message) -> {
            message.getConnection().getState().isLoggedIn = false;

            lobby.removeConnection(message.getConnection());
        });
        addParser("login", "/login (?<username>.*)", (message) -> {
            boolean authenticated = lobby.authenticate(message);
            if (authenticated) {
                message.getConnection().getState().isLoggedIn = true;
                message.getConnection().outputMessage(new LobbyMessage("Lobby", "You logged in successfully"));
                message.getConnection().outputMessages(lobby.showUsers());
            }
        });
    }

    /**
     * Add a new parser
     * @param name Name of the parser command
     * @param regex Regex used for validation before processing
     * @param callback Callback to process the message
     */
    private void addParser(String name, String regex, Consumer<LobbyMessage> callback) {
        patterns.add(new CommandKeyValueEntry(name, regex, callback));
        functionTable.put(name, callback);
    }

    /**
     * Parses a message and returns a callback to process
     * the specified message.
     * @param message Message to parse
     * @return Callback to process the message.
     */
    public Consumer<LobbyMessage> parseMessage(LobbyMessage message) {
        if (!message.getAuthor().equals("Lobby")) {
            for (CommandKeyValueEntry ckve : patterns) {
                if (ckve.pattern.matcher(message.getMessage()).matches()) {
                    System.out.println("[Message parsed as: " + ckve.key + "]");
                    return ckve.callback;
                }
            }
        }
        System.out.println("[Message was parsed as a chat message]");
        return lobbyMessage -> CommandParser.this.lobby.broadcastMessage(message);
    }

    private class CommandKeyValueEntry {
        public String key;
        public Pattern pattern;
        public Consumer<LobbyMessage> callback;

        public CommandKeyValueEntry(String key, String regex, Consumer<LobbyMessage> callback) {
            this.key = key;
            this.pattern = Pattern.compile(regex);
            this.callback = callback;
        }
    }
}
