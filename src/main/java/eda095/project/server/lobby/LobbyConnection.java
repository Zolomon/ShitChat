package eda095.project.server.lobby;

import eda095.project.server.lobby.messages.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

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
    private LinkedBlockingDeque<Message> outputMessageQueue;

    public LobbyConnection(Socket socket, Lobby lobby) {
        this.socket = socket;
        this.lobby = lobby;
        this.outputMessageQueue = new LinkedBlockingDeque<>();
        state = new LobbyClientState();

        // TODO(zol): Figure out a nice way to handle states, EX: finite state machine
        this.outputMessageQueue.addLast(new ServerMessage("Welcome!"));
        this.outputMessageQueue.addLast(new ServerMessage("Please enter your username"));

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

    public String getUsername() {
        return username;
    }

    public void start() {
        state.setRunning(true);
        inputThread.start();
        outputThread.start();
        System.out.printf("Client connection started.");
    }

    // How can we be sure that input and output threads terminate?
    public void stop() {
        state.setRunning(false);
        state.isLoggedIn = false;
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed stopping connection");
            e.printStackTrace();
        }
        System.out.printf("Client connection stopped.");
    }

    public void sendInput(Message message) {
        message.setConnection(this);
        lobby.input(message);
    }

    public void outputMessage(Message message) {
        outputMessageQueue.addLast(message);
    }

    public void outputMessages(ArrayList<Message> messages) {
        for(Message m : messages) {
            outputMessage(m);
        }
    }
}
