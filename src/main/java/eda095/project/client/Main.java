package eda095.project.client;

import java.net.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        MessageBox mb = new MessageBox();
        try {
            Socket s = new Socket("localhost", 8081);
            ChatWindow cw = new ChatWindow(400, 300, "ShitChat", mb);
            InputThread it = new InputThread(cw, s.getInputStream());
            OutputThread ot = new OutputThread(s.getOutputStream(), mb);
            it.start();
            ot.start();
            cw.show();
        } catch (UnknownHostException e) {
            System.err.println("Couldn't find host, exiting.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException when opening socket to host, exiting.");
            System.exit(1);
        }
    }
}