package eda095.project.server.lobby.commands;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyClientState;
import eda095.project.server.lobby.LobbyConnection;
import eda095.project.server.lobby.database.DatabaseStore;
import eda095.project.server.messages.ChatLobbyMessage;
import eda095.project.server.messages.LobbyMessage;
import eda095.project.server.messages.ServerLobbyMessage;
import eda095.project.server.messages.WhisperLobbyMessage;
import eda095.project.server.messages.decorators.BroadcastDecorator;
import eda095.project.server.messages.decorators.SequentialListDecorator;
import eda095.project.shared.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private static CommandKeyValueEntry DEFAULT_COMMAND_CHAT_MESSAGE;
    private final Lobby lobby;
    private HashMap<String, BiConsumer<LobbyMessage, CommandKeyValueEntry>> functionTable;
    private ArrayList<CommandKeyValueEntry> patterns;

    public CommandParser(Lobby lobby) {
        this.lobby = lobby;
        patterns = new ArrayList<>();
        functionTable = new HashMap<>();
        DEFAULT_COMMAND_CHAT_MESSAGE = new CommandKeyValueEntry("default", ".*", (lobbyMessage, ckve) -> {
            LobbyConnection connection = lobbyMessage.getConnection();
            LobbyClientState state = connection.getState();
            lobby.processMessage(lobbyMessage, new BroadcastDecorator(new ChatLobbyMessage(state.getUsername(), "general", lobbyMessage.toString())));
        });
        setupParsers(lobby);
    }

    private void setupParsers(Lobby lobby) {
        addParser("quit", "\\/quit", (message, ckve) -> {
            synchronized (this) {
                lobby.processMessage(message,
                        new BroadcastDecorator(
                                new ServerLobbyMessage(message.getConnection().getState().getUsername() + " has perished.")));
                message.getConnection().stop();
            }
        });

        addParser("login", "\\/login (?<username>.*)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String username = matcher.group("username");
                if (!username.toLowerCase().equals("server")) {
                    lobby.authenticate(message, username);
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage("Sorry, you can't name yourself that."));
                }
            }
        });

        addParser("whisper", "\\/whisper (?<recipient>\\w+) (?<message>.*)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String recipient = matcher.group("recipient");
                String cMessage = matcher.group("message");
                LobbyConnection connection = message.getConnection();
                LobbyClientState state = connection.getState();
                lobby.processMessage(message, new WhisperLobbyMessage(state.getUsername(), recipient, cMessage));
            }
        });

        addParser("join", "\\/join (?<channel>\\w+)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String channel = matcher.group("channel");
                if (message.getConnection().getState().addChannel(channel)) {
                    lobby.processMessage(message,
                            new ChatLobbyMessage("server", channel,
                                    message.getConnection().getState().getUsername()+ " has joined " + channel + "."));
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage("You are already a member of " + channel + "."));
                }
            }
        });

        addParser("leave", "\\/leave (?<channel>\\w+)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String channel = matcher.group("channel");
                if (channel.equals("general")) {
                    lobby.processMessage(message, new ServerLobbyMessage("Participation in the general channel is obligatory."));
                } else if (message.getConnection().getState().removeChannel(channel)) {
                    lobby.processMessage(message, new ServerLobbyMessage("Left channel " + channel + "."));
                    String username = message.getConnection().getState().getUsername();
                    lobby.processMessage(message,
                            new ChatLobbyMessage("server", channel, username + " has left " + channel + "."));
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage("You are not a member of " + channel + "."));
                }
            }
        });

        addParser("channelMessage", "\\/msg (?<channel>\\w+) (?<message>.*)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String channel = matcher.group("channel");
                String cMessage = matcher.group("message");
                String username = message.getConnection().getState().getUsername();
                if (message.getConnection().getState().getChannels().contains(channel)) {
                    lobby.processMessage(message, new ChatLobbyMessage(username, channel, cMessage));
                } else {
                    lobby.processMessage(message,
                            new ServerLobbyMessage("Message not sent, you are not a member of " + channel + "."));
                }
            }
        });
    }

    private void addParser(String name, String regex, BiConsumer<LobbyMessage, CommandKeyValueEntry> callback) {
        patterns.add(new CommandKeyValueEntry(name, regex, callback));
        functionTable.put(name, callback);
    }

    public CommandKeyValueEntry parseMessage(LobbyMessage message) {
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
        public BiConsumer<LobbyMessage, CommandKeyValueEntry> callback;

        public CommandKeyValueEntry(String key, String regex, BiConsumer<LobbyMessage, CommandKeyValueEntry> callback) {
            this.key = key;
            this.pattern = Pattern.compile(regex);
            this.callback = callback;
        }
    }
}
