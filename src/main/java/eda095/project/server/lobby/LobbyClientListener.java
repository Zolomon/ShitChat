package eda095.project.server.lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 23060835 on 3/31/15.
 */
public class LobbyClientListener extends Thread {
    private final int port;
    private Lobby lobby;
    private ServerSocket server;
    private ConcurrentLinkedDeque<LobbyConnection> connections;
    private boolean isRunning = true;
    private int nrUsers = 0;

    public LobbyClientListener(Lobby lobby, int port) {
        connections = new ConcurrentLinkedDeque<>();
        this.lobby = lobby;
        this.lobby.setConnections(connections);
        this.port = port;
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("[Listening for connections on port " + port + "]");
        while (isRunning) {
            try {
                Socket socket = server.accept();
                ++nrUsers;
                System.out.println("[" + socket.getInetAddress().toString() + " connected]");
                LobbyConnection connection = new LobbyConnection(socket, lobby, "anon" + nrUsers);
                connections.add(connection);
                connection.start();
                lobby.presentUsers(connection); //send buddylist to user
                lobby.addUser("anon" + nrUsers); //update all buddylists
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        System.out.println("Stopped listening for connections.");
    }
}
