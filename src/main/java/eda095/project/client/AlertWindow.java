package eda095.project.client;
import java.awt.*;
import java.awt.event.*;

/**
 * Implements pop-up alert boxes which displays a message and requires the
 * user to confirm the message before proceeding.<p>
 * Since the class only contains one static method and nothing else, it
 * should normally not be instantiated. The static method can be called
 * directly using <code>AlertWindow.displayAlert("Title","A message.")</code>.
 *
 * @author Roger Henriksson
 * @version 1.0 (2002-04-29)
 */
public class AlertWindow {

    /**
     * Displays an alert box containing a message. The method blocks until
     * the user confirms the message by clicking the OK button.
     *
     * @param x        The x coordinate of the alert window.
     * @param y        The y coordinate of the alert window.
     * @param title    The string to be displayed in the title bar of
     *                 of the window associated with the alert box.
     * @param message  The string to be displayed in the alert box.
     */
    public static void displayAlert(int x,int y,String title,String message) {
        Frame theAlert = new Frame(title);
        theAlert.setLocation(x,y);

        Label theMessage = new Label(message);
        Panel messagePanel = new Panel();
        theAlert.add("Center",messagePanel);
        messagePanel.add(theMessage);
        Font f = new Font("Dialog",Font.PLAIN,16);
        theMessage.setFont(f);

        Panel okPanel = new Panel();
        Button okButton = new Button("OK");
        okPanel.add(okButton);
        theAlert.add("South",okPanel);
        f = new Font("Dialog",Font.BOLD,16);
        okButton.setFont(f);
        ButtonHandler handler = new ButtonHandler();
        okButton.addActionListener(handler);

        theAlert.pack();
        theAlert.setVisible(true);

        handler.awaitOK();

        theAlert.dispose();
    }

}

class ButtonHandler implements ActionListener {
    boolean confirmed;

    public void actionPerformed(ActionEvent e) {
        confirmOK();
    }

    private synchronized void confirmOK() {
        confirmed = true;
        notify();
    }

    public synchronized void awaitOK() {
        if (!confirmed) {
            try{
                wait();
            } catch(InterruptedException e) { }
        }
    }
}
