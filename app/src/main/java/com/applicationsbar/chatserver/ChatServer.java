package com.applicationsbar.chatserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static com.applicationsbar.chatserver.ChatServer.GetRoomPlayer;
import static com.applicationsbar.chatserver.ChatServer.chatRooms;
import static com.applicationsbar.chatserver.ChatServer.drawingPlayersList;
import static com.applicationsbar.chatserver.ChatServer.isExistChatRoom;
import static com.applicationsbar.chatserver.ChatServer.sendByteArrayMessage;
import static com.applicationsbar.chatserver.ChatServer.sendLoginFailedMessage;
import static com.applicationsbar.chatserver.ChatServer.sendStringMessage;
import static java.lang.System.out;

public class ChatServer {

    /*
     * A chat server that delivers public and private messages.
     */


    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.


    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10; //max number of clients to accept
    private static final clientThread[] threads = new clientThread[maxClientsCount];//list of the client's threads
    public static final RandomWord word = new RandomWord();//a new RandomWord object
    static ArrayList<DrawingPlayer> drawingPlayersList = new ArrayList<DrawingPlayer>();
    static ArrayList<Integer> chatRooms = new ArrayList<Integer>();

    static Boolean isExistChatRoom(int chatRoom)
    {
        return ChatServer.chatRooms.contains(chatRoom);
    }

    static DrawingPlayer GetRoomPlayer(int chatRoom)
    {
        int index = 0;

        for (int i = drawingPlayersList.size()-1 ; i >= 0; i--)
        {
            if (drawingPlayersList.get(i).GetChatRoom() == chatRoom)
            {
                index = i;
                break;
            }
        }
        return drawingPlayersList.get(index);
    }

