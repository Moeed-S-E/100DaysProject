package app;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

@SuppressWarnings("unused")
public class ChatClient extends JFrame {
    private static final long serialVersionUID = 1L; 
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private JButton createRoomButton, joinRoomButton, leaveRoomButton, exitButton; 
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private StyledDocument doc;
    private Style myStyle, userStyle, serverStyle, errorStyle, roomMessageStyle;
    private String loggedInUsername;
    private String currentRoomName = null; // Track current room

    private JList<String> userJList;
    private DefaultListModel<String> userListModel;

    public ChatClient() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
        }

        // This ensures clicking 'X' button on window triggers shutdownClient then exits.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Override default, handle manually
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownClient();
            }
        });

        if (showLoginDialog()) {
            // GUI initialized and shown in connectToServer if auth is successful
        } else {
            System.out.println("Login cancelled or failed. Exiting client application.");
            System.exit(0); // Exit if login is cancelled
        }
    }

    private boolean showLoginDialog() {
        // ... (Login dialog code remains the same as in your provided paste.txt)
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPasswordField passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        SwingUtilities.invokeLater(usernameField::requestFocusInWindow);
        int result = JOptionPane.showConfirmDialog(this, loginPanel, "Login to Chat",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return showLoginDialog();
            }
            return connectToServer(username, password);
        }
        return false;
    }

    private boolean connectToServer(String username, String password) {
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);
            out.println(password);

            String response = in.readLine();
            if ("AUTH_SUCCESS".equals(response)) {
                this.loggedInUsername = username;
                initGUI();
                setVisible(true);
                new MessageReceiver().start();
                appendMessageToPane("[SYSTEM] Connected to server as " + loggedInUsername + ".\n", serverStyle);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Authentication Failed.", "Login Error", JOptionPane.ERROR_MESSAGE);
                closeConnection();
                return false;
            }
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Server not found.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not connect: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        closeConnection(); // Ensure connection is closed on failure
        return false;
    }

    private void initGUI() {
        setTitle("Chat Application - " + loggedInUsername + (currentRoomName == null ? " (Lobby)" : " (Room: " + currentRoomName + ")"));
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        doc = chatPane.getStyledDocument();
        setupStyles();
        JScrollPane chatScrollPane = new JScrollPane(chatPane);

        // User List Panel (East)
        userListModel = new DefaultListModel<>();
        userJList = new JList<>(userListModel);
        JScrollPane userListScrollPane = new JScrollPane(userJList);
        userListScrollPane.setPreferredSize(new Dimension(150, 0));
        JPanel eastPanel = new JPanel(new BorderLayout(5,5));
        eastPanel.add(new JLabel("Online Users", SwingConstants.CENTER), BorderLayout.NORTH);
        eastPanel.add(userListScrollPane, BorderLayout.CENTER);
        updateUserList(new String[]{loggedInUsername + " (You)"}); // Initial self add

        // Input Panel (South)
        JPanel inputControlsPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputControlsPanel.add(inputField, BorderLayout.CENTER);
        inputControlsPanel.add(sendButton, BorderLayout.EAST);

        // Room Controls Panel (North)
        JPanel roomControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        leaveRoomButton = new JButton("Leave Room");
        exitButton = new JButton("Exit Application"); // New Exit Button
        leaveRoomButton.setEnabled(false); // Initially disabled

        createRoomButton.addActionListener(e -> createRoomAction());
        joinRoomButton.addActionListener(e -> joinRoomAction());
        leaveRoomButton.addActionListener(e -> leaveRoomAction());
        exitButton.addActionListener(e -> shutdownClient()); // Action for new Exit button

        roomControlsPanel.add(createRoomButton);
        roomControlsPanel.add(joinRoomButton);
        roomControlsPanel.add(leaveRoomButton);
        roomControlsPanel.add(exitButton);


        mainPanel.add(roomControlsPanel, BorderLayout.NORTH);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(inputControlsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupStyles() {
        // ... (style setup remains the same)
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        myStyle = doc.addStyle("MyStyle", defaultStyle); StyleConstants.setForeground(myStyle, new Color(0, 102, 204)); StyleConstants.setBold(myStyle, true);
        userStyle = doc.addStyle("UserStyle", defaultStyle); StyleConstants.setForeground(userStyle, Color.BLACK);
        serverStyle = doc.addStyle("ServerStyle", defaultStyle); StyleConstants.setForeground(serverStyle, new Color(100, 100, 100)); StyleConstants.setItalic(serverStyle, true);
        errorStyle = doc.addStyle("ErrorStyle", defaultStyle); StyleConstants.setForeground(errorStyle, Color.RED); StyleConstants.setBold(errorStyle, true);
        roomMessageStyle = doc.addStyle("RoomMessageStyle", defaultStyle); StyleConstants.setForeground(roomMessageStyle, new Color(0, 128, 0)); // Green for room messages
    }

    private void appendMessageToPane(String message, Style style) {
        SwingUtilities.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), message, style);
                chatPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) { /* ignore */ }
        });
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && out != null) {
            if (currentRoomName != null) {
                out.println("/roommsg " + currentRoomName + " " + message);
            } else {
                out.println(message); 
            }
            inputField.setText("");
        }
    }
    
    private void createRoomAction() {
        JTextField roomNameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Name:"));
        panel.add(roomNameField);
        panel.add(new JLabel("Password (optional for public room):"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String roomName = roomNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (!roomName.isEmpty()) {
                out.println("/createroom " + roomName + " " + (password.isEmpty() ? "NOPASSWORD" : password));
            } else {
                JOptionPane.showMessageDialog(this, "Room name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void joinRoomAction() {
        JTextField roomNameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Room Name:"));
        panel.add(roomNameField);
        panel.add(new JLabel("Password (if required):"));
        panel.add(passwordField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Join Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String roomName = roomNameField.getText().trim();
            String password = new String(passwordField.getPassword());
             if (!roomName.isEmpty()) {
                out.println("/joinroom " + roomName + " " + (password.isEmpty() ? "NOPASSWORD" : password));
            } else {
                JOptionPane.showMessageDialog(this, "Room name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void leaveRoomAction() {
        if (currentRoomName != null && out != null) {
            out.println("/leaveroom " + currentRoomName);
        }
    }

    private void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }
    
    private void updateRoomStatus(String roomName) {
        this.currentRoomName = roomName;
        if (roomName != null) {
            setTitle("Chat Application - " + loggedInUsername + " (Room: " + roomName + ")");
            leaveRoomButton.setEnabled(true);
            createRoomButton.setEnabled(false);
            joinRoomButton.setEnabled(false);
        } else {
            setTitle("Chat Application - " + loggedInUsername + " (Lobby)");
            leaveRoomButton.setEnabled(false);
            createRoomButton.setEnabled(true);
            joinRoomButton.setEnabled(true);
        }
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    private void shutdownClient() {
        appendMessageToPane("[SYSTEM] Disconnecting...\n", serverStyle);
        if (out != null) {
            out.println("/quit"); // Inform server about quitting
        }
        closeConnection();
        // A short delay to allow messages to be processed/displayed if needed
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        System.out.println("Client " + loggedInUsername + " is shutting down.");
        System.exit(0);
    }

    class MessageReceiver extends Thread {
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    if (serverMessage.startsWith("JOINED_ROOM ")) {
                        String room = serverMessage.substring("JOINED_ROOM ".length());
                        updateRoomStatus(room);
                        appendMessageToPane("[SYSTEM] You joined room: " + room + "\n", serverStyle);
                    } else if (serverMessage.startsWith("LEFT_ROOM")) {
                         appendMessageToPane("[SYSTEM] You left the room. Now in Lobby.\n", serverStyle);
                        updateRoomStatus(null);
                    } else if (serverMessage.startsWith("ROOM_MSG ")) {
                        // Format: ROOM_MSG <roomName> <sender>: <message>
                        String[] parts = serverMessage.split(" ", 3); // ROOM_MSG, roomName, rest
                        String room = parts[1];
                        String actualMessage = parts[2]; // <sender>: <message>
                        if (room.equals(currentRoomName)) {
                             appendMessageToPane(actualMessage + "\n", roomMessageStyle);
                        }
                    } else if (serverMessage.startsWith("ERROR ")) {
                        appendMessageToPane("[SERVER ERROR] " + serverMessage.substring("ERROR ".length()) + "\n", errorStyle);
                    } else if (serverMessage.startsWith("[SERVER]") || serverMessage.startsWith("[" + loggedInUsername + "]")) {
                        // Differentiate own messages if server echoes them back with username
                         if (serverMessage.startsWith("[" + loggedInUsername + "]")) {
                            appendMessageToPane(serverMessage + "\n", myStyle);
                        } else {
                            appendMessageToPane(serverMessage + "\n", serverStyle);
                        }
                    } else if (serverMessage.startsWith("/userslist ")) {
                        String[] users = serverMessage.substring("/userslist ".length()).split(",");
                        updateUserList(users);
                    }
                    else { // General messages or other user messages in lobby
                        appendMessageToPane(serverMessage + "\n", userStyle);
                    }
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    appendMessageToPane("[SYSTEM] Lost connection to server: " + e.getMessage() + "\n", errorStyle);
                }
            } finally {
                appendMessageToPane("[SYSTEM] Connection closed.\n", serverStyle);
                inputField.setEnabled(false);
                sendButton.setEnabled(false);
                createRoomButton.setEnabled(false);
                joinRoomButton.setEnabled(false);
                leaveRoomButton.setEnabled(false);
                
                 if (!socket.isClosed()) {
                    closeConnection();
                 }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}
