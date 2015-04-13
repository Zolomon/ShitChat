package eda095.project.client;

import java.net.*;
import java.io.*;

public class InputThread extends Thread {
    ChatWindow cw;
    InputStream is;
    MessageBox mb;
    CommandParser cp;

    public InputThread(MessageBox mb, ChatWindow cw, InputStream is) {
        this.mb = mb;
        this.cw = cw;
        this.is = is;
        cp = new CommandParser(cw);
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                // Parse json!
                CommandParser.CommandKeyValueEntry callback = cp.parseMessage(line);
                callback.callback.accept(line, callback);
                System.out.println(line);
            }
            mb.setConnectionTerminated(true);             
        } catch (IOException e) {
            System.out.println("IOException in ClientReadThread:\n"+e.getMessage());
        }  
        System.out.println("InputThread shutting down");
    }
}