    static void sendByteArrayMessage(PrintStream os, byte[] ba) {


        try {

            byte currentByte = 2;
            os.write(currentByte);
            int len = ba.length;

            for (int i = 3; i >= 0; i--) {
                currentByte = (byte) ((len >> (i * 8)) % 256);
                os.write(currentByte);
            }

            os.write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendStringMessage(PrintStream os, String s) {
        byte[] ba = s.getBytes();
        try {

            byte currentByte = 1;
            os.write(currentByte);
            int len = ba.length;

            for (int i = 3; i >= 0; i--) {
                currentByte = (byte) ((len >> (i * 8)) % 256);
                os.write(currentByte);
            }

            os.write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendLoginFailedMessage(PrintStream os, String s) {

        byte[] ba = s.getBytes();
        try {

            byte currentByte = 3;
            os.write(currentByte);
            int len = ba.length;

            for (int i = 3; i >= 0; i--) {
                currentByte = (byte) ((len >> (i * 8)) % 256);
                os.write(currentByte);
            }

            os.write(ba);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Socket clientSocket;
        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]);
        }

        /*
         * Open a server socket on the portNumber (default 2222). Note that we can
         * not choose a port less than 1023 if we are not privileged users (root).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
           e.printStackTrace();
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i;
                System.out.println("New connection");
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {

                        (threads[i] = new clientThread(clientSocket, threads,i)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    sendLoginFailedMessage(os,"Server too busy. Try later.");
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private User u = null;
    private Socket tClientSocket;
    private final clientThread[] threads;
    private int maxClientsCount;
    private int index;
    private int chatRoom;
    private DrawingPlayer drawingPlayer;
    clientThread(Socket clientSocket, clientThread[] threads, int index) {
        this.tClientSocket = clientSocket;
        this.threads = threads;
        this.index = index;
        maxClientsCount = threads.length;

    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;
        String username= "";
        String password = "";
        String firstName = "";
        String lastName;
        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(tClientSocket.getInputStream());
            os = new PrintStream(tClientSocket.getOutputStream());
            String joinString = is.readLine().trim();
            System.out.println("Input String:"+joinString);
            String joinType = joinString.split("&")[0];
            if (joinType.equals("signUp"))
            {
                password = joinString.split("&")[1].split("=")[1];
                username = joinString.split("&")[2].split("=")[1];
                firstName = joinString.split("&")[3].split("=")[1];
                lastName = joinString.split("&")[4].split("=")[1];
                User.addUser(firstName,lastName,username,password);
                sendStringMessage(os, "Signed Up Successfully");
            }
            if (joinType.equals("login")) {
                password = joinString.split("&")[1].split("=")[1];
                username = joinString.split("&")[2].split("=")[1];
                this.chatRoom = Integer.parseInt(joinString.split("&")[3].split("=")[1]);
            }
            this.u = User.getUser(username, password);
            if (this.u!=null && joinType.equals("login")) {
                String name=u.firstName+" "+u.lastName;
                /* Welcome the new the client. */
                String welcome = "Welcome '" + name +"' to our chat room #"+this.chatRoom;
                if (!isExistChatRoom(this.chatRoom))
                {
                    chatRooms.add(this.chatRoom);
                    drawingPlayersList.add(new DrawingPlayer(chatRoom));
                }
                if (GetRoomPlayer(this.chatRoom).GetName()!= null)
                {
                    final String drawing = "'" + ChatServer.GetRoomPlayer(this.chatRoom).GetName() + "' is now drawing...";
                    sendStringMessage(os,welcome);
                    out.println("sent: "+welcome+" -to "+name);
                    Thread.sleep(2000);
                    sendStringMessage(os,drawing);
                    out.println("sent: "+drawing+" -to "+name);
                }
                else {
                    sendStringMessage(os,welcome);
                    out.println("sent: "+welcome+" -to "+name);
                    Thread.sleep(2000); //let the client complete the intent change
                }

                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] == this) {
                            clientName = "@" + name;
                            break;
                        }
                    }
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this && threads[i].chatRoom == this.chatRoom) {
                            sendStringMessage(threads[i].os,"*** A new user " + name
                                    + " entered the chat room !!! ***");
                            out.println("sent: "+"*** A new user " + name
                                    + " entered the chat room !!! ***" + " -to pl."+i);
                        }
                    }
                }
                /* Start the conversation. */
                while (true) {
                    synchronized (this) {
                        this.drawingPlayer = GetRoomPlayer(this.chatRoom);
                        String keyword = drawingPlayer.GetKeyword();
                        if ( GetRoomPlayer(this.chatRoom).GetName()== null) {
                            sendStringMessage(os, "It's your turn to draw: " + keyword + "!");
                            out.println("sent: "+"It's your turn to draw: " + keyword + "!"+" -to "+u.firstName+" "+u.lastName);
                            drawingPlayer.SetName(name);
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i].clientName != null && threads[i].chatRoom == this.chatRoom) {
                                    sendStringMessage(threads[i].os, "'" +drawingPlayer.GetName() + "' " + "is now drawing...");
                                    out.println("sent: "+"'" + drawingPlayer.GetName() + "' " + "is now drawing..."+" -to pl."+i);
                                }
                            }
                        }
                    }


                    int messageType = is.read();
                    if (messageType == -1)
                        break;
                    int messageLength = 0;
                    for (int i = 0; i < 4; i++) {

                        int j = is.read();
                        messageLength = messageLength * 256 + j;
                    }
                    byte[] message = new byte[messageLength];
                    for (int i = 0; i < messageLength; i++) {
                        message[i] = (byte) is.read();
                    }

                    if (messageType == 1) {
                        String line = new String(message);
                        if (line.contains("*** Bye"))
                            break;
                        if (line.startsWith("/quit")) {
                            break;

                        }
                        if (line.startsWith("get leaders")) {
                            //build message
                            String msg = User.getTopUsers();
                            //send message
                            sendStringMessage(os, "leaders:"+msg);
                        }
                        else

                        /* If the message is private sent it to the given client. */
                        if (line.startsWith("@")) {
                            String[] words = line.split("\\s", 2);
                            if (words.length > 1 && words[1] != null) {
                                words[1] = words[1].trim();
                                if (!words[1].isEmpty()) {
                                    synchronized (this) {
                                        for (int i = 0; i < maxClientsCount; i++) {
                                            if (threads[i] != null && threads[i] != this
                                                    && threads[i].clientName != null
                                                    && threads[i].clientName.equals(words[0])) {

                                                sendStringMessage(threads[i].os, ">" + name + "> " + words[1]);
                                                /*
                                                 * Echo this message to let the client know the private
                                                 * message was sent.
                                                 */
                                                sendStringMessage(os, ">" + name + "> " + words[1]);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else {
                            /* The message is public, broadcast it to all other clients. */

                            synchronized (this) {
                                if (line.toLowerCase().contains(drawingPlayer.GetKeyword().toLowerCase()) && !name.equals(drawingPlayer.GetName()))
                                {
                                    u.addPoints();
                                    String keyword = drawingPlayer.GetKeyword();

                                    for (int i = 0; i < maxClientsCount; i++) {

                                        if (threads[i] != null && threads[i].clientName != null && threads[i].chatRoom == this.chatRoom) {
                                            sendStringMessage(threads[i].os, "'" + name + "' " + "guessed "+"'"+keyword+"'"+" correctly!!!");
                                            out.println("sent: "+"'" + name + "' " + "guessed "+"'"+keyword+"'"+" correctly!!!"+" -to pl."+i);
                                            drawingPlayer.SetName(null);
                                            drawingPlayer.SetKeyword(ChatServer.word.GetNewWord());
                                        }
                                    }
                                }
                                else {
                                    for (int i = 0; i < maxClientsCount; i++) {
                                        if (threads[i] != null && threads[i].clientName != null && threads[i].chatRoom == this.chatRoom) {
                                            sendStringMessage(threads[i].os, ">" + name + "> " + line);
                                            out.println("sent: "+">" + name + "> " + line+" -to pl."+i);

                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        synchronized (this) {
                            for (int i = 0; i < maxClientsCount; i++) {
                                if (threads[i] != null && threads[i].clientName != null && threads[i].chatRoom == this.chatRoom) {
                                    sendByteArrayMessage(threads[i].os, message);
                                    out.println("sent image array to pl."+i);

                                }
                            }
                        }

                    }

                }
                synchronized (this) {
                    if(name.equals(drawingPlayer.GetName()))
                        drawingPlayer.SetName(null);
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this
                                && threads[i].clientName != null && threads[i].chatRoom == this.chatRoom) {
                            sendStringMessage(threads[i].os,"*** The user " + name
                                    + " is leaving the chat room !!! ***");
                            out.println("sent: "+"*** The user " + name
                                    + " is leaving the chat room !!! ***"+" -to pl."+i);

                        }
                    }
                }
                //  os.println("*** Bye " + name + " ***");

                /*
                 * Clean up. Set the current thread variable to null so that a new client
                 * could be accepted by the server.
                 */
            }
            else {
                if (joinType.equals("login"))
                {
                sendLoginFailedMessage(this.os,"Login failed");
                }

            }

            /*
             * Close the output stream, close the input stream, close the socket.
             */
            this.is.close();
            this.os.close();
            this.tClientSocket.close();
            out.println("Socket Closed"+this.tClientSocket.isClosed());
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
        } catch (Exception e) {
            try {
                if (is!=null) is.close();
                if (os!=null) os.close();
                if (tClientSocket!=null) tClientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


   

    
}



