package app.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ChatServer {
    private static final int PORT = 1234;
    private static ExecutorService pool = Executors.newFixedThreadPool(50); // Increased pool size
    // Use ConcurrentHashMap for thread-safe operations on clients list
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static Map<String, Room> activeRooms = new ConcurrentHashMap<>();

    // Inner class for Room
    static class Room {
        String name;
        String password; // Plaintext for example, hash in production
        Set<ClientHandler> occupants = new CopyOnWriteArraySet<>(); // Thread-safe set for occupants

        Room(String name, String password) {
            this.name = name;
            this.password = (password == null || password.equals("NOPASSWORD") || password.isEmpty()) ? null : password;
        }

        void addOccupant(ClientHandler client) {
            occupants.add(client);
        }

        void removeOccupant(ClientHandler client) {
            occupants.remove(client);
        }

        boolean verifyPassword(String passToVerify) {
            if (this.password == null) return true; // Public room
            return this.password.equals(passToVerify);
        }

        void broadcastToRoom(String message, ClientHandler sender) {
            String fullMessage = "[" + sender.username + " in " + name + "]: " + message;
            if (sender.currentRoom != null && sender.currentRoom.name.equals(this.name)) {
                 // Send as ROOM_MSG for client-side parsing
                 String roomMessageProtocol = "ROOM_MSG " + this.name + " " + sender.username + ": " + message;
                for (ClientHandler occupant : occupants) {
                    occupant.sendMessage(roomMessageProtocol);
                }
            }
        }
         void notifyRoomMembers(String notification, ClientHandler exclude) {
            for (ClientHandler occupant : occupants) {
                if (occupant != exclude) {
                    occupant.sendMessage("ROOM_MSG " + this.name + " [SYSTEM@" + this.name + "]: " + notification);
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Chat Server started on port " + PORT);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientThread = new ClientHandler(clientSocket);
                // Don't add to 'clients' map until authenticated and username is set
                pool.execute(clientThread);
            }
        } finally {
            pool.shutdown();
            serverSocket.close();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private Room currentRoom = null;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            // Initialize out and in immediately
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        
        void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }


        public void run() {
            try {
                // Authentication
                this.username = in.readLine(); // Expect username first
                String password = in.readLine(); // Expect password second

                if (username == null || password == null || username.trim().isEmpty()) {
                    sendMessage("AUTH_FAIL_INVALID"); // Custom error for client
                    System.out.println("Authentication failed for " + socket.getRemoteSocketAddress() + ": Empty username/password.");
                    return; // End thread
                }
                
                // Simplified Authentication & Duplicate Username Check
                if (clients.containsKey(username)) {
                    sendMessage("AUTH_FAIL_USER_EXISTS");
                    System.out.println("Authentication failed for " + username + ": Already logged in.");
                    return;
                }


                if (authenticateUser(username, password)) { // Your actual authentication logic
                    clients.put(username, this); // Add to clients map *after* successful auth
                    sendMessage("AUTH_SUCCESS");
                    System.out.println(username + " authenticated and connected.");
                    broadcastGlobalMessage("[SERVER] " + username + " joined the chat.", this);
                    broadcastUserList();


                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Received from " + username + ": " + message);
                        if (message.startsWith("/")) {
                            handleCommand(message);
                        } else {
                            if (currentRoom != null) {
                                currentRoom.broadcastToRoom(message, this);
                            } else {
                                // Global message if not in a room
                                broadcastGlobalMessage("[" + username + "]: " + message, null); // Send to all, including self
                            }
                        }
                    }
                } else {
                    sendMessage("AUTH_FAIL");
                    System.out.println("Authentication failed for " + username);
                }
            } catch (IOException e) {
                if (username != null) {
                    System.out.println("Client " + username + " disconnected: " + e.getMessage());
                } else {
                     System.out.println("Client " + socket.getRemoteSocketAddress() + " disconnected before auth: " + e.getMessage());
                }
            } finally {
                cleanUpClient();
            }
        }
        
        private void cleanUpClient() {
            if (username != null) {
                if (currentRoom != null) {
                    handleLeaveRoom(currentRoom.name, false); // Silently leave room on disconnect
                }
                clients.remove(username);
                broadcastGlobalMessage("[SERVER] " + username + " left the chat.", null); // Inform others
                broadcastUserList(); // Update user list for everyone
                System.out.println(username + " has been removed from clients list.");
            }
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing client resources for " + username + ": " + ex.getMessage());
            }
        }

        private void handleCommand(String command) {
            String[] parts = command.split(" ", 3); // For commands like /createroom name pass
            String cmd = parts[0];

            switch (cmd) {
                case "/createroom":
                    if (parts.length >= 3) { // /createroom <roomName> <passwordOrNoPassword>
                        handleCreateRoom(parts[1], parts[2]);
                    } else if (parts.length == 2) { // /createroom <roomName> (implies public)
                         handleCreateRoom(parts[1], "NOPASSWORD");
                    }
                    else {
                        sendMessage("ERROR Invalid create room command. Use /createroom <roomName> [password]");
                    }
                    break;
                case "/joinroom":
                     if (parts.length >= 3) { // /joinroom <roomName> <passwordOrNoPassword>
                        handleJoinRoom(parts[1], parts[2]);
                    } else if (parts.length == 2) { // /joinroom <roomName> (implies public or trying without pass)
                        handleJoinRoom(parts[1], "NOPASSWORD");
                    }
                    else {
                        sendMessage("ERROR Invalid join room command. Use /joinroom <roomName> [password]");
                    }
                    break;
                case "/leaveroom":
                    if (parts.length >= 2) {
                        handleLeaveRoom(parts[1], true);
                    } else if (currentRoom != null) {
                        handleLeaveRoom(currentRoom.name, true);
                    }
                    break;
                case "/roommsg": // Format: /roommsg <roomName> <message>
                     if (parts.length >= 3 && currentRoom != null && currentRoom.name.equals(parts[1])) {
                        currentRoom.broadcastToRoom(parts[2], this);
                    } else if (currentRoom == null) {
                        sendMessage("ERROR You are not in a room to send a room message.");
                    } else if (currentRoom != null && !currentRoom.name.equals(parts[1])) {
                        sendMessage("ERROR You are trying to send to a different room than you are in.");
                    }
                    break;
                case "/quit":
                    // Client initiated quit, cleanup is handled in finally block
                    break;
                default:
                    sendMessage("ERROR Unknown command: " + cmd);
            }
        }

        private void handleCreateRoom(String roomName, String password) {
            if (currentRoom != null) {
                sendMessage("ERROR You are already in a room. Leave first to create another.");
                return;
            }
            if (activeRooms.containsKey(roomName)) {
                sendMessage("ERROR Room '" + roomName + "' already exists.");
                return;
            }
            Room newRoom = new Room(roomName, password);
            newRoom.addOccupant(this);
            activeRooms.put(roomName, newRoom);
            this.currentRoom = newRoom;
            sendMessage("JOINED_ROOM " + roomName); // Confirms creation and join
            System.out.println(username + " created and joined room: " + roomName);
             // Optionally notify global chat or just keep it within the room
            newRoom.notifyRoomMembers(username + " created and joined the room.", null);
        }

        private void handleJoinRoom(String roomName, String password) {
            if (currentRoom != null) {
                sendMessage("ERROR You are already in room '" + currentRoom.name + "'. Leave first.");
                return;
            }
            Room roomToJoin = activeRooms.get(roomName);
            if (roomToJoin == null) {
                sendMessage("ERROR Room '" + roomName + "' does not exist.");
                return;
            }
            if (!roomToJoin.verifyPassword(password)) {
                sendMessage("ERROR Incorrect password for room '" + roomName + "'.");
                return;
            }
            // Leave current room if any (should not happen due to check above, but defensive)
            if(this.currentRoom != null) {
                handleLeaveRoom(this.currentRoom.name, false);
            }

            roomToJoin.addOccupant(this);
            this.currentRoom = roomToJoin;
            sendMessage("JOINED_ROOM " + roomName);
            System.out.println(username + " joined room: " + roomName);
            roomToJoin.notifyRoomMembers(username + " joined the room.", this);
        }

        private void handleLeaveRoom(String roomName, boolean sendClientUpdate) {
            if (currentRoom == null || !currentRoom.name.equals(roomName)) {
                if(sendClientUpdate) sendMessage("ERROR You are not in room '" + roomName + "'.");
                return;
            }
            currentRoom.removeOccupant(this);
            currentRoom.notifyRoomMembers(username + " left the room.", null);
            
            System.out.println(username + " left room: " + roomName);
            if (currentRoom.occupants.isEmpty()) {
                activeRooms.remove(roomName); // Remove room if empty
                System.out.println("Room '" + roomName + "' is empty and has been removed.");
            }
            this.currentRoom = null;
            if(sendClientUpdate) sendMessage("LEFT_ROOM");
        }


        private boolean authenticateUser(String username, String password) {
            // Replace with your actual database/authentication logic
            // For this example, any non-empty password for a new user is fine.
            return password != null && !password.isEmpty();
        }
    }

    // Broadcast to all clients NOT in a room (lobby) or system-wide if exclude is null
    private static void broadcastGlobalMessage(String message, ClientHandler exclude) {
        clients.values().forEach(client -> {
            if (client != exclude && client.currentRoom == null) { // Only send to clients in lobby
                client.sendMessage(message);
            }
        });
         // If it's a server message (exclude is null or it's a general announcement)
        if (exclude == null) { // Typically for server wide announcements
            clients.values().forEach(client -> client.sendMessage(message));
        }
    }
    
    private static void broadcastUserList() {
        String userListStr = clients.keySet().stream().collect(Collectors.joining(","));
        clients.values().forEach(client -> client.sendMessage("/userslist " + userListStr));
    }
}
