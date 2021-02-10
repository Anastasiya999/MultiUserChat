package com.chat;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientReader  extends Thread{

    private final Socket socket;
    private final ArrayList<MessageListener> messageListeners;
    private final ArrayList<UserStatusListener> userStatusListeners;
    private  ObjectInputStream in;

    public ClientReader(ObjectInputStream in, Socket socket, ArrayList<MessageListener>messageListeners,ArrayList<UserStatusListener>userStatusListeners) {
        this.socket=socket;
        this.messageListeners=messageListeners;
        this.userStatusListeners=userStatusListeners;
        this.in=in;
    }
    @Override
    public void run() {
        readMessageLoop();

    }

    private void readMessageLoop(){
        try {
        String line;
        while (true) {

            Message message=(Message)in.readObject();

            line=message.toString();
            System.out.println(line);
            String[] tokens = StringUtils.split(line);
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("online".equalsIgnoreCase(cmd)) {
                    handleOnline(tokens);
                } else if ("offline".equalsIgnoreCase(cmd)) {
                    handleOffline(tokens);
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                }else if ("join".equalsIgnoreCase(cmd)) {
                handleJoin(tokens);
            } else if ("leave".equalsIgnoreCase(cmd)) {
                handleLeave(tokens);
            }
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    private void handleLeave(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleJoin(String[] tokens) {
        String topic = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(topic);
        }
    }
    private void handleMessage(String[] tokensMsg) {

        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {

                listener.onMsg(login, msgBody);

        }
    }
   private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }


}
