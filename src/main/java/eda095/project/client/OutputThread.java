package eda095.project.client;

import java.net.*;
import java.io.*;

public class OutputThread extends Thread {

    Socket s;
    OutputStream os;
    MessageBox mb;

    public OutputThread(Socket s, MessageBox mb) {
        try { 
            os = s.getOutputStream();
        } catch (IOException e) {
            System.err.println("Exception when opening stream");
        }
            this.s = s;
            this.mb = mb;
    }

    public void run() {
        PrintWriter out = new PrintWriter(os, true);
        while (!s.isClosed()) {
            System.out.println("OutputThread: waiting for outgoing.");
            String userInput = mb.getOutgoing(); 
            System.out.println("OutputThread: sent outgoing. os is open: " + s.isClosed());
            if (userInput != null)  {
                out.println(userInput); 
            } else {
                System.out.println("Userinput was null, shutting down?");
            }
        }
        System.out.println("Output thread shutting down");
        
    }
}
