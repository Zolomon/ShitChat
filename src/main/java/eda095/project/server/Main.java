package eda095.project.server;

import eda095.project.server.lobby.Lobby;

public class Main {

    public static void main(String[] args) {
	    Lobby lobby = new Lobby(8081);
        lobby.start();
    }
}
