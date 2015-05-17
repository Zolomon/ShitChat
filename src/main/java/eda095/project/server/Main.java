package eda095.project.server;

import eda095.project.server.lobby.Lobby;

public class Main {

    public static void main(String[] args) {
        int port = 8888;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        System.out.printf("Starting server on port %d\n", port);

        Lobby lobby = new Lobby(port);
        lobby.start();
    }
}
