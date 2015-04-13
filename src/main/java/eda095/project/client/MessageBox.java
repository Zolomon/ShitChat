package eda095.project.client;

import java.util.ArrayList;

public class MessageBox {
    private ArrayList<String> outgoing;
    private ArrayList<String> incoming;
    private boolean terminatedConnection;

    public MessageBox() {
        outgoing = new ArrayList<String>();
        incoming = new ArrayList<String>();
        terminatedConnection = false;
    }
    
    public void setConnectionTerminated(boolean t) {
        synchronized (this) {
            terminatedConnection = t; 
            notifyAll();
        }
    }

    public void waitForConnectionToTerminate() {
        synchronized (this) {
            while (!terminatedConnection) 
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted while waiting for connection to terminate!");
                }
        }
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
            while (!terminatedConnection && outgoing.size() == 0)
                try {
                    outgoing.wait();
                } catch (InterruptedException e) {
                    System.err.println("MessageBox.getOutgoing(): InterruptedException while waiting for thread");
                }
            return (outgoing.size() > 0 ? outgoing.remove(0) : null);
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
