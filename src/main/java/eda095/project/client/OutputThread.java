package eda095.project.client;

import java.net.*;
import java.io.*;

public class OutputThread extends Thread {

    OutputStream os;
    MessageBox mb;

    public OutputThread(OutputStream os, MessageBox mb) {
        this.os = os;
        this.mb = mb;
    }

    public void run() {
        while (true) {
            PrintWriter out = new PrintWriter(os, true);
            System.out.println("OutputThread: waiting for outgoing.");
            String userInput = mb.getOutgoing(); 
            System.out.println("OutputThread: sent outgoing.");
            out.println(userInput);
        }
    }
}
