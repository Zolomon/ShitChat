package com.zolomon.eda095.project;

import com.zolomon.eda095.project.lobby.Lobby;

public class Main {

    public static void main(String[] args) {
	    Lobby lobby = new Lobby(8081);
        lobby.start();
    }
}
