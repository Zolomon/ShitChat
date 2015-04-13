package eda095.project.client;

import java.util.ArrayList;

public class MessageBox {
    private ArrayList<String> outgoing;
    private ArrayList<String> incoming;

    public MessageBox() {
        outgoing = new ArrayList<String>();
        incoming = new ArrayList<String>();
    }

    public void addOutgoing(String msg) {
        synchronized (outgoing) {
            outgoing.add(msg);
            outgoing.notifyAll();
        }
    }

    /*
    public void addIncoming(String msg) {
        synchronized (incoming) {
            incoming.add(msg);
            notifyAll();
        }
    }
    */

    public String getOutgoing() {
        synchronized (outgoing) {
            while (outgoing.size() == 0)
                try {
                    outgoing.wait();
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException while waiting for thread");
                }
            return outgoing.remove(0);
        }
    }
    /*
    public String getIncoming() {
        synchronized (incoming) {
            while (incoming.size() == 0)
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException while waiting for thread");
                }
            return incoming.remove(0);
        }
    }
    */

}
