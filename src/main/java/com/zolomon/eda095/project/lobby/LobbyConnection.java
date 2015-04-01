package com.zolomon.eda095.project.lobby;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 23060835 on 3/31/15.
 */
public class LobbyConnection {
    private final Socket socket;
    private Lobby lobby;
    private String username;
    private LobbyClientState state;
    private LobbyClientInputThread inputThread;
    private LobbyClientOutputThread outputThread;
    private LinkedBlockingDeque<LobbyMessage> outputMessageQueue;
    private boolean isLoggedIn;
    private boolean isRunning;

    public LobbyConnection(Socket socket, Lobby lobby) {
        this.socket = socket;
        this.lobby = lobby;
        this.outputMessageQueue = new LinkedBlockingDeque<>();
        state = new LobbyClientState();

        // TODO(zol): Figure out a nice way to handle states, EX: finite state machine
        this.outputMessageQueue.addLast(new LobbyMessage("Lobby", "Welcome!"));
        this.outputMessageQueue.addLast(new LobbyMessage("Lobby", "Please enter your username"));

        isRunning = true;

        createInputThread(socket);
        createOutputThread(socket);
    }

    // TODO(zol): Need to fix some kind of login manager

    public LobbyClientState getState() {
        return state;
    }

    private void createOutputThread(Socket socket) {
        try {
            this.outputThread = new LobbyClientOutputThread(this, socket.getOutputStream(), this.outputMessageQueue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createInputThread(Socket socket) {
        try {
            inputThread = new LobbyClientInputThread(this, socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login() {

        // TODO(zol): if new user, create account
        // TODO(zol): disconnect if login failed
        // TODO(zol): Set username to what it should be
        username = "Zolomon";
        isLoggedIn = true;
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void start() {
        inputThread.start();
        outputThread.start();
        System.out.printf("Client connection started.");
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public Lobby getLobby() {
        return this.lobby;
    }

    public void sendInput(LobbyMessage message) {
        message.setConnection(this);

        Pattern pattern = Pattern.compile("/name (?<name>.*)");
        Matcher matcher = pattern.matcher(message.getMessage());

        if (matcher.matches()) {
            username = matcher.group(1);
            lobby.broadcastMessage(new LobbyMessage("Lobby", username + " has logged in."));
            isLoggedIn = true;
        }

        pattern = Pattern.compile("/quit");
        matcher = pattern.matcher(message.getMessage());
        if (matcher.matches()) {
            synchronized (this) {
                isRunning = false;
            }
        }

        lobby.input(message);
    }

    public void outputMessage(LobbyMessage message) {
        outputMessageQueue.addLast(message);
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void outputMessages(ArrayList<LobbyMessage> lobbyMessages) {
        for(LobbyMessage m : lobbyMessages) {
            outputMessage(m);
        }
    }
}