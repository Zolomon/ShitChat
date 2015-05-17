package eda095.project.server.lobby.commands;

import eda095.project.server.lobby.Lobby;
import eda095.project.server.lobby.LobbyClientState;
import eda095.project.server.lobby.LobbyConnection;
import eda095.project.server.messages.ChatLobbyMessage;
import eda095.project.server.messages.LobbyMessage;
import eda095.project.server.messages.ServerLobbyMessage;
import eda095.project.server.messages.WhisperLobbyMessage;
import eda095.project.server.messages.decorators.BroadcastDecorator;
import eda095.project.shared.Profile;

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
                lobby.clearUser(message.getConnection().getState().getUsername()); //update all buddylists
                message.getConnection().stop();
            }
        });

        addParser("help", "\\/help", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                ArrayList<String> helpMessage = new ArrayList<>();
                helpMessage.add("Available commands: ");
                helpMessage.add("  /help - show this help message");
                helpMessage.add("  /login <username> - login as <username>");
                helpMessage.add("  /msg <channel> <message> - send <message> to all users in <channel>");
                helpMessage.add("  /editprofile <name> <location> <avatar URI> - to fill in the profile with your <name>, your <location> and your <avatar URI>");
                helpMessage.add("  /finger <username> - to view the profile of <username>");
                helpMessage.add("  /join <channel> - to join <channel>");
                helpMessage.add("  /leave <channel> - to leave <channel>");
                helpMessage.add("  /whisper <recipient> <message> - to send a private message to <recipient>");
                helpMessage.add("  /quit - to quit the client");
                for (String m : helpMessage) {
                    lobby.processMessage(message, new ServerLobbyMessage(m));
                }
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
                boolean exists = false;
                for (LobbyConnection a : lobby.getConnections()) {
                    exists |= a.getState().getUsername().equals(recipient);
                }
                if (exists) {
                    lobby.processMessage(message, new WhisperLobbyMessage(state.getUsername(), recipient, cMessage));
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage(
                            String.format("Whisper failed: the user %s does not exist.", recipient)));
                }
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
                                    message.getConnection().getState().getUsername() + " has joined " + channel + "."));
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

        addParser("finger", "\\/finger (?<username>\\w+)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String username = matcher.group("username");
                LobbyClientState state = message.getConnection().getState();
                Profile p = lobby.database.getProfile(username);
                if (p != null) {
                    lobby.processMessage(message, new ServerLobbyMessage("username: " + username +
                            " title: " + p.getTitle() +
                            " location: " + p.getLocation() +
                            " avatar URI: " + p.getAvatar_uri()));
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage("Profile for " + username + "does not exist."));
                }
            }
        });

        addParser("editProfile", "\\/editprofile (?<title>\\w+) (?<location>\\w+) (?<avatar>.+)", (message, ckve) -> {
            synchronized (this) {
                Matcher matcher = ckve.pattern.matcher(message.getMessage());
                matcher.matches();
                String title = matcher.group("title");
                String location = matcher.group("location");
                String avatar = matcher.group("avatar");
                LobbyClientState state = message.getConnection().getState();
                if (state.isLoggedIn()) {
                    Profile p = lobby.database.getProfile(state.getUsername());
                    if (p != null) {
                        p.setTitle(title);
                        p.setLocation(location);
                        p.setAvatar_uri(avatar);
                        int statuscode = lobby.database.update(p);
                        lobby.processMessage(message, new ServerLobbyMessage("Profile was updated with statuscode: " + statuscode + "."));
                    } else {
                        lobby.database.create(new Profile(lobby.database.getAccount(state.getUsername()), title, location, avatar));
                        lobby.processMessage(message, new ServerLobbyMessage("Profile was created."));
                    }
                } else {
                    lobby.processMessage(message, new ServerLobbyMessage("Can't edit profile unless you're logged in."));
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
