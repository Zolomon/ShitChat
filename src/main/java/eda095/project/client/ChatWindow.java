package eda095.project.client;
import java.awt.*;
import java.awt.event.*;

/**
 * A ChatWindow is a window which handles the user interaction of one
 *  active chat session. The window consists of two main parts. The upper
 * part of the window consists of a scrollable text field into which
 * incoming messages (as well as your own) can be written. The text field
 * is read-only to the user. The lower part of the window consists of
 * a single-line input field into which the user can type his own, out-going,
 * messages. A button which can be used to submit the text in the input
 * field to the chat system is located next to the input field.<p>
 * Input, that is messages entered into the input field, is handled by
 * the <code>messageEntered</code> method which is called automatically
 * whenever the user clicks on the submit button. The intention is that the
 * ChatWindow class should be used as a super class to another class which
 * overloads the <code>messageEntered</code> method. The overloaded method
 * should contain the Java code to be executed when a new message is entered.
 * Another possible way of using the class is to modify the ChatWindow class
 * directly, putting the code to be executed when a new message is entered
 * directly in the <code>messageEntered</code> method.
 *
 * @author  Roger Henriksson
 * @version 1.0 (2002-04-29)
 */
public class ChatWindow {

    private Frame         theWindow;
    private TextArea      theMessages;
    private Panel         inputPanel;
    private TextField     inputField;
    private Button        submitButton;
    private SubmitHandler handler;
    private boolean       destroyed;
    private SubmitThread  submitter;

    /**
     * Constructs a ChatWindow at a given position on the screen and with
     * a given title which is displayed in the title bar of the window. Note
     * that the window is initially invisible. You must call the
     * <code>show</code> method in order to display the window.
     * 
     *
     * @param x      the x coordinate of the position of the window
     * @param y      the y coordinate of the position of the window
     * @param title  a String containing the title to be displayed in
     *               the title bar of the window
     */
    public ChatWindow(int x,int y,String title) {
        theWindow = new Frame(title);
        theWindow.setLocation(x,y);

        Font f = new Font("Dialog",Font.PLAIN,16);

        theMessages = new TextArea("",20,80);
        theMessages.setFont(f);
        theMessages.setEditable(false);
        theMessages.setBackground(SystemColor.text);
        theWindow.add("Center",theMessages);

        inputPanel = new Panel();
        theWindow.add("South",inputPanel);

        inputField = new TextField("",70);
        inputField.setFont(f);
        inputPanel.add(inputField);

        f = new Font("Dialog",Font.BOLD,16);
        submitButton = new Button("Submit");
        submitButton.setFont(f);
        inputPanel.add(submitButton);

        handler = new SubmitHandler();
        submitButton.addActionListener(handler);
        inputField.addActionListener(handler);

        theWindow.pack();

        submitter = null;
    }

    /**
     * Adds a new message to the message area in the upper part of the
     * ChatWindow. The new message should consist of a String containing
     * a single line of text. The new message is added to the bottom of
     * the message area.
     *
     * @param message  a String containing the message to add
     */
    public void add(String message) {
        if (!destroyed) {
            theMessages.append(message+'\n');
        }
    }

    /**
     * Clears the contents of the message area in the upper part of the
     * ChatWindow.
     */
    public void clear() {
        if (!destroyed) {
            theMessages.replaceRange("",0,theMessages.getText().length());
        }
    }

    /**
     * Makes the ChatWindow invisible. Call <code>show</code> to make the
     * window visible again.
     *
     * @see ChatWindow#show
     */
    public void hide() {
        if (!destroyed) {
            theWindow.setVisible(false);    
        }
    }

    /**
     * Makes the ChatWindow visible. ChatWindows is initially invisible
     * when having been created. To actually make it visible you must call
     * the <code>show</code> method. Call <code>hide</code> to make the
     * window invisible again.
     *
     * @see ChatWindow#hide
     */
    public void show() {
        if (!destroyed) {
            theWindow.setVisible(true);
        }
    }

    /**
     * Destroys the ChatWindow and frees all resources associated with it.
     * The <code>destroy</code> method should be called when the ChatWindow
     * is not needed anymore. Note that it is <b>ILLEGAL</b> to call any
     * method of the ChatWindow object once <code>destroy</code> has
     * been called.
     */
    public void destroy() {
        destroyed = true;
        theWindow.dispose();
    }

    /**
     * The <code>messageEntered</code> method is called whenever the user
     * submits a new message and controls how to handle the new message.
     * The intention is that this method should be overloaded in a
     * subclass to ChatWindow. The default implementation found in the
     * ChatWindow class does nothing.
     *
     * @param message  a String containing the new message entered by
     *                 the user
     */
    public void messageEntered(String message) {
        // Default implementation: do nothing
        // Overload messageEntered in a subclass to ChatWindow.
    }

    private class SubmitHandler implements ActionListener {
        boolean confirmed;

        public void actionPerformed(ActionEvent e) {
            String m = inputField.getText();
            inputField.setText("");
            if (submitter==null || !submitter.isAlive()) {
                submitter = new SubmitThread(m);
                submitter.start();
            }
        }
    }

    private class SubmitThread extends Thread {
        private String mess;

        public SubmitThread(String mess) {
            this.mess = mess;
        }

        public void run() {
            messageEntered(mess);
        }
    }

}
