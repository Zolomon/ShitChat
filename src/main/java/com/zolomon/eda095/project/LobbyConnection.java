package com.zolomon.eda095.project;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 23060835 on 3/31/15.
 */
public class LobbyConnection {
    private final Socket socket;
    private Lobby lobby;
    private String username;
    private CallbackInterface onDisconnectCallback;
    private LobbyClientInputThread inputThread;
    private LobbyClientOutputThread outputThread;
    private String lastMessage;
    private ConcurrentLinkedDeque<LobbyMessage> outputMessageQueue;

    public LobbyConnection(Socket socket, Lobby lobby) {
        this.socket = socket;
        this.lobby = lobby;
        this.outputMessageQueue = new ConcurrentLinkedDeque<>();
        createInputThread(socket);
        createOutputThread(socket);
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
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void broadcastMsg(LobbyMessage message) {

    }

    public void pushMsg(LobbyMessage message) {
        System.out.println("[" + socket.getInetAddress() + ": " + message.getMessage() + "]");
        // TODO(zol): Write message
        lobby.broadcastMessage(message);
    }

    public void start() {
        inputThread.start();
        outputThread.start();
    }

    public synchronized boolean isRunning() {
        if (lastMessage.equals("/quit")) {
            return false;
        }
        return true;
    }

    public Lobby getLobby() {
        return this.lobby;
    }

    public void sendInput(UserChatMessage message) {
        lobby.input(message);
    }
}
