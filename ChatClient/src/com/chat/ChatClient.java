package com.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;
    private Message message;


    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }


    public void msg(String sendTo, String msgBody) throws IOException {

        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        Message message=new Message(cmd);
        out.writeObject(message);
        out.flush();
    }

   public boolean login(String login, String password) throws IOException {

        String cmd = "login " + login + " " + password + "\n";
        Message message=new Message(cmd);
        out.writeObject(message);
        out.flush();


            ClientReader clientReader=new ClientReader(in,socket,messageListeners,userStatusListeners);
            clientReader.start();
            return true;

    }

   public void logoff() throws IOException {

        String cmd = "logoff";
        Message msg=new Message(cmd);
        out.writeObject(msg);
        out.flush();
    }
    public void leave(String topic) throws IOException {

        String cmd = "leave #"+ topic+"\n";
        Message msg=new Message(cmd);
        out.writeObject(msg);
        out.flush();
    }
    public void addTopic(String string) throws IOException {

        String cmd = "join #" + string +"\n";
        Message msg=new Message(cmd);
        out.writeObject(msg);
        out.flush();
    }


    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.out=new ObjectOutputStream(serverOut);
            this.serverIn = socket.getInputStream();
            this.in=new ObjectInputStream(serverIn);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

}
