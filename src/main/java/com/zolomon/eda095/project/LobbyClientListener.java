package com.zolomon.eda095.project;

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

    public LobbyClientListener(Lobby lobby, int port) {
        connections = new ConcurrentLinkedDeque<LobbyConnection>();
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
        while(isRunning) {
            try {
                Socket socket = server.accept();
                System.out.println("["+socket.getInetAddress().toString()+" connected]");
                LobbyConnection connection = new LobbyConnection(socket, lobby);
                connections.add(connection);

                lobby.broadcastNewConnection(connection);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        System.out.println("Stopped listening for connections.");
    }
}
