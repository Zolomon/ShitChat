package eda095.project.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8888;
        if (args.length != 2) {
            System.out.printf("Attempting to connect to defaults. (%s:%d)\n", host, port);
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            System.out.printf("Connecting to %s:%d)\n", host, port);
        }
        MessageBox mb = new MessageBox();
        try {
            Socket s = new Socket(host, port);
            ChatWindow cw = new ChatWindow(400, 300, "ShitChat", mb);
            InputThread it = new InputThread(mb, cw, s.getInputStream());
            OutputThread ot = new OutputThread(s, mb);
            it.start();
            ot.start();
            cw.show();
            mb.waitForConnectionToTerminate();
            ot.interrupt();
            s.close();
            System.out.println("Connection terminated.");
            cw.destroy();
        } catch (UnknownHostException e) {
            System.err.println("Couldn't find host, exiting.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException when opening socket to host, exiting.");
            System.exit(1);
        }
        System.out.println("Shutting down client.");
    }
}
