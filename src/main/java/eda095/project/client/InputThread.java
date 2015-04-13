package eda095.project.client;

import java.net.*;
import java.io.*;

public class InputThread extends Thread {
    ChatWindow cw;
    InputStream is;

    public InputThread(ChatWindow cw, InputStream is) {
        this.is = is;
        this.cw = cw;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                // Parse json!
                cw.add(line);
            }
            System.out.println("Connection terminated.");
        } catch (IOException e) {
            System.out.println("IOException in ClientReadThread:\n"+e.getMessage());
        }  
    }
}
