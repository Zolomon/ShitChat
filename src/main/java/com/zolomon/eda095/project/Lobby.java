package com.zolomon.eda095.project;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by 23060835 on 3/31/15.
 */
public class Lobby {
    private LobbyClientListener listener;
    private ConcurrentLinkedDeque<LobbyConnection> connections;

    public Lobby(int port) {
        listener = new LobbyClientListener(this, port);
    }

    public void start() {
        listener.start();
    }

    public synchronized void broadcastNewConnection(LobbyConnection connection) {
        for (LobbyConnection con : connections) {
            con.pushMsg(new NewUserConnectedMessage("Lobby", connection));
        }
    }

    public void setConnections(ConcurrentLinkedDeque<LobbyConnection> connections) {
        this.connections = connections;
    }

    public ConcurrentLinkedDeque<LobbyConnection> getConnections() {
        return connections;
    }
}
