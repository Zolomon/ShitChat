package com.zolomon.eda095.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zol on 3/31/2015.
 */
public class LobbyClientInputThread extends Thread {

    private final BufferedReader reader;
    private final LobbyConnection connection;

    public LobbyClientInputThread(LobbyConnection connection, InputStream inputStream) {
        this.connection = connection;
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void run() {
        while(connection.isRunning()) {

        }
    }
}
