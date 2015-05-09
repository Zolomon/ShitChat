package eda095.project.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    private static CommandKeyValueEntry DEFAULT_COMMAND_CHAT_MESSAGE;
    private final ChatWindow cw;
    private HashMap<String, BiConsumer<String, CommandKeyValueEntry>> functionTable;
    private ArrayList<CommandKeyValueEntry> patterns;

    public CommandParser(ChatWindow cw) {
        this.cw = cw;
        patterns = new ArrayList<>();
        functionTable = new HashMap<>();
        DEFAULT_COMMAND_CHAT_MESSAGE = new CommandKeyValueEntry("UNPARSED", ".*", (message, ckve) -> {
            cw.add("UNPARSED: " + message);
        });
        setupParsers();
    }

    /**
     * Setup parsers
     */
    private void setupParsers() {
        addParser("/msg", "\\/msg (?<channel>\\w+) (?<username>\\w+) (?<message>.*)", (message, ckve) -> {
            synchronized (this) {
                System.out.println("Executing " + ckve.key);
                Matcher matcher = ckve.pattern.matcher(message);
                boolean matches = matcher.matches();
                String channel = matcher.group("channel");
                String username = matcher.group("username");
                String cMessage = matcher.group("message");
                cw.add("[" + channel + "] " + username + ": " + cMessage);
            }
        });
        addParser("/server", "\\/server (?<message>.*)", (message, ckve) -> {
            synchronized (this) {
                System.out.println("Executing " + ckve.key);
                Matcher matcher = ckve.pattern.matcher(message);
                boolean matches = matcher.matches();
                String cMessage = matcher.group("message");
                cw.add(cMessage);
            }
        });
        addParser("/whisper", "\\/whisper (?<author>\\w+) (?<recipient>\\w+) (?<message>.*)", (message, ckve) -> {
            synchronized (this) {
                System.out.println("Executing " + ckve.key);
                Matcher matcher = ckve.pattern.matcher(message);
                boolean matches = matcher.matches();
                String author = matcher.group("author");
                String recipient = matcher.group("recipient");
                String cMessage = matcher.group("message");
                cw.add("whisper " + author + " -> " + recipient + ": " + cMessage);
            }
        });
        addParser("/buddy", "\\/buddy (?<buddy>\\w+)", (message, ckve) -> {
            synchronized (this) {
                System.out.println("Executing " + ckve.key);
                Matcher matcher = ckve.pattern.matcher(message);
                boolean matches = matcher.matches();
                String buddy = matcher.group("buddy");
                cw.addBuddy(buddy);
            }
        });

        addParser("/clear", "\\/clear (?<buddy>\\w+)", (message, ckve) -> {
            synchronized (this) {
                System.out.println("Executing " + ckve.key);
                Matcher matcher = ckve.pattern.matcher(message);
                boolean matches = matcher.matches();
                String buddy = matcher.group("buddy");
                cw.removeBuddy(buddy);
            }
        });
    }

    private void addParser(String name, String regex, BiConsumer<String, CommandKeyValueEntry> callback) {
        patterns.add(new CommandKeyValueEntry(name, regex, callback));
        functionTable.put(name, callback);
    }

    public CommandKeyValueEntry parseMessage(String message) {
        for (CommandKeyValueEntry ckve : patterns) {
            //System.out.println("[Trying to parse as: " + ckve.key + "]");
            if (ckve.pattern.matcher(message).matches()) {
                System.out.println("[Message parsed as: " + ckve.key + "]");
                return ckve;
            }
        }
        System.out.println("[Message was parsed as a UNPARSED message]");
        return DEFAULT_COMMAND_CHAT_MESSAGE;
    }

    public class CommandKeyValueEntry {
        public String key;
        public Pattern pattern;
        public BiConsumer<String, CommandKeyValueEntry> callback;

        public CommandKeyValueEntry(String key, String regex, BiConsumer<String, CommandKeyValueEntry> callback) {
            this.key = key;
            this.pattern = Pattern.compile(regex);
            this.callback = callback;
        }
    }
}
